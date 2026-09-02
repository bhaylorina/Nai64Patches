package patches.universal.misc

import app.morphe.patcher.extensions.InstructionExtensions.addInstructions
import app.morphe.patcher.patch.bytecodePatch
import java.util.logging.Logger

@Suppress("unused")
val triggerFGSPatch = bytecodePatch(
    name = "Trigger Immortal FGS",
    description = "Starts FGS safely",
    default = false,
) {
    execute {
        var patched = false
        classDefForEach { c ->
            val isApp = c.superclass == 
                "Landroid/app/Application;" || 
                c.superclass == 
                "Landroidx/multidex/MultiDexApplication;"

            if (isApp) {
                val mClass = mutableClassDefBy(c)
                val onC = mClass.methods.find { 
                    it.name == "onCreate" 
                }

                if (onC != null && onC.implementation != null) {
                    val smali = "invoke-static " +
                        "{p0}, Lapp/morphe/" +
                        "patches/ServiceStarter;" +
                        "->start(Landroid/content/" +
                        "Context;)V"

                    onC.addInstructions(0, smali)
                    patched = true
                }
            }
        }
        if (patched) Logger.getLogger(
            this::class.java.name
        ).info("FGS Triggered")
    }
}

