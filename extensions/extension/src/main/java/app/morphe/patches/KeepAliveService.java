package app.morphe.patches;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

public class KeepAliveService extends Service {
    @Override 
    public IBinder onBind(Intent i) { return null; }

    @Override
    public int onStartCommand(Intent i, int f, int s) {
        try {
            if (Build.VERSION.SDK_INT >= 26) {
                NotificationManager nm = getSystemService(
                    NotificationManager.class
                );
                NotificationChannel ch = new NotificationChannel(
                    "fgs_x", "X Immortal", 2
                );
                if (nm != null) {
                    nm.createNotificationChannel(ch);
                }

                Notification.Builder b = new Notification.Builder(
                    this, "fgs_x"
                )
                .setContentTitle("X is Immortal")
                .setContentText("Listening for DMs...")
                .setSmallIcon(
                    android.R.drawable.ic_menu_info_details
                );

                if (Build.VERSION.SDK_INT >= 34) {
                    // 512 = REMOTE_MESSAGING
                    startForeground(1001, b.build(), 512);
                } else {
                    startForeground(1001, b.build());
                }
            }
        } catch (Exception e) {}
        
        return START_STICKY;
    }
}
