package one.tranic.sewlia.preload.mod;

import gg.pufferfish.pufferfish.simd.SIMDDetection;
import one.tranic.sewlia.preload.Preloads;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SIMDCheck extends Preloads {
    @Override
    public void doPreload() {
        Logger logger = LoggerFactory.getLogger("SIMDUtils");

        // Attempt to detect vectorization
        try {
            SIMDDetection.isEnabled = SIMDDetection.canEnable(logger);
            SIMDDetection.versionLimited = SIMDDetection.getJavaVersion() < 17;
        } catch (NoClassDefFoundError | Exception e) {
            e.printStackTrace();
        }

        if (SIMDDetection.isEnabled) {
            logger.info("SIMD operations detected as functional. Will replace some operations with faster versions.");
        } else if (SIMDDetection.versionLimited) {
            logger.warn("Will not enable SIMD! These optimizations are only safely supported on Java 17-21.");
        } else {
            logger.warn("SIMD operations are available for your server, but are not configured!");
            logger.warn("To enable additional optimizations, add \"--add-modules=jdk.incubator.vector\" to your startup flags, BEFORE the \"-jar\".");
            logger.warn("If you have already added this flag, then SIMD operations are not supported on your JVM or CPU.");
            logger.warn("Debug: Java: {}, test run: {}", System.getProperty("java.version"), SIMDDetection.testRun);
        }
    }
}