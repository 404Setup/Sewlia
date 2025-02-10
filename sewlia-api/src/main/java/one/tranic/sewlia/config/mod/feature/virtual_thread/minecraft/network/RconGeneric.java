package one.tranic.sewlia.config.mod.feature.virtual_thread.minecraft.network;

import one.tranic.sewlia.config.annotation.DisableReload;

public class RconGeneric {
    @DisableReload
    public static boolean value = false;

    public static Thread getThread(final Runnable r, final String name) {
        return value ? Thread.ofVirtual().name(name).unstarted(r) : new Thread(r, name);
    }
}