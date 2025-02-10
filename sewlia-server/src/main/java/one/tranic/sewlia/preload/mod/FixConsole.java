package one.tranic.sewlia.preload.mod;

import gg.pufferfish.pufferfish.simd.SIMDDetection;
import one.tranic.sewlia.preload.Preloads;

public class FixConsole extends Preloads {
    @Override
    public void doPreload() {
        if (SIMDDetection.getJavaVersion() > 21) System.setProperty("jdk.console", "java.base");
    }
}