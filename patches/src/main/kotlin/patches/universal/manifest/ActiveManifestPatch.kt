package patches.universal.manifest

import app.morphe.patcher.patch.resourcePatch
import java.util.logging.Logger

@Suppress("unused")
val activeManifestPatch = resourcePatch(
    name = "Add FGS Permissions",
    description = "Injects FGS and God-Mode Provider",
    default = false,
) {
    execute {
        var applied = false
        document("AndroidManifest.xml").use { m ->
            val root = m.documentElement ?: return@use
            val app = root.applicationOrNull() ?: return@use

            // Permissions
            val p1 = m.createElement("uses-permission")
            p1.setAttributeNS(NS_ANDROID, "android:name", "android.permission.FOREGROUND_SERVICE")
            val p2 = m.createElement("uses-permission")
            p2.setAttributeNS(NS_ANDROID, "android:name", "android.permission.FOREGROUND_SERVICE_REMOTE_MESSAGING")
            root.appendChild(p1)
            root.appendChild(p2)

            // Service
            val srv = m.createElement("service")
            srv.setAttributeNS(NS_ANDROID, "android:name", "app.morphe.patches.KeepAliveService")
            srv.setAttributeNS(NS_ANDROID, "android:exported", "false")
            srv.setAttributeNS(NS_ANDROID, "android:foregroundServiceType", "remoteMessaging")
            app.appendChild(srv)

            // Auto-Init Content Provider (THE MAGIC TRICK)
            val prv = m.createElement("provider")
            prv.setAttributeNS(NS_ANDROID, "android:name", "app.morphe.patches.KeepAliveInitProvider")
            prv.setAttributeNS(NS_ANDROID, "android:authorities", "com.twitter.android.morphe.keepalive")
            prv.setAttributeNS(NS_ANDROID, "android:exported", "false")
            app.appendChild(prv)

            applied = true
        }
        if (applied) Logger.getLogger(this::class.java.name).info("Manifest Auto-Init Patched!")
    }
}
