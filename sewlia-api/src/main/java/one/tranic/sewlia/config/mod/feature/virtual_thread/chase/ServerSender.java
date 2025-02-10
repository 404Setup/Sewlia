package one.tranic.sewlia.config.mod.feature.virtual_thread.chase;

import one.tranic.sewlia.config.annotation.DisableReload;

public class ServerSender {
    @DisableReload
    public static boolean value = false;

    public static Thread getThread(final Runnable runnable, final String name) {
        return value ? Thread.ofVirtual().name(name).unstarted(runnable) : new Thread(runnable, name);
    }
}