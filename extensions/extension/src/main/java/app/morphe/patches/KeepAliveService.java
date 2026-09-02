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
import android.widget.Toast;

public class KeepAliveService extends Service {
    private static boolean running = false;

    public static void trigger(Context ctx) {
        if (running || ctx == null) return;
        running = true;
        
        // Use App Context so the request is never destroyed by Activity changes
        Context appCtx = ctx.getApplicationContext();
        
        try {
            Intent i = new Intent(appCtx, KeepAliveService.class);
            if (Build.VERSION.SDK_INT >= 26) {
                appCtx.startForegroundService(i);
            } else {
                appCtx.startService(i);
            }
        } catch (Exception e) {
            running = false; // Reset on fail
            // SNITCH 1: Tells you if OS completely blocked the start command
            new Handler(Looper.getMainLooper()).post(() -> 
                Toast.makeText(appCtx, "Start Error: " + e.getMessage(), Toast.LENGTH_LONG).show()
            );
        }
    }

    @Override public IBinder onBind(Intent intent) { return null; }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
            if (Build.VERSION.SDK_INT >= 26) {
                NotificationManager nm = getSystemService(NotificationManager.class);
                NotificationChannel ch = new NotificationChannel("fgs_immortal", "X Keep Alive", 2);
                if (nm != null) nm.createNotificationChannel(ch);

                Notification.Builder b = new Notification.Builder(this, "fgs_immortal")
                    .setContentTitle("X Immortal")
                    .setContentText("Background Sync Active")
                    .setSmallIcon(android.R.drawable.ic_menu_info_details);

                if (Build.VERSION.SDK_INT >= 34) {
                    startForeground(1001, b.build(), 512); // REMOTE_MESSAGING
                } else {
                    startForeground(1001, b.build());
                }
            }
        } catch (Exception e) {
            // SNITCH 2: Tells you if the Notification itself was rejected by API 36
            new Handler(Looper.getMainLooper()).post(() -> 
                Toast.makeText(this, "FGS Crash: " + e.getMessage(), Toast.LENGTH_LONG).show()
            );
        }
        return START_STICKY;
    }
}
