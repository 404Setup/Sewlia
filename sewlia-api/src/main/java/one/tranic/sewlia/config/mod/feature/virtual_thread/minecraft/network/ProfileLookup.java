package one.tranic.sewlia.config.mod.feature.virtual_thread.minecraft.network;

import one.tranic.sewlia.annotation.loader.DisableReload;

public class ProfileLookup {
    @DisableReload
    public static boolean value = false;

    public static Thread getThread(final Runnable runnable) {
        return value ? Thread.ofVirtual().unstarted(runnable) : new Thread(runnable);
    }
}