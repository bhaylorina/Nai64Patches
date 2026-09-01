package patches.universal.manifest

import app.morphe.patcher.patch.resourcePatch
import java.util.logging.Logger

@Suppress("unused")
val keepAliveAccessibilityPatch = resourcePatch(
    name = "Keep Alive Accessibility",
    description = "Injects an Accessibility Service into the manifest to keep the app immortal like aodnotify.",
    default = false,
) {
    execute {
        val logger = Logger.getLogger(this::class.java.name)
        var applied = false

        document("AndroidManifest.xml").use { manifest ->
            val application = manifest.documentElement.applicationOrNull()
                ?: return@use

            // Create the Accessibility Service tag
            val service = manifest.createElement("service")
            service.setAttributeNS(NS_ANDROID, "android:name", "com.twitter.android.KeepAliveService")
            service.setAttributeNS(NS_ANDROID, "android:permission", "android.permission.BIND_ACCESSIBILITY_SERVICE")
            service.setAttributeNS(NS_ANDROID, "android:exported", "true")

            // Add the intent filter so OS recognizes it
            val intentFilter = manifest.createElement("intent-filter")
            val action = manifest.createElement("action")
            action.setAttributeNS(NS_ANDROID, "android:name", "android.accessibilityservice.AccessibilityService")
            intentFilter.appendChild(action)

            // Add meta-data required for Accessibility Services
            val metaData = manifest.createElement("meta-data")
            metaData.setAttributeNS(NS_ANDROID, "android:name", "android.accessibilityservice")
            metaData.setAttributeNS(NS_ANDROID, "android:value", "true")

            service.appendChild(intentFilter)
            service.appendChild(metaData)
            application.appendChild(service)
            
            applied = true
        }

        if (applied) logger.info("Successfully injected Accessibility Keep-Alive Service")
        else logger.warning("Could not find <application> tag. No changes applied.")
    }
}
    
