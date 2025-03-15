package one.tranic.sewlia.task;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import net.kyori.adventure.bossbar.BossBar;
import net.minecraft.server.level.ServerPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public abstract class BossBarTask implements Runnable {
    private final Object2ObjectOpenHashMap<UUID, BossBar> listBossbar = new Object2ObjectOpenHashMap<>();
    private final Logger logger = LoggerFactory.getLogger("BossBarTask");
    private Thread thread;
    private boolean running = false;

    public static void startAll() {
        TPSBarTask.instance().start();
        RamBarTask.instance().start();
    }

    public static void stopAll() {
        TPSBarTask.instance().stop();
        RamBarTask.instance().stop();
    }

    public static void addToAll(ServerPlayer player) {
        Player bukkit = player.getBukkitEntity();
        if (player.tpsBar()) TPSBarTask.instance().addPlayer(bukkit);
        if (player.ramBar()) RamBarTask.instance().addPlayer(bukkit);
    }

    public static void removeFromAll(Player player) {
        TPSBarTask.instance().removePlayer(player);
        RamBarTask.instance().removePlayer(player);
    }

    abstract BossBar createBossBar();

    abstract void updateBossBar(BossBar bossbar, Player player);

    @Override
    public void run() {
        ObjectIterator<Map.Entry<UUID, BossBar>> iter = listBossbar.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry<UUID, BossBar> entry = iter.next();
            Player player = Bukkit.getPlayer(entry.getKey());
            if (player == null) {
                iter.remove();
                continue;
            }
            updateBossBar(entry.getValue(), player);
        }
    }

    private void cancel() {
        running = false;
        if (null != this.thread) {
            int i = 0;
            try {
                while (this.thread.isAlive()) {

                    this.thread.join(1000L);
                    if (++i >= 5) {
                        logger.warn("Waited {} seconds attempting force stop!", i);
                    } else if (this.thread.isAlive()) {
                        logger.warn("Thread {} ({}) failed to exit after {} second(s)", this, this.thread.getState(), i, new Exception("Stack:"));
                        this.thread.interrupt();
                    }
                }
            } catch (InterruptedException ignored) {
            }
            this.thread = null;
        }
        new ObjectArraySet<>(this.listBossbar.keySet()).forEach(uuid -> {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null) removePlayer(player);
        });
        this.listBossbar.clear();
    }

    public boolean removePlayer(Player player) {
        BossBar bossbar = this.listBossbar.remove(player.getUniqueId());
        if (bossbar != null) {
            player.hideBossBar(bossbar);
            return true;
        }
        return false;
    }

    public void addPlayer(Player player) {
        removePlayer(player);
        BossBar bossbar = createBossBar();
        this.listBossbar.put(player.getUniqueId(), bossbar);
        this.updateBossBar(bossbar, player);
        player.showBossBar(bossbar);
    }

    public boolean hasPlayer(UUID uuid) {
        return this.listBossbar.containsKey(uuid);
    }

    public boolean togglePlayer(Player player) {
        if (removePlayer(player)) return false;
        addPlayer(player);
        return true;
    }

    public void start() {
        stop();
        running = true;
        thread = Thread.ofVirtual().name("Sewlia Bossbar Task Thread").unstarted(() -> {
            try {
                while (running) {
                    this.run();
                    TimeUnit.MILLISECONDS.sleep(75);
                }
            } catch (InterruptedException ignored) {
            }
        });
        thread.start();
    }

    public void stop() {
        cancel();
    }
}