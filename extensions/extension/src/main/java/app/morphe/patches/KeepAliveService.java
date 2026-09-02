package app.morphe.patches;

import android.app.Activity;
import android.app.Application;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.IBinder;
import android.widget.Toast;

public class KeepAliveService extends Service {
    private static boolean isTriggered = false;
    private static boolean isInit = false;

    // Called precisely when MultiDex is 100% loaded
    public static void init(Application app) {
        if (isInit) return;
        isInit = true;

        app.registerActivityLifecycleCallbacks(new Application.ActivityLifecycleCallbacks() {
            @Override
            public void onActivityResumed(Activity a) {
                if (isTriggered) return;
                isTriggered = true; // Lock 

                // 1 Second Delay to beat Android 16 Animation Check
                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                    try {
                        Intent i = new Intent(a, KeepAliveService.class);
                        if (Build.VERSION.SDK_INT >= 26) {
                            a.startForegroundService(i);
                        } else {
                            a.startService(i);
                        }
                    } catch (Exception e) {
                        isTriggered = false; // Unlock to retry
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

    @Override public IBinder onBind(Intent i) { return null; }

    @Override
    public int onStartCommand(Intent i, int f, int s) {
        try {
            if (Build.VERSION.SDK_INT >= 26) {
                NotificationManager nm = getSystemService(NotificationManager.class);
                NotificationChannel ch = new NotificationChannel("fgs_x", "X Immortal", 2);
                if (nm != null) nm.createNotificationChannel(ch);

                Notification.Builder b = new Notification.Builder(this, "fgs_x")
                    .setContentTitle("X is Immortal")
                    .setContentText("Network Locked (Background Active)")
                    .setSmallIcon(android.R.drawable.ic_menu_info_details);

                if (Build.VERSION.SDK_INT >= 34) {
                    startForeground(1001, b.build(), 512); // REMOTE_MESSAGING
                } else {
                    startForeground(1001, b.build());
                }
            }
        } catch (Exception e) {}
        return START_STICKY;
    }
}
