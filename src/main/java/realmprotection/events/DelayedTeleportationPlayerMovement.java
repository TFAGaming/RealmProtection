package realmprotection.events;

import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scheduler.BukkitTask;

import realmprotection.utils.ChatColorTranslator;
import realmprotection.utils.DelayedTeleportation;
import realmprotection.utils.Language;

public class DelayedTeleportationPlayerMovement implements Listener {
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        String prefix = "";

        if ((Boolean) Language.get("general.prefix.enabled") && (Boolean) Language.get("commands.__ALLOW_PREFIX__")) {
            prefix += (String) Language.get("general.prefix.value");
        }

        if (DelayedTeleportation.teleport_tasks.containsKey(player.getUniqueId())) {
            if (event.getFrom().getBlockX() != event.getTo().getBlockX()
                    || event.getFrom().getBlockZ() != event.getTo().getBlockZ()) {
                player.sendMessage(ChatColorTranslator.translate(
                        prefix + (String) Language.get("general.delayed_teleportation.messages.player_moved")));

                BukkitTask playerTask = DelayedTeleportation.teleport_tasks.get(player.getUniqueId());

                if (playerTask != null) {
                    playerTask.cancel();

                    DelayedTeleportation.teleport_tasks.remove(player.getUniqueId());
                    BossBar bossBar = DelayedTeleportation.teleport_bossbars.get(player.getUniqueId());

                    bossBar.removeAll();

                    DelayedTeleportation.teleport_bossbars.remove(player.getUniqueId());
                }
            }
        }
        ;
    }
}
