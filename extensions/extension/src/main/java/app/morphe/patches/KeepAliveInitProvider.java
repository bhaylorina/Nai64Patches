package app.morphe.patches;

import android.app.Activity;
import android.app.Application;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

public class KeepAliveInitProvider extends ContentProvider {
    private static boolean isTriggered = false;

    @Override
    public boolean onCreate() {
        Context ctx = getContext();
        if (ctx != null && ctx.getApplicationContext() instanceof Application) {
            Application app = (Application) ctx.getApplicationContext();
            
            // Monitor every screen natively
            app.registerActivityLifecycleCallbacks(new Application.ActivityLifecycleCallbacks() {
                @Override
                public void onActivityResumed(Activity a) {
                    if (isTriggered) return;
                    isTriggered = true; // Lock
                    
                    // 1 Second Delay hack to beat Android 16 Animation Trap
                    new Handler(Looper.getMainLooper()).postDelayed(() -> {
                        try {
                            Intent i = new Intent(a, KeepAliveService.class);
                            if (Build.VERSION.SDK_INT >= 26) {
                                a.startForegroundService(i);
                            } else {
                                a.startService(i);
                            }
                        } catch (Exception e) {
                            isTriggered = false;
                            Toast.makeText(a, "FGS Blocked: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }, 1000);
                }

                @Override public void onActivityCreated(Activity a, Bundle b) {}
                @Override public void onActivityStarted(Activity a) {}
                @Override public void onActivityPaused(Activity a) {}
                @Override public void onActivityStopped(Activity a) {}
                @Override public void onActivitySaveInstanceState(Activity a, Bundle b) {}
                @Override public void onActivityDestroyed(Activity a) {}
            });
        }
        return true;
    }

    // Mandatory ContentProvider methods (We leave them empty)
    @Override public Cursor query(Uri u, String[] p, String s, String[] a, String so) { return null; }
    @Override public String getType(Uri u) { return null; }
    @Override public Uri insert(Uri u, ContentValues v) { return null; }
    @Override public int delete(Uri u, String s, String[] a) { return 0; }
    @Override public int update(Uri u, ContentValues v, String s, String[] a) { return 0; }
}

