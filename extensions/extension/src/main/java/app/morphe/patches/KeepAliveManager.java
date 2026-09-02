package app.morphe.patches;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.os.Bundle;
import android.os.Build;

public class KeepAliveManager {
    private static boolean started = false;

    public static void init(Application app) {
        if (started) return;
        started = true;
        
        app.registerActivityLifecycleCallbacks(
            new Application.ActivityLifecycleCallbacks() {
                @Override
                public void onActivityResumed(Activity a) {
                    try {
                        Intent i = new Intent(
                            a, KeepAliveService.class
                        );
                        if (Build.VERSION.SDK_INT >= 26) {
                            a.startForegroundService(i);
                        } else {
                            a.startService(i);
                        }
                    } catch (Exception e) {}
                }
                
                // Empty mandatory overrides
                @Override
                public void onActivityCreated(
                    Activity a, Bundle b
                ) {}
                @Override
                public void onActivityStarted(Activity a) {}
                @Override
                public void onActivityPaused(Activity a) {}
                @Override
                public void onActivityStopped(Activity a) {}
                @Override
                public void onActivitySaveInstanceState(
                    Activity a, Bundle b
                ) {}
                @Override
                public void onActivityDestroyed(Activity a) {}
            }
        );
    }
}

