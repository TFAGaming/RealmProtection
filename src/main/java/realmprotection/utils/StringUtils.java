package realmprotection.utils;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Chunk;
import org.bukkit.entity.Player;

import realmprotection.RealmProtection;
import realmprotection.managers.ChunksManager;
import realmprotection.managers.LandsManager;

public class StringUtils {
    private static Set<UUID> cooldow_players = new HashSet<>();

    public static boolean isAlphanumericString(String input) {
        String regex = "^[a-zA-Z0-9]+$";

        return input.matches(regex);
    }

    public static void sendMessageToPlayerWithTimeout(Player player, String permission, Chunk claimed_chunk) {
        UUID player_uuid = player.getUniqueId();
        if (!cooldow_players.contains(player_uuid)) {
            String message = getConfigurationStringMessage(permission, player, claimed_chunk);

            player.sendMessage(ChatColorTranslator.translate(message));

            cooldow_players.add(player_uuid);

            player.getServer().getScheduler().runTaskLater(RealmProtection.getPlugin(RealmProtection.class),
                    () -> cooldow_players.remove(player_uuid), 40L);
        }
    }

    private static String getConfigurationStringMessage(String permission, Player player, Chunk claimed_chunk) {
        String final_string = "";

        String land_id = ChunksManager.getChunkDetail(claimed_chunk, "land_id");
        String land_name = LandsManager.getLandDetailById(new Integer(land_id), "land_name");

        String message = (String) Language.get("permissions.lands." + permission);

        if ((Boolean) Language.get("permissions.config.prefix.enabled")) {
            final_string += (String) Language.get("permissions.config.prefix.value");
        }

        final_string += message;

        if ((Boolean) Language.get("permissions.config.suffix.enabled")) {
            final_string += (String) Language.get("permissions.config.suffix.value");
        }

        return final_string.replace("%land_name%", land_name).replace("%flag%", permission);
    }
}