package patches.universal.manifest

import app.morphe.patcher.patch.resourcePatch
import java.util.logging.Logger

@Suppress("unused")
val activeManifestPatch = resourcePatch(
    name = "Add FGS Permissions",
    description = "Android 14 FGS tags",
    default = false,
) {
    execute {
        var applied = false
        document("AndroidManifest.xml").use { m ->
            val root = m.documentElement ?: return@use
            val app = root.applicationOrNull() ?: return@use

            val p1 = m.createElement("uses-permission")
            p1.setAttributeNS(
                NS_ANDROID, "android:name", 
                "android.permission.FOREGROUND_SERVICE"
            )
            val p2 = m.createElement("uses-permission")
            p2.setAttributeNS(
                NS_ANDROID, "android:name", 
                "android.permission.FOREGROUND_SERVICE_DATA_SYNC"
            )
            root.appendChild(p1)
            root.appendChild(p2)

            val srv = m.createElement("service")
            srv.setAttributeNS(
                NS_ANDROID, "android:name", 
                "app.morphe.patches.KeepAliveService"
            )
            srv.setAttributeNS(
                NS_ANDROID, "android:exported", "false"
            )
            srv.setAttributeNS(
                NS_ANDROID, "android:foregroundServiceType", 
                "dataSync"
            )
            app.appendChild(srv)
            applied = true
        }
        if (applied) Logger.getLogger(
            this::class.java.name
        ).info("Manifest Patched")
    }
}

