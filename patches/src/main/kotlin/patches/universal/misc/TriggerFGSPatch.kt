package patches.universal.misc

import app.morphe.patcher.extensions.InstructionExtensions.addInstructions
import app.morphe.patcher.patch.bytecodePatch
import java.util.logging.Logger

@Suppress("unused")
val triggerFGSPatch = bytecodePatch(
    name = "Trigger Immortal FGS",
    description = "Starts FGS safely from Activity",
    default = false,
) {
    execute {
        val log = Logger.getLogger(
            this::class.java.name
        )
        var patched = false

        classDefForEach { c ->
            // Hooking into Activity ensures MultiDex 
            // is fully loaded before our code runs!
            val targets = listOf(
                "Landroidx/activity/" +
                "ComponentActivity;",
                "Landroidx/appcompat/app/" +
                "AppCompatActivity;"
            )

            if (c.type in targets) {
                val mClass = mutableClassDefBy(c)
                val onC = mClass.methods.find { 
                    it.name == "onCreate" 
                }

                if (onC != null && 
                    onC.implementation != null) {
                    
                    val smali = "invoke-static " +
                        "{p0}, Lapp/morphe/" +
                        "patches/KeepAliveService;" +
                        "->start(Landroid/content/" +
                        "Context;)V"

                    onC.addInstructions(0, smali)
                    patched = true
                }
            }
        }
        if (patched) log.info("FGS Hooked!")
    }
}
