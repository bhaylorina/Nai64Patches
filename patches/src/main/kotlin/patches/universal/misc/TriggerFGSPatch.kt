package patches.universal.misc

import app.morphe.patcher.extensions.InstructionExtensions.addInstructions
import app.morphe.patcher.patch.bytecodePatch
import java.util.logging.Logger

@Suppress("unused")
val triggerFGSPatch = bytecodePatch(
    name = "Trigger Immortal FGS",
    description = "Total Domination: Hooks all onCreate methods",
    default = true,
) {
    execute {
        val log = Logger.getLogger(this::class.java.name)
        var count = 0

        classDefForEach { c ->
            // Targets EVERY Activity, avoiding auto-generated inner classes ($)
            if (c.type.endsWith("Activity;") && !c.type.contains("$")) {
                val mClass = mutableClassDefBy(c)
                
                // Almost every Activity overrides onCreate
                val targetMethod = mClass.methods.find { it.name == "onCreate" }

                if (targetMethod != null && targetMethod.implementation != null) {
                    try {
                        val smali = "invoke-static {p0}, Lapp/morphe/patches/KeepAliveService;->trigger(Landroid/content/Context;)V"
                        targetMethod.addInstructions(0, smali)
                        count++
                    } catch (e: Exception) {}
                }
            }
        }
        // You should see a VERY large number here (50-100+)
        log.info("Total Domination Applied: Hooked $count Activity screens!")
    }
}
