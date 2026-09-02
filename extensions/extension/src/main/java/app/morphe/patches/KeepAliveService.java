package app.morphe.patches;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

public class KeepAliveService extends Service {
    
    private static boolean isTriggered = false;

    public KeepAliveService() {
        super();
        if (Build.VERSION.SDK_INT < 0) { start(null); }
    }

    public static void start(Context ctx) {
        if (isTriggered || ctx == null) return;
        isTriggered = true; // Lock engaged
        
        // DELAY HACK: Wait 1 full second for app animation to finish
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            try {
                Intent i = new Intent(ctx, KeepAliveService.class);
                if (Build.VERSION.SDK_INT >= 26) {
                    ctx.startForegroundService(i);
                } else {
                    ctx.startService(i);
                }
            } catch (Exception e) {
                isTriggered = false; // Unlock so it can retry on next screen
                // THE SNITCH: Will show exact error on screen if OS blocks it
                Toast.makeText(ctx, "FGS Blocked: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }, 1000); // 1000 milliseconds = 1 Second
    }

    @Override 
    public IBinder onBind(Intent i) { return null; }

    @Override
    public int onStartCommand(Intent i, int f, int s) {
        try {
            if (Build.VERSION.SDK_INT >= 26) {
                NotificationManager nm = getSystemService(NotificationManager.class);
                NotificationChannel ch = new NotificationChannel("fgs_x", "X Immortal", 2);
                if (nm != null) nm.createNotificationChannel(ch);

                Notification.Builder b = new Notification.Builder(this, "fgs_x")
                    .setContentTitle("X is Immortal")
                    .setContentText("Network Locked & Active")
                    .setSmallIcon(android.R.drawable.ic_menu_info_details);

                if (Build.VERSION.SDK_INT >= 34) {
                    startForeground(1001, b.build(), 512); // REMOTE_MESSAGING tag
                } else {
                    startForeground(1001, b.build());
                }
            }
        } catch (Exception e) {
            new Handler(Looper.getMainLooper()).post(() -> 
                Toast.makeText(this, "Notification Error: " + e.getMessage(), Toast.LENGTH_LONG).show()
            );
        }
        
        return START_STICKY;
    }
}
