package patches.universal.misc

import app.morphe.patcher.extensions.InstructionExtensions.addInstructions
import app.morphe.patcher.patch.bytecodePatch
import java.util.logging.Logger

@Suppress("unused")
val triggerFGSPatch = bytecodePatch(
    name = "Trigger Immortal FGS",
    description = "Safe Carpet Bomb Hook for Activities",
    default = false,
) {
    execute {
        val log = Logger.getLogger(this::class.java.name)
        var count = 0

        // Only target concrete Activity base classes to prevent Patcher NPE
        val validSuperclasses = listOf(
            "Landroidx/appcompat/app/AppCompatActivity;",
            "Landroidx/activity/ComponentActivity;",
            "Landroidx/fragment/app/FragmentActivity;",
            "Landroid/app/Activity;"
        )

        classDefForEach { c ->
            if (c.superclass in validSuperclasses || c.type.contains("MainActivity")) {
                val mClass = mutableClassDefBy(c)
                
                // Specifically hook onResume so it triggers when screen is visible
                val targetMethod = mClass.methods.find { 
                    it.name == "onResume" 
                }

                if (targetMethod != null && targetMethod.implementation != null) {
                    try {
                        val smali = "invoke-static {p0}, Lapp/morphe/" +
                            "patches/KeepAliveService;" +
                            "->start(Landroid/content/Context;)V"
                        
                        targetMethod.addInstructions(0, smali)
                        count++
                    } catch (e: Exception) {
                        // Safely ignore any compiler quirks on obscure classes
                    }
                }
            }
        }
        log.info("Safe Carpet Bomb Hit: Hooked $count screens!")
    }
}
