package one.tranic.sewlia.config;

import one.tranic.sewlia.config.mod.unsafe.optimization.fast_rng.EnableForWorldgen;
import one.tranic.sewlia.config.mod.unsafe.optimization.fast_rng.Enabled;
import one.tranic.sewlia.config.mod.unsafe.optimization.fast_rng.RandomGenerator;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.random.RandomGeneratorFactory;

public class ConfigUtils {
    private static YamlConfiguration configuration;

    public static YamlConfiguration getConfiguration() {
        return configuration;
    }

    public static void reloadConfiguration(boolean isCommandSource) {
        try {
            File configFile = new File("sewlia.yaml");
            if (!configFile.exists()) {
                configFile.createNewFile();
            }
            configuration = YamlConfiguration.loadConfiguration(configFile);
            if (!isCommandSource) {
                addDefaults(configFile);
                addDefaults(configFile);
            }
            readAll(isCommandSource);

            ReadDo();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void ReadDo() {
        if (Enabled.value || EnableForWorldgen.value) {
            try {
                RandomGeneratorFactory.of(RandomGenerator.value);
            } catch (Exception e) {
                one.tranic.sewlia.config.util.ConfigUtils.logger.error("Faster random generator is enabled but {} is not supported by your JVM, " +
                        "falling back to legacy random source.", RandomGenerator.value);
                Enabled.value = false;
                EnableForWorldgen.value = false;
            }
        }
    }

    private static void addDefaults(@NotNull File file) throws IOException {
        configuration.options().setHeader(
                Arrays.asList(
                        "Sewlia. Configuration",
                        "Sewlia Github: https://github.com/LevelTranic/Sewlia",
                        "It is recommended to always keep the latest version, download it at https://tranic.one/downloads/sewlia"
                )
        );

        @Nullable Map<Class<?>, String> clz = NewConfigScanner.getClasses();
        if (clz != null && !clz.isEmpty()) {
            for (Map.Entry<Class<?>, String> entry : clz.entrySet())
                NewConfigScanner.processStaticValueFieldWithWrite(entry.getKey(), entry.getValue());
        }
        configuration.options().copyDefaults(true);
        configuration.save(file);
    }

    private static void readAll(boolean isReload) {
        @Nullable Map<Class<?>, String> clz = NewConfigScanner.getClasses();
        if (clz == null || clz.isEmpty()) return;
        for (Map.Entry<Class<?>, String> entry : clz.entrySet())
            NewConfigScanner.processStaticValueFieldWithRead(entry.getKey(), entry.getValue(), isReload);
    }
}