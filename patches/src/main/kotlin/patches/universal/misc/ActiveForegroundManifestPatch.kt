package patches.universal.manifest

import app.morphe.patcher.patch.resourcePatch
import java.util.logging.Logger

@Suppress("unused")
val activeForegroundManifestPatch = resourcePatch(
    name = "Add Active Foreground Service Info",
    description = "Injects Android 14 foreground service permissions and tags.",
    default = false,
) {
    execute {
        val logger = Logger.getLogger(this::class.java.name)
        var applied = false

        document("AndroidManifest.xml").use { manifest ->
            val root = manifest.documentElement ?: return@use
            val application = root.applicationOrNull() ?: return@use

            // 1. Add required Android 14 FGS Permissions
            val fgsPermission = manifest.createElement("uses-permission")
            fgsPermission.setAttributeNS(NS_ANDROID, "android:name", "android.permission.FOREGROUND_SERVICE")
            
            val dataSyncPermission = manifest.createElement("uses-permission")
            dataSyncPermission.setAttributeNS(NS_ANDROID, "android:name", "android.permission.FOREGROUND_SERVICE_DATA_SYNC")

            root.appendChild(fgsPermission)
            root.appendChild(dataSyncPermission)

            // 2. Add the Service tag with the required foregroundServiceType
            val service = manifest.createElement("service")
            service.setAttributeNS(NS_ANDROID, "android:name", "com.twitter.android.ActiveKeepAliveService")
            service.setAttributeNS(NS_ANDROID, "android:exported", "false")
            service.setAttributeNS(NS_ANDROID, "android:foregroundServiceType", "dataSync")

            application.appendChild(service)
            applied = true
        }

        if (applied) logger.info("Successfully injected Android 14 FGS manifest data")
    }
}

