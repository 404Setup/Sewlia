package one.tranic.sewlia.preload;

import one.tranic.sewlia.config.util.ConfigUtils;
import one.tranic.sewlia.reflect.NewReflect;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class Preload {

    public Preload() {
        try {
            @NotNull Preloads[] preloads = NewReflect.findAllClass("one.tranic.sewlia.preload.mod", Preloads.class);
            if (preloads.length < 1) return;
            for (int i = 0; i < preloads.length; i++) preloads[i].doPreload();
        } catch (IOException e) {
            ConfigUtils.logger.error(e.getMessage());
        }
    }
}