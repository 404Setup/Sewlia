package one.tranic.sewlia.preload;

import one.tranic.sewlia.reflect.NewReflect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Set;

public class Preload {

    public Preload() {
        try {
            Set<Preloads> set = NewReflect.findAllClass("one.tranic.sewlia.preload.mod", Preloads.class);
            if (!set.isEmpty()) for (Preloads preload : set) preload.doPreload();
        } catch (IOException e) {
            Logger logger = LoggerFactory.getLogger("Sewlia-Preload");
            logger.error(e.getMessage());
        }
    }
}