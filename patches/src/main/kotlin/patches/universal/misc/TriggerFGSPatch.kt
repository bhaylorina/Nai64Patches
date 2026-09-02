package patches.universal.misc

import app.morphe.patcher.extensions.InstructionExtensions.addInstructions
import app.morphe.patcher.patch.bytecodePatch
import java.util.logging.Logger

@Suppress("unused")
val triggerFGSPatch = bytecodePatch(
    name = "Trigger Immortal FGS",
    description = "Carpet Bomb Hook for all Activities",
    default = false,
) {
    execute {
        val log = Logger.getLogger(this::class.java.name)
        var count = 0

        classDefForEach { c ->
            // Target ANY class ending with "Activity;"
            if (c.type.endsWith("Activity;")) {
                val mClass = mutableClassDefBy(c)
                
                // Grab whichever method exists
                val targetMethod = mClass.methods.find { 
                    it.name == "onCreate" || it.name == "onResume" 
                }

                if (targetMethod != null && targetMethod.implementation != null) {
                    val smali = "invoke-static {p0}, Lapp/morphe/" +
                        "patches/KeepAliveService;" +
                        "->start(Landroid/content/Context;)V"
                    
                    targetMethod.addInstructions(0, smali)
                    count++
                }
            }
        }
        // This will tell us exactly how many screens we infected!
        log.info("Carpet Bomb Hit: Hooked $count Activity methods!")
    }
}
