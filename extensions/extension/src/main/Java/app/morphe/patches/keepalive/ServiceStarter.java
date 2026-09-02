package app.morphe.patches.keepalive;

import android.content.Context;
import android.content.Intent;
import android.os.Build;

public class ServiceStarter {
    public static void start(Context context) {
        try {
            Intent intent = new Intent(context, ActiveKeepAliveService.class);
            if (Build.VERSION.SDK_INT >= 26) {
                context.startForegroundService(intent);
            } else {
                context.startService(intent);
            }
        } catch (Exception e) {
        }
    }
}

