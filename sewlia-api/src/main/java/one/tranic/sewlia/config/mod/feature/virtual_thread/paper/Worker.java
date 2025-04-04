package one.tranic.sewlia.config.mod.feature.virtual_thread.paper;

import com.google.common.util.concurrent.MoreExecutors;
import one.tranic.sewlia.annotation.loader.DisableReload;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Worker {
    @DisableReload
    public static boolean value = false;

    public static ExecutorService getDirectExecutorService() {
        return value ? Executors.newVirtualThreadPerTaskExecutor() : MoreExecutors.newDirectExecutorService();
    }
}