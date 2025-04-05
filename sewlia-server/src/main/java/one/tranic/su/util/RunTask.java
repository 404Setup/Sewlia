package one.tranic.su.util;

import ca.spottedleaf.moonrise.common.PlatformHooks;
import io.papermc.paper.configuration.GlobalConfiguration;
import one.tranic.sewlia.config.util.ConfigUtils;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;

@SuppressWarnings("unused")
public class RunTask {
    public static void checkThreadTask() {
        int foliaRegionThreads = getFoliaRegionThreads();
        ChunkSystem chunkSystem = ChunkSystem.get();

        int nettyThreads = Integer.getInteger("io.netty.eventLoopThreads", 4);
        int otherPluginThreads = 2; // Inaccurate speculation
        int systemThreads = 1;

        SystemInfo systemInfo = new SystemInfo();
        CentralProcessor processor = systemInfo.getHardware().getProcessor();
        int physicalCoreCount = processor.getPhysicalProcessorCount();

        int allThreads = foliaRegionThreads + nettyThreads + otherPluginThreads;
        if (allThreads >= physicalCoreCount) {
            ConfigUtils.logger.warn("****************************");
            ConfigUtils.logger.warn("Thread Allocation Warning: The number of threads you pre-allocated ({}) far " +
                    "exceeds the number of physical CPU cores ({}) on the system!", allThreads, physicalCoreCount);
            ConfigUtils.logger.warn("Possible Causes: This might occur if you over-allocated " +
                    "threads for certain tasks or plugins.");
            ConfigUtils.logger.warn("Suggestions: If this was unintentional, reduce the allocation of threads " +
                    "for thread-heavy tasks (Folia regions, Netty, or other plugins).");
            ConfigUtils.logger.warn("You can also offload some threads to virtual threads in Sewlia Config.");
            ConfigUtils.logger.warn("Note: If your server rarely experiences high load, you may ignore this warning. " +
                    "Otherwise, consider a more balanced thread allocation strategy to optimize performance.");
            ConfigUtils.logger.warn("****************************");
        }

    }

    public static int getFoliaRegionThreads() {
        int foliaRegionThreads = GlobalConfiguration.get().threadedRegions.threads;
        if (foliaRegionThreads <= 0) {
            int tickThreads = Runtime.getRuntime().availableProcessors() / 2;
            if (tickThreads <= 4) {
                foliaRegionThreads = 1;
            } else {
                foliaRegionThreads = tickThreads / 4;
            }
        }
        return foliaRegionThreads;
    }

    record ChunkSystem(int workerThreads, int ioThreads) {
        static ChunkSystem get() {
            int configWorkerThreads = GlobalConfiguration.get().chunkSystem.workerThreads;
            int configIoThreads = GlobalConfiguration.get().chunkSystem.ioThreads;

            int defaultWorkerThreads = Runtime.getRuntime().availableProcessors() / 2;
            if (defaultWorkerThreads <= 4) {
                defaultWorkerThreads = defaultWorkerThreads <= 3 ? 1 : 2;
            } else {
                defaultWorkerThreads = defaultWorkerThreads / 2;
            }

            defaultWorkerThreads = Integer.getInteger(PlatformHooks.get().getBrand() + ".WorkerThreadCount", Integer.valueOf(defaultWorkerThreads));

            int workerThreads = configWorkerThreads;

            if (workerThreads <= 0) {
                workerThreads = defaultWorkerThreads;
            }

            final int ioThreads = Math.max(1, configIoThreads);

            return new ChunkSystem(workerThreads, ioThreads);
        }
    }
}
