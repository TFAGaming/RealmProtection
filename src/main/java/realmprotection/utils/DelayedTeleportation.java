package realmprotection.utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.UUID;

import realmprotection.RealmProtection;

public class DelayedTeleportation extends JavaPlugin {
    public static HashMap<UUID, BukkitTask> teleport_tasks = new HashMap<>();
    public static HashMap<UUID, BossBar> teleport_bossbars = new HashMap<>();

    public static void teleportPlayerWithDelay(Player player, Location location, String message) {
        if (teleport_tasks.containsKey(player.getUniqueId())) {
            String prefix = "";

            if ((Boolean) Language.get("general.prefix.enabled")
                    && (Boolean) Language.get("commands.__ALLOW_PREFIX__")) {
                prefix += (String) Language.get("general.prefix.value");
            }

            player.sendMessage(ChatColorTranslator.translate(
                    prefix + (String) Language.get("general.delayed_teleportation.messages.already_teleporting")));

            return;
        }

        BossBar bossBar = Bukkit.createBossBar(
                ChatColorTranslator.translate((String) Language.get("general.delayed_teleportation.bossbar.title")),
                BarColor.valueOf((String) Language.get("general.delayed_teleportation.bossbar.color")),
                BarStyle.valueOf((String) Language.get("general.delayed_teleportation.bossbar.style")));
        bossBar.setProgress(1.0);
        bossBar.addPlayer(player);

        BukkitTask task = Bukkit.getScheduler().runTaskTimer(RealmProtection.getPlugin(RealmProtection.class), () -> {
            double progress = bossBar.getProgress();
            progress -= 1.0 / 60;

            if (getTwoDigits(progress) == 25 || getTwoDigits(progress) == 50 || getTwoDigits(progress) == 75) {
                player.playSound(player.getLocation(), Sound.BLOCK_LEVER_CLICK, 500.0f, 1.0f);
            }

            if (progress <= 0) {
                player.teleport(location);
                bossBar.removeAll();

                BukkitTask playerTask = teleport_tasks.get(player.getUniqueId());

                if (playerTask != null) {
                    playerTask.cancel();

                    teleport_tasks.remove(player.getUniqueId());
                    teleport_bossbars.remove(player.getUniqueId());

                    if (message != null) {
                        player.sendMessage(message);
                    }
                }
            } else {
                bossBar.setProgress(progress);
            }
        }, 0L, 1L);

        teleport_tasks.put(player.getUniqueId(), task);
        teleport_bossbars.put(player.getUniqueId(), bossBar);
    }

    private static int getTwoDigits(double number) {
        return (int) ((number + 0.001) * 100) % 100;
    }
}
