package one.tranic.sewlia.task;

import io.papermc.paper.threadedregions.ThreadedRegionizer;
import io.papermc.paper.threadedregions.TickData;
import io.papermc.paper.threadedregions.TickRegions;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class TPSBarTask extends BossBarTask {
    private static TPSBarTask instance;
    private int tickCounter = 0;

    public static TPSBarTask instance() {
        if (instance == null) {
            instance = new TPSBarTask();
        }
        return instance;
    }

    @Override
    BossBar createBossBar() {
        double currentTPS = Bukkit.getTPS()[0];
        double averageTickTime = Bukkit.getAverageTickTime();
        return BossBar.bossBar(
                Component.text(""),
                0.0F,
                getColorForMetrics(currentTPS, averageTickTime),
                TPSConstants.PROGRESS_OVERLAY
        );
    }

    @Override
    void updateBossBar(BossBar bossbar, Player player) {
        TickData.TickReportData reportData = getPlayerRegionReport(player);
        if (reportData == null) return;

        TickData.SegmentData tpsData = reportData.tpsData().segmentAll();
        double mspt = calculateMSPT(reportData);

        updateBossBarState(bossbar, tpsData.average(), mspt, player.getPing(), player.getNearbyChunkHot());
    }

    private void updateBossBarState(BossBar bossbar, double tps, double mspt, int ping, long chunkHot) {
        bossbar.progress(calculateProgress(mspt, tps));
        bossbar.color(getColorForMetrics(tps, mspt));
        bossbar.name(createBarTitle(tps, mspt, ping, chunkHot));
    }

    private Component createBarTitle(double tps, double mspt, int ping, long chunkHot) {
        return MiniMessage.miniMessage().deserialize(
                TPSConstants.BAR_TITLE_FORMAT,
                Placeholder.component("tps", createColoredComponent(tps, FillMode.TPS)),
                Placeholder.component("mspt", createColoredComponent(mspt, FillMode.MSPT)),
                Placeholder.component("ping", createColoredComponent(ping, FillMode.PING)),
                Placeholder.component("chunkhot", createColoredComponent(chunkHot, FillMode.CHUNKHOT))
        );
    }

    private Component createColoredComponent(double value, FillMode mode) {
        String color = determineColor(value, mode);
        return MiniMessage.miniMessage().deserialize(color,
                Placeholder.parsed("text", String.format("%.2f", value)));
    }

    private Component createColoredComponent(long value, FillMode mode) {
        String color = determineColor(value, mode);
        return MiniMessage.miniMessage().deserialize(color,
                Placeholder.parsed("text", String.format("%,d", value)));
    }

    private String determineColor(double value, FillMode mode) {
        if (isGood(mode, value)) return TPSConstants.TEXT_COLOR_GOOD;
        if (isMedium(mode, value)) return TPSConstants.TEXT_COLOR_MEDIUM;
        return TPSConstants.TEXT_COLOR_LOW;
    }

    private TickData.TickReportData getPlayerRegionReport(Player player) {
        ServerPlayer nmsPlayer = ((CraftPlayer) player).getHandle();
        ThreadedRegionizer.ThreadedRegion<TickRegions.TickRegionData, TickRegions.TickRegionSectionData> region =
                ((ServerLevel) nmsPlayer.level()).regioniser.getRegionAtUnsynchronised(
                        nmsPlayer.moonrise$getSectionX(),
                        nmsPlayer.moonrise$getSectionZ()
                );
        if (region == null) return null;
        return region.getData().getRegionSchedulingHandle().getTickReport5s(System.nanoTime());
    }

    private double calculateMSPT(TickData.TickReportData reportData) {
        return reportData.timePerTickData().segmentAll().average() / 1.0E6;
    }

    private float calculateProgress(double mspt, double tps) {
        if (TPSConstants.PROGRESS_FILL_MODE == FillMode.MSPT) {
            return Math.max(Math.min((float) mspt / 50.0F, 1.0F), 0.0F);
        }
        return Math.max(Math.min((float) tps / 20.0F, 1.0F), 0.0F);
    }

    private BossBar.Color getColorForMetrics(double tps, double mspt) {
        if (isGood(TPSConstants.PROGRESS_FILL_MODE, tps)) return TPSConstants.COLOR_GOOD;
        if (isMedium(TPSConstants.PROGRESS_FILL_MODE, mspt)) return TPSConstants.COLOR_MEDIUM;
        return TPSConstants.COLOR_LOW;
    }

    @Override
    public void run() {
        if (++tickCounter < 20) return;
        tickCounter = 0;
        super.run();
    }

    private boolean isGood(FillMode mode, double value) {
        return switch (mode) {
            case MSPT -> value < 40;
            case TPS -> value >= 19;
            case PING -> value < 100;
            case CHUNKHOT -> value < 300000L;
        };
    }

    private boolean isMedium(FillMode mode, double value) {
        return switch (mode) {
            case MSPT -> value < 50;
            case TPS -> value >= 15;
            case PING -> value < 200;
            case CHUNKHOT -> value < 500000L;
        };
    }

    public enum FillMode {
        TPS, MSPT, PING, CHUNKHOT
    }

    private static class TPSConstants {
        static final BossBar.Overlay PROGRESS_OVERLAY = BossBar.Overlay.NOTCHED_20;
        static final FillMode PROGRESS_FILL_MODE = FillMode.MSPT;
        static final BossBar.Color COLOR_GOOD = BossBar.Color.GREEN;
        static final BossBar.Color COLOR_MEDIUM = BossBar.Color.YELLOW;
        static final BossBar.Color COLOR_LOW = BossBar.Color.RED;
        static final String TEXT_COLOR_GOOD = "<gradient:#55ff55:#00aa00><text></gradient>";
        static final String TEXT_COLOR_MEDIUM = "<gradient:#ffff55:#ffaa00><text></gradient>";
        static final String TEXT_COLOR_LOW = "<gradient:#ff5555:#aa0000><text></gradient>";
        static final String BAR_TITLE_FORMAT = "<gray>TPS<yellow>:</yellow> <tps> MSPT<yellow>:</yellow> <mspt> Ping<yellow>:</yellow> <ping>ms ChunkHot<yellow>:</yellow> <chunkhot>";
    }
}