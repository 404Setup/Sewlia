package one.tranic.sewlia.task;

import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.entity.Player;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryUsage;

public class RamBarTask extends BossBarTask {
    private static final float MEDIUM_MEMORY_THRESHOLD = 0.5F;
    private static final float HIGH_MEMORY_THRESHOLD = 0.75F;
    private static final float MEDIUM_TEXT_THRESHOLD = 0.60F;
    private static final float HIGH_TEXT_THRESHOLD = 0.85F;
    private static final int BYTES_PER_UNIT = 1024;
    private static final String UNIT_SYMBOLS = "BKMGTPE";
    private static final int UPDATE_INTERVAL = 20;

    private static RamBarTask instance;
    public final String RAM_BAR_TITLE = "<gray>Ram<yellow>:</yellow> <used>/<xmx> (<percent>)";
    public final BossBar.Overlay RAM_BAR_OVERLAY = BossBar.Overlay.NOTCHED_20;
    private final MemoryStats memoryStats = new MemoryStats();
    private int tickCounter = 0;

    public static RamBarTask instance() {
        if (instance == null) {
            instance = new RamBarTask();
        }
        return instance;
    }

    @Override
    BossBar createBossBar() {
        return BossBar.bossBar(Component.text(""), 0.0F, getCurrentState().barColor, RAM_BAR_OVERLAY);
    }

    @Override
    void updateBossBar(BossBar bossbar, Player player) {
        bossbar.progress(memoryStats.usagePercent);
        bossbar.color(getCurrentState().barColor);
        bossbar.name(MiniMessage.miniMessage().deserialize(RAM_BAR_TITLE,
                Placeholder.component("used", formatMemoryValue(memoryStats.used)),
                Placeholder.component("xmx", formatMemoryValue(memoryStats.maxMemory)),
                Placeholder.unparsed("percent", String.format("%d%%", (int) (memoryStats.usagePercent * 100)))
        ));
    }

    @Override
    public void run() {
        if (++tickCounter < UPDATE_INTERVAL) {
            return;
        }
        tickCounter = 0;
        memoryStats.update();
        super.run();
    }

    private MemoryState getCurrentState() {
        if (memoryStats.usagePercent < MEDIUM_MEMORY_THRESHOLD) {
            return MemoryState.GOOD;
        } else if (memoryStats.usagePercent < HIGH_MEMORY_THRESHOLD) {
            return MemoryState.MEDIUM;
        } else {
            return MemoryState.HIGH;
        }
    }

    private Component formatMemoryValue(long bytes) {
        if (bytes < BYTES_PER_UNIT) {
            return formatWithColor(bytes + "B");
        }
        int unitIndex = (63 - Long.numberOfLeadingZeros(bytes)) / 10;
        String value = String.format("%.1f%s",
                (double) bytes / (1L << (unitIndex * 10)),
                UNIT_SYMBOLS.charAt(unitIndex));
        return formatWithColor(value);
    }

    private Component formatWithColor(String text) {
        String colorTemplate = memoryStats.usagePercent < MEDIUM_TEXT_THRESHOLD ?
                MemoryState.GOOD.textColor :
                (memoryStats.usagePercent < HIGH_TEXT_THRESHOLD ?
                        MemoryState.MEDIUM.textColor :
                        MemoryState.HIGH.textColor);
        return MiniMessage.miniMessage().deserialize(colorTemplate,
                Placeholder.unparsed("text", text));
    }

    // Getters
    public long getAllocated() {
        return memoryStats.allocated;
    }

    public long getUsed() {
        return memoryStats.used;
    }

    public long getXmx() {
        return memoryStats.maxMemory;
    }

    public long getXms() {
        return memoryStats.initialMemory;
    }

    public float getPercent() {
        return memoryStats.usagePercent;
    }

    private enum MemoryState {
        GOOD(BossBar.Color.GREEN, "<gradient:#55ff55:#00aa00><text></gradient>"),
        MEDIUM(BossBar.Color.YELLOW, "<gradient:#ffff55:#ffaa00><text></gradient>"),
        HIGH(BossBar.Color.RED, "<gradient:#ff5555:#aa0000><text></gradient>");

        final BossBar.Color barColor;
        final String textColor;

        MemoryState(BossBar.Color barColor, String textColor) {
            this.barColor = barColor;
            this.textColor = textColor;
        }
    }

    private static class MemoryStats {
        private long allocated = 0L;
        private long used = 0L;
        private long maxMemory = 0L;
        private long initialMemory = 0L;
        private float usagePercent = 0F;

        void update() {
            MemoryUsage heap = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage();
            allocated = heap.getCommitted();
            used = heap.getUsed();
            maxMemory = heap.getMax();
            initialMemory = heap.getInit();
            usagePercent = Math.max(Math.min((float) used / maxMemory, 1.0F), 0.0F);
        }
    }
}