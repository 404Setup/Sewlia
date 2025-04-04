package one.tranic.sewlia.config.mod.feature.virtual_thread.netty;

import one.tranic.sewlia.annotation.loader.DisableReload;

import java.util.concurrent.ThreadFactory;

public class Server {
    @DisableReload
    public static boolean value = false;

    public static ThreadFactory getThreadFactory() {
        return value ? Thread.ofVirtual().factory() : Thread.ofPlatform().factory();
    }
}