package patches.universal.misc

import app.morphe.patcher.extensions.InstructionExtensions.addInstructions
import app.morphe.patcher.patch.bytecodePatch
import java.util.logging.Logger

@Suppress("unused")
val triggerForegroundServicePatch = bytecodePatch(
    name = "Trigger Active Service",
    description = "Hooks into the app to start the active timer service on launch.",
    default = false,
) {
    execute {
        val logger = Logger.getLogger(this::class.java.name)
        var patched = false

        classDefForEach { classDef ->
            // Find the main Application class that runs immediately when Twitter opens
            if (classDef.superclass == "Landroid/app/Application;" || classDef.superclass == "Landroidx/multidex/MultiDexApplication;") {
                val onCreate = classDef.methods.find { it.name == "onCreate" }
                if (onCreate != null && onCreate.implementation != null) {
                    // Inject a single line of smali to call our Java helper
                    onCreate.addInstructions(0, """
                        invoke-static {p0}, Lcom/twitter/android/ServiceStarter;->start(Landroid/content/Context;)V
                    """.trimIndent())
                    patched = true
                }
            }
        }

        if (patched) logger.info("Successfully injected service trigger")
        else logger.warning("Could not find Application class to inject trigger")
    }
}

