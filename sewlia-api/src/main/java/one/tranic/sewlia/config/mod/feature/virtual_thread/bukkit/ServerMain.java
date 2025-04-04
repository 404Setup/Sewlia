package one.tranic.sewlia.config.mod.feature.virtual_thread.bukkit;

import one.tranic.sewlia.annotation.loader.DisableReload;

public class ServerMain {
    @DisableReload
    public static boolean value = false;

    public static Thread getThread(final Runnable runnable, final String name) {
        return value ? Thread.ofVirtual().name(name).unstarted(runnable) : new Thread(runnable, name);
    }
}