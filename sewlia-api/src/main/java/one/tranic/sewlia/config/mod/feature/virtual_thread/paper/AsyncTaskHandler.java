package one.tranic.sewlia.config.mod.feature.virtual_thread.paper;

import one.tranic.sewlia.config.annotation.DisableReload;

import java.util.concurrent.ThreadFactory;

public class AsyncTaskHandler {
    @DisableReload
    public static boolean value = false;

    public static ThreadFactory getThreadFactory() {
        return value ? Thread.ofVirtual().factory() : Thread.ofPlatform().factory();
    }
}