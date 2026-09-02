package app.morphe.patches;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.widget.Toast;
import android.os.Handler;
import android.os.Looper;

public class KeepAliveService extends Service {
    
    public KeepAliveService() {
        super();
        // Dummy call to prevent R8 from deleting the start method
        if (Build.VERSION.SDK_INT < 0) { start(null); }
    }

    public static void start(Context ctx) {
        try {
            Intent i = new Intent(ctx, KeepAliveService.class);
            if (Build.VERSION.SDK_INT >= 26) {
                ctx.startForegroundService(i);
            } else {
                ctx.startService(i);
            }
        } catch (Exception e) {
            new Handler(Looper.getMainLooper()).post(() -> 
                Toast.makeText(ctx, "FGS Blocked: " + e.getMessage(), 
                Toast.LENGTH_LONG).show()
            );
        }
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
                    .setContentText("Network Locked (Listening...)")
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
