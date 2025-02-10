package one.tranic.sewlia.config.util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.ThreadFactory;

public class ThreadConfig {
    public static ThreadFactory getThreadFactory(final boolean value) {
        return value ? Thread.ofVirtual().factory() : Thread.ofPlatform().factory();
    }

    public static Thread getThread(final boolean value, final Runnable runnable) {
        return value ? Thread.ofVirtual().unstarted(runnable) : new Thread(runnable);
    }

    public static Thread getThread(final boolean value, final Runnable runnable, final String name) {
        return value ? Thread.ofVirtual().name(name).unstarted(runnable) : new Thread(runnable, name);
    }

    public static Thread getThread(final boolean value, @Nullable ThreadGroup group, Runnable task, @NotNull String name, long stackSize) {
        return value ? Thread.ofVirtual().name(name).unstarted(task) : new Thread(group, task, name, stackSize);
    }
}