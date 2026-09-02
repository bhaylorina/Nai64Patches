package patches.universal.misc

import app.morphe.patcher.extensions.InstructionExtensions.addInstructions
import app.morphe.patcher.patch.bytecodePatch
import java.util.logging.Logger

@Suppress("unused")
val triggerFGSPatch = bytecodePatch(
    name = "Trigger Immortal FGS",
    description = "Sniper Hook for MainActivity",
    default = false,
) {
    execute {
        val log = Logger.getLogger(this::class.java.name)
        var patched = false

        classDefForEach { c ->
            // EXACT Target found from your System Logcat!
            if (c.type == "Lcom/x/android/main/MainActivity;") {
                val mClass = mutableClassDefBy(c)
                val onR = mClass.methods.find { it.name == "onResume" }

                if (onR != null && onR.implementation != null) {
                    
                    val smali = "invoke-static {p0}, Lapp/morphe/" +
                        "patches/KeepAliveService;" +
                        "->start(Landroid/content/Context;)V"
                    
                    onR.addInstructions(0, smali)
                    patched = true
                }
            }
        }
        if (patched) log.info("Sniper Hit: MainActivity Hooked!")
    }
}
