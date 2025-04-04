package one.tranic.sewlia.config.mod.feature.virtual_thread.bukkit;

import one.tranic.sewlia.annotation.loader.DisableReload;

import java.util.concurrent.ThreadFactory;

public class AsyncScheduler {
    @DisableReload
    public static boolean value = false;

    public static ThreadFactory getThreadFactory() {
        return value ? Thread.ofVirtual().factory() : Thread.ofPlatform().factory();
    }
}