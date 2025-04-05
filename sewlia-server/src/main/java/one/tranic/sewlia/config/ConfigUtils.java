package one.tranic.sewlia.config;

import one.tranic.sewlia.config.util.NewConfigScanner;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

public class ConfigUtils {
    private static YamlConfiguration configuration;
    private static Map<Class<?>, String> configClasses;

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
        } catch (IOException e) {
            throw new RuntimeException(e);
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

        if (configClasses == null || configClasses.isEmpty()) {
            configClasses = NewConfigScanner.getClasses("one.tranic.sewlia.config.mod");
        }
        if (configClasses != null && !configClasses.isEmpty())
            for (Map.Entry<Class<?>, String> entry : configClasses.entrySet())
                NewConfigScanner.processStaticValueFieldWithWrite(entry.getKey(), entry.getValue(), configuration);
        configuration.options().copyDefaults(true);
        configuration.save(file);
    }

    private static void readAll(boolean isReload) {
        if (configClasses == null || configClasses.isEmpty()) {
            configClasses = NewConfigScanner.getClasses("one.tranic.sewlia.config.mod");
        }
        if (configClasses != null && !configClasses.isEmpty())
            for (Map.Entry<Class<?>, String> entry : configClasses.entrySet())
                NewConfigScanner.processStaticValueFieldWithRead(entry.getKey(), entry.getValue(), configuration, isReload);
    }
}