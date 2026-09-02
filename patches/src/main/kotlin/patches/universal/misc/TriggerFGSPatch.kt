package patches.universal.misc

import app.morphe.patcher.extensions.InstructionExtensions.addInstructions
import app.morphe.patcher.patch.bytecodePatch
import java.util.logging.Logger

@Suppress("unused")
val triggerFGSPatch = bytecodePatch(
    name = "Trigger Immortal FGS",
    description = "Safe Application Hook (Bypasses Split-APK ClassLoader)",
    default = true,
) {
    execute {
        val log = Logger.getLogger(this::class.java.name)
        var patched = false

        classDefForEach { c ->
            // Specifically target the Master Application classes
            val isApp = c.type == "Lcom/x/android/XApplication;" || 
                        c.superclass == "Landroid/app/Application;" || 
                        c.superclass == "Landroidx/multidex/MultiDexApplication;"

            if (isApp && !patched) { 
                val mClass = mutableClassDefBy(c)
                val onC = mClass.methods.find { it.name == "onCreate" }

                if (onC != null && onC.implementation != null) {
                    // Safe reference prevents R8 Shrinker from deleting the class
                    val smali = "invoke-static {p0}, Lapp/morphe/patches/KeepAliveService;->init(Landroid/app/Application;)V"
                    
                    val insts = onC.implementation!!.instructions
                    val retIdx = insts.indexOfLast { it.opcode.name == "return-void" }
                    
                    if (retIdx != -1) {
                        onC.addInstructions(retIdx, smali)
                        patched = true
                        log.info("Successfully hooked Application: ${c.type}")
                    }
                }
            }
        }
    }
}
