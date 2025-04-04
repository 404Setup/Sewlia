package one.tranic.sewlia.config.mod.feature.virtual_thread.minecraft;

import one.tranic.sewlia.annotation.loader.DisableReload;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class NamedThread {
    @DisableReload
    public static boolean value = false;

    public static Thread getThread(@Nullable ThreadGroup group, Runnable task, @NotNull String name, long stackSize) {
        return value ? Thread.ofVirtual().name(name).unstarted(task) : new Thread(group, task, name, stackSize);
    }
}