package app.morphe.patches;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

public class KeepAliveService extends Service {

    // FOOL PROGUARD: Dummy call so method isn't deleted
    public KeepAliveService() {
        super();
        if (Build.VERSION.SDK_INT < 0) {
            start(null); 
        }
    }

    public static void start(Context ctx) {
        try {
            Intent i = new Intent(
                ctx, KeepAliveService.class
            );
            if (Build.VERSION.SDK_INT >= 26) {
                ctx.startForegroundService(i);
            } else {
                ctx.startService(i);
            }
        } catch (Exception e) {}
    }

    @Override
    public IBinder onBind(Intent i) { 
        return null; 
    }

    @Override
    public void onCreate() {
        super.onCreate();
        try {
            if (Build.VERSION.SDK_INT >= 26) {
                NotificationManager nm = 
                    getSystemService(
                        NotificationManager.class
                    );
                NotificationChannel ch = 
                    new NotificationChannel(
                        "fgs", "Active", 2
                    );
                if (nm != null) {
                    nm.createNotificationChannel(ch);
                }

                Notification.Builder b = 
                    new Notification.Builder(this, "fgs")
                    .setContentTitle("X is Immortal")
                    .setContentText("FGS Active")
                    .setSmallIcon(
                        android.R.drawable.ic_dialog_info
                    );

                if (Build.VERSION.SDK_INT >= 34) {
                    startForeground(1, b.build(), 1); 
                } else {
                    startForeground(1, b.build());
                }
            }
        } catch (Exception e) {}
    }
    
    @Override
    public int onStartCommand(Intent i, int f, int s) {
        return START_STICKY;
    }
}
