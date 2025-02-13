package one.tranic.sewlia.preload.mod;

import one.tranic.sewlia.preload.Preloads;

public class Spark extends Preloads {
    @Override
    public void doPreload() {
        System.setProperty("spark.serverconfigs.extra", "sewlia.yaml");
    }
}