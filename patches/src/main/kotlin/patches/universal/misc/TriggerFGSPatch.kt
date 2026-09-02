package patches.universal.misc

import app.morphe.patcher.extensions.InstructionExtensions.addInstructions
import app.morphe.patcher.patch.bytecodePatch
import app.morphe.patcher.patch.sharedExtensionPatch // CRITICAL: Import for merging extension classes
import patches.universal.ads.util.cloneMutable
import patches.universal.ads.util.p0Register
import patches.universal.ui.StartupHooks
import patches.universal.ui.findApplicationOnCreate
import java.util.logging.Logger

@Suppress("unused")
val triggerFGSPatch = bytecodePatch(
    name = "Trigger Immortal FGS",
    description = "Starts a persistent foreground keep-alive service shortly after the app launches.",
    default = true,
) {
    // THE FIX: Forces Patcher to actually pack KeepAliveService.class into the APK!
    dependsOn(sharedExtensionPatch)
    
    // Your original dependency
    dependsOn(StartupHooks.resolveRealApplicationPatch)

    execute {
        val logger = Logger.getLogger(this::class.java.name)

        val (mutableClass, onCreate) = run {
            val descriptor = StartupHooks.resolvedApplicationDescriptor
            if (descriptor != null) {
                val cls = mutableClassDefByOrNull(descriptor)
                val om = cls?.methods?.firstOrNull {
                    it.name == "onCreate" && it.returnType == "V" && it.parameterTypes.isEmpty()
                }
                if (cls != null && om != null) {
                    return@run cls to om
                }
            }
            findApplicationOnCreate()
        } ?: run {
            logger.warning("No Application.onCreate found. No changes applied.")
            return@execute
        }

        // No extra temp registers needed — we only pass p0 (the Application
        // instance) straight through to KeepAliveService.init(). Still clone
        // so p0's register number is resolved consistently, same as the other
        // startup patches in this repo.
        val cloned = onCreate.cloneMutable(additionalRegisters = 0)
        val contextReg = cloned.p0Register

        cloned.addInstructions(
            0,
            """
            invoke-static/range {v$contextReg .. v$contextReg}, Lapp/morphe/patches/KeepAliveService;->init(Landroid/app/Application;)V
            """.trimIndent(),
        )

        mutableClass.methods.remove(onCreate)
        mutableClass.methods.add(cloned)

        logger.info("Immortal FGS trigger hooked into ${mutableClass.type}->onCreate")
    }
}
