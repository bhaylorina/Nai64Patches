package app.morphe.patches;

import android.content.Context;
import android.content.Intent;
import android.os.Build;

public class ServiceStarter {
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
        } catch (Exception e) {
            // Yahan error catch hoga!
            // Background crash prevent karega
        }
    }
}

