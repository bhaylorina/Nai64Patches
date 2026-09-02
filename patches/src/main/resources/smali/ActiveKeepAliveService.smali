.class public Lapp/morphe/patches/keepalive/ActiveKeepAliveService;
.super Landroid/app/Service;

.method public constructor <init>()V
    .locals 0
    invoke-direct {p0}, Landroid/app/Service;-><init>()V
    return-void
.end method

.method public onBind(Landroid/content/Intent;)Landroid/os/IBinder;
    .locals 1
    const/4 v0, 0x0
    return-object v0
.end method

.method public onCreate()V
    .locals 6

    invoke-super {p0}, Landroid/app/Service;->onCreate()V

    sget v0, Landroid/os/Build$VERSION;->SDK_INT:I
    const/16 v1, 0x1a
    if-lt v0, v1, :end

    const-class v1, Landroid/app/NotificationManager;
    invoke-virtual {p0, v1}, Landroid/content/Context;->getSystemService(Ljava/lang/Class;)Ljava/lang/Object;
    move-result-object v1
    check-cast v1, Landroid/app/NotificationManager;

    const-string v2, "keepalive_channel"
    const-string v3, "App Active Service"
    const/4 v4, 0x2
    new-instance v5, Landroid/app/NotificationChannel;
    invoke-direct {v5, v2, v3, v4}, Landroid/app/NotificationChannel;-><init>(Ljava/lang/String;Ljava/lang/CharSequence;I)V

    if-eqz v1, :skip_channel
    invoke-virtual {v1, v5}, Landroid/app/NotificationManager;->createNotificationChannel(Landroid/app/NotificationChannel;)V

:skip_channel
    new-instance v3, Landroid/app/Notification$Builder;
    invoke-direct {v3, p0, v2}, Landroid/app/Notification$Builder;-><init>(Landroid/content/Context;Ljava/lang/String;)V

    const-string v2, "Background Service Active"
    invoke-virtual {v3, v2}, Landroid/app/Notification$Builder;->setContentTitle(Ljava/lang/CharSequence;)Landroid/app/Notification$Builder;

    const-string v2, "Listening for instant notifications..."
    invoke-virtual {v3, v2}, Landroid/app/Notification$Builder;->setContentText(Ljava/lang/CharSequence;)Landroid/app/Notification$Builder;

    const v2, 0x108008a
    invoke-virtual {v3, v2}, Landroid/app/Notification$Builder;->setSmallIcon(I)Landroid/app/Notification$Builder;

    invoke-virtual {v3}, Landroid/app/Notification$Builder;->build()Landroid/app/Notification;
    move-result-object v2

    const/16 v3, 0x22
    const/4 v4, 0x1
    if-lt v0, v3, :android_13_below

    invoke-virtual {p0, v4, v2, v4}, Landroid/app/Service;->startForeground(ILandroid/app/Notification;I)V
    goto :end

:android_13_below
    invoke-virtual {p0, v4, v2}, Landroid/app/Service;->startForeground(ILandroid/app/Notification;)V

:end
    return-void
.end method

.method public onStartCommand(Landroid/content/Intent;II)I
    .locals 1
    const/4 v0, 0x1
    return v0
.end method

