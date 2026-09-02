package patches.universal.misc

import app.morphe.patcher.extensions.InstructionExtensions.addInstructions
import app.morphe.patcher.patch.bytecodePatch
import java.util.logging.Logger

@Suppress("unused")
val triggerForegroundServicePatch = bytecodePatch(
    name = "Trigger Active Service",
    description = "Hooks into the app to start the universal active timer service on launch.",
    default = false,
) {
    execute {
        val logger = Logger.getLogger(this::class.java.name)
        var patched = false

        classDefForEach { classDef ->
            if (classDef.superclass == "Landroid/app/Application;" || classDef.superclass == "Landroidx/multidex/MultiDexApplication;") {
                val onCreate = classDef.methods.find { it.name == "onCreate" }
                if (onCreate != null && onCreate.implementation != null) {
                    onCreate.addInstructions(0, """
                        invoke-static {p0}, Lapp/morphe/patches/keepalive/ServiceStarter;->start(Landroid/content/Context;)V
                    """.trimIndent())
                    patched = true
                }
            }
        }

        if (patched) logger.info("Successfully injected universal service trigger")
        else logger.warning("Could not find Application class to inject trigger")
    }
}

