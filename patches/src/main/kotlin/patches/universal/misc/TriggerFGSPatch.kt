package patches.universal.misc

import app.morphe.patcher.extensions.InstructionExtensions.addInstructions
import app.morphe.patcher.patch.bytecodePatch
import java.util.logging.Logger

@Suppress("unused")
val triggerFGSPatch = bytecodePatch(
    name = "Trigger Immortal FGS",
    description = "Protects code from Shrinker",
    default = false,
) {
    execute {
        val log = Logger.getLogger(this::class.java.name)
        var count = 0

        val validSuperclasses = listOf(
            "Landroidx/appcompat/app/AppCompatActivity;",
            "Landroidx/activity/ComponentActivity;",
            "Landroidx/fragment/app/FragmentActivity;",
            "Landroid/app/Activity;"
        )

        classDefForEach { c ->
            if (c.superclass in validSuperclasses) {
                val mClass = mutableClassDefBy(c)
                val targetMethod = mClass.methods.find { it.name == "onResume" }

                if (targetMethod != null && targetMethod.implementation != null) {
                    try {
                        // FAKE CALL: Only exists to keep our Java files in the APK
                        val smali = "invoke-static {}, Lapp/morphe/patches/KeepAliveInitProvider;->anchor()V"
                        targetMethod.addInstructions(0, smali)
                        count++
                    } catch (e: Exception) {}
                }
            }
        }
        log.info("Anchor Hit: Protected across $count screens!")
    }
}

