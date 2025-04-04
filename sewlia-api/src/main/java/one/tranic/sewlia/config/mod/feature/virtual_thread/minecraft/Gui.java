package one.tranic.sewlia.config.mod.feature.virtual_thread.minecraft;

import one.tranic.sewlia.annotation.loader.DisableReload;

public class Gui {
    @DisableReload
    public static boolean value = false;

    public static Thread getThread(final Runnable r) {
        return value ? Thread.ofVirtual().unstarted(r) : new Thread(r);
    }
}