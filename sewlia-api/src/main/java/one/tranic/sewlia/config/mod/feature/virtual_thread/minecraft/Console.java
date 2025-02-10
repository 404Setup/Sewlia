package one.tranic.sewlia.config.mod.feature.virtual_thread.minecraft;

import one.tranic.sewlia.config.annotation.DisableReload;

public class Console {
    @DisableReload
    public static boolean value = false;

    public static Thread getThread(final String name, final Runnable r) {
        return value ? Thread.ofVirtual().name(name).unstarted(r) : new Thread(r, name);
    }
}