package one.tranic.sewlia.config.mod.feature.virtual_thread.minecraft.network;

import one.tranic.sewlia.annotation.loader.DisableReload;

import java.util.concurrent.ThreadFactory;

public class YggdrasilServices {
    @DisableReload
    public static boolean value = false;

    public static ThreadFactory getThreadFactory() {
        return value ? Thread.ofVirtual().factory() : Thread.ofPlatform().factory();
    }
}