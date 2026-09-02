package app.morphe.patches;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.IBinder;

public class KeepAliveService extends Service {
    private static boolean running = false;

    public static void trigger(Context ctx) {
        if (running || ctx == null) return;
        
        // 1500ms (1.5s) delay ensures window transition animations are complete.
        // Android 16 grants FGS tokens ONLY when UI is fully settled.
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            try {
                Intent i = new Intent(ctx, KeepAliveService.class);
                if (Build.VERSION.SDK_INT >= 26) {
                    ctx.startForegroundService(i);
                } else {
                    ctx.startService(i);
                }
                running = true;
            } catch (Exception e) {
                running = false;
            }
        }, 1500);
    }

    @Override public IBinder onBind(Intent intent) { return null; }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
            if (Build.VERSION.SDK_INT >= 26) {
                NotificationManager nm = getSystemService(NotificationManager.class);
                NotificationChannel ch = new NotificationChannel("fgs_immortal", "X Keep Alive", NotificationManager.IMPORTANCE_LOW);
                if (nm != null) nm.createNotificationChannel(ch);

                Notification.Builder b = new Notification.Builder(this, "fgs_immortal")
                    .setContentTitle("X Background Sync")
                    .setContentText("Keeping app alive in background")
                    .setSmallIcon(android.R.drawable.ic_menu_info_details);

                if (Build.VERSION.SDK_INT >= 34) {
                    startForeground(1001, b.build(), 512); // 512 = REMOTE_MESSAGING
                } else {
                    startForeground(1001, b.build());
                }
            }
        } catch (Exception e) {}
        return START_STICKY;
    }
}
