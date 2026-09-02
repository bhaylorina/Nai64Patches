package patches.universal.misc

import app.morphe.patcher.extensions.InstructionExtensions.addInstructions
import app.morphe.patcher.patch.bytecodePatch
import java.util.logging.Logger

@Suppress("unused")
val triggerFGSPatch = bytecodePatch(
    name = "Trigger Immortal FGS",
    description = "Injects delayed FGS trigger into Activity.onResume",
    default = true,
) {
    execute {
        val log = Logger.getLogger(this::class.java.name)
        var count = 0

        // Safely target base activities to prevent Patcher NPEs
        val targets = listOf(
            "Landroidx/appcompat/app/AppCompatActivity;",
            "Landroidx/activity/ComponentActivity;",
            "Landroidx/fragment/app/FragmentActivity;",
            "Landroid/app/Activity;"
        )

        classDefForEach { c ->
            if (c.superclass in targets || c.type.contains("MainActivity")) {
                val mClass = mutableClassDefBy(c)
                val onR = mClass.methods.find { it.name == "onResume" }

                if (onR != null && onR.implementation != null) {
                    try {
                        // This explicit smali call proves to R8 that KeepAliveService is USED
                        val smali = "invoke-static {p0}, Lapp/morphe/patches/KeepAliveService;->trigger(Landroid/content/Context;)V"
                        onR.addInstructions(0, smali)
                        count++
                    } catch (e: Exception) {}
                }
            }
        }
        log.info("Deep Research Hook Applied: Infected $count screens!")
    }
}
