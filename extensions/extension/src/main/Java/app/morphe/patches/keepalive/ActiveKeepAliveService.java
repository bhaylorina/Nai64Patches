package app.morphe.patches.keepalive;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

public class ActiveKeepAliveService extends Service {
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        try {
            if (Build.VERSION.SDK_INT >= 26) {
                NotificationChannel channel = new NotificationChannel(
                        "keepalive_channel",
                        "App Active Service",
                        NotificationManager.IMPORTANCE_LOW
                );
                NotificationManager manager = getSystemService(NotificationManager.class);
                if (manager != null) {
                    manager.createNotificationChannel(channel);
                }
                
                Notification.Builder builder = new Notification.Builder(this, "keepalive_channel")
                        .setContentTitle("Background Service Active")
                        .setContentText("Listening for instant notifications...")
                        .setSmallIcon(android.R.drawable.stat_notify_sync); 

                Notification notification = builder.build();
                
                if (Build.VERSION.SDK_INT >= 34) { 
                    startForeground(1, notification, 1); 
                } else {
                    startForeground(1, notification);
                }
            }
        } catch (Exception e) {
        }
    }
    
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY; 
    }
}

