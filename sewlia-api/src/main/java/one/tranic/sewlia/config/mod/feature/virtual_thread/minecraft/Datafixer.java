package one.tranic.sewlia.config.mod.feature.virtual_thread.minecraft;

import one.tranic.sewlia.config.annotation.DisableReload;

import java.util.concurrent.ThreadFactory;

public class Datafixer {
    @DisableReload
    public static boolean value = false;

    public static ThreadFactory getThreadFactory() {
        return value ? Thread.ofVirtual().factory() : Thread.ofPlatform().factory();
    }
}