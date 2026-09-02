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
        
        Context appCtx = ctx.getApplicationContext();
        
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            try {
                Intent i = new Intent(appCtx, KeepAliveService.class);
                if (Build.VERSION.SDK_INT >= 26) {
                    appCtx.startForegroundService(i);
                } else {
                    appCtx.startService(i);
                }
            } catch (Exception e) {
                running = false; 
                Toast.makeText(appCtx, "FGS Blocked: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }, 1500); 
    }

    @Override 
    public IBinder onBind(Intent intent) { return null; }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
            if (Build.VERSION.SDK_INT >= 26) {
                NotificationManager nm = getSystemService(NotificationManager.class);
                NotificationChannel ch = new NotificationChannel("fgs_immortal", "X Keep Alive", 2);
                if (nm != null) nm.createNotificationChannel(ch);

                Notification.Builder b = new Notification.Builder(this, "fgs_immortal")
                    .setContentTitle("X is Immortal")
                    .setContentText("Background Sync Active")
                    .setSmallIcon(android.R.drawable.ic_menu_info_details);

                if (Build.VERSION.SDK_INT >= 34) {
                    startForeground(1001, b.build(), 512);
                } else {
                    startForeground(1001, b.build());
                }
            }
        } catch (Exception e) {}
        
        return START_STICKY;
    }
}
