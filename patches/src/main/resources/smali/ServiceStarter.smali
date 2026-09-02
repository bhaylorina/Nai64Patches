.class public Lapp/morphe/patches/keepalive/ServiceStarter;
.super Ljava/lang/Object;

.method public static start(Landroid/content/Context;)V
    .locals 3

    new-instance v0, Landroid/content/Intent;
    const-class v1, Lapp/morphe/patches/keepalive/ActiveKeepAliveService;
    invoke-direct {v0, p0, v1}, Landroid/content/Intent;-><init>(Landroid/content/Context;Ljava/lang/Class;)V

    sget v1, Landroid/os/Build$VERSION;->SDK_INT:I
    const/16 v2, 0x1a
    if-lt v1, v2, :legacy

    invoke-virtual {p0, v0}, Landroid/content/Context;->startForegroundService(Landroid/content/Intent;)Landroid/content/ComponentName;
    goto :end

:legacy
    invoke-virtual {p0, v0}, Landroid/content/Context;->startService(Landroid/content/Intent;)Landroid/content/ComponentName;

:end
    return-void
.end method
