package patches.universal.misc

import app.morphe.patcher.extensions.InstructionExtensions.addInstructions
import app.morphe.patcher.patch.bytecodePatch
import java.util.logging.Logger

@Suppress("unused")
val triggerFGSPatch = bytecodePatch(
    name = "Trigger Immortal FGS",
    description = "Global Application Hook via Service",
    default = false,
) {
    execute {
        val log = Logger.getLogger(this::class.java.name)
        var patched = false

        classDefForEach { c ->
            val isApp = c.superclass == "Landroid/app/Application;" || 
                        c.superclass == "Landroidx/multidex/MultiDexApplication;"

            if (isApp) {
                val mClass = mutableClassDefBy(c)
                val onC = mClass.methods.find { it.name == "onCreate" }

                if (onC != null && onC.implementation != null) {
                    // Pointing directly to KeepAliveService now
                    val smali = "invoke-static {p0}, Lapp/morphe/patches/KeepAliveService;->init(Landroid/app/Application;)V"
                    
                    val insts = onC.implementation!!.instructions
                    val retIdx = insts.indexOfLast { it.opcode.name == "return-void" }
                    
                    if (retIdx != -1) {
                        onC.addInstructions(retIdx, smali)
                        patched = true
                    }
                }
            }
        }
        if (patched) log.info("Global App Hook Injected Successfully!")
    }
}
