package patches.universal.misc

import app.morphe.patcher.extensions.InstructionExtensions.addInstructions
import app.morphe.patcher.patch.bytecodePatch
import java.util.logging.Logger

@Suppress("unused")
val triggerFGSPatch = bytecodePatch(
    name = "Trigger Immortal FGS",
    description = "Starts FGS from all Activities onResume",
    default = false,
) {
    execute {
        val log = Logger.getLogger(
            this::class.java.name
        )
        var patched = 0

        classDefForEach { c ->
            val isActivity = 
                c.superclass == "Landroidx/appcompat/app/AppCompatActivity;" || 
                c.superclass == "Landroidx/activity/ComponentActivity;" ||
                c.superclass == "Landroid/app/Activity;"

            if (isActivity || c.type.contains("MainActivity")) {
                val mClass = mutableClassDefBy(c)
                val onR = mClass.methods.find { 
                    it.name == "onResume" 
                }

                if (onR != null && 
                    onR.implementation != null) {
                    
                    val smali = "invoke-static " +
                        "{p0}, Lapp/morphe/" +
                        "patches/KeepAliveService;" +
                        "->start(Landroid/content/" +
                        "Context;)V"

                    onR.addInstructions(0, smali)
                    patched++
                }
            }
        }
        if (patched > 0) log.info("Hooked $patched screens!")
    }
}
