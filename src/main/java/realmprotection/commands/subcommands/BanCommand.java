package realmprotection.commands.subcommands;

import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import realmprotection.managers.ChunksManager;
import realmprotection.managers.LandBansManager;
import realmprotection.managers.LandMembersManager;
import realmprotection.managers.LandsManager;
import realmprotection.utils.ChatColorTranslator;
import realmprotection.utils.Language;

public class BanCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (!LandsManager.hasLand(player.getUniqueId().toString())) {
                player.sendMessage(Language.getCommand("ban.land_not_found"));
                return true;
            }

            String land_id = LandsManager.getLandDetail(player.getUniqueId().toString(), "id");

            if (args.length == 1) {
                player.sendMessage(Language.getCommand("ban.no_playername_provided"));
                return true;
            }

            if (Bukkit.getPlayer(args[1]) == null) {
                player.sendMessage(Language.getCommand("ban.player_not_found"));
                return true;
            }

            if (LandBansManager.isPlayerBannedFromLand(new Integer(land_id),
                    Bukkit.getPlayer(args[1]).getUniqueId().toString())) {
                player.sendMessage(Language.getCommand("ban.player_already_banned"));
                return true;
            }

            String land_owner_uuid = LandsManager.getLandDetailById(new Integer(land_id), "owner_uuid");

            if (args[1].equals(land_owner_uuid)) {
                player.sendMessage(Language.getCommand("ban.player_owner_of_land"));
                return true;
            }

            String reason = "";

            if (args.length == 2) {
                reason = (String) Language.get("commands.ban.__DEFAULT_REASON__");
            } else {
                String[] newargs = Arrays.copyOfRange(args, 2, args.length);

                reason = String.join(" ", newargs);
            }

            LandBansManager.banPlayerFromLand(new Integer(land_id), Bukkit.getPlayer(args[1]).getUniqueId().toString(),
                    reason);

            if (LandMembersManager.isPlayerInTheLand(new Integer(land_id),
                    Bukkit.getPlayer(args[1]).getUniqueId().toString())) {
                LandMembersManager.removePlayerFromLand(new Integer(land_id),
                        Bukkit.getPlayer(args[1]).getUniqueId().toString());
            }

            player.sendMessage(Language.getCommand("ban.player_banned_success")
                    .replace("%player%", args[1])
                    .replace("%reason%", reason));

            if (LandMembersManager.isPlayerInTheLand(new Integer(land_id), Bukkit.getPlayer(args[1]).getUniqueId().toString())) {
                LandMembersManager.removePlayerFromLand(new Integer(land_id), Bukkit.getPlayer(args[1]).getUniqueId().toString());
            }

            // Checks if banned player is in the land chunk, it will teleport them out
            Player banned_player = Bukkit.getPlayer(args[1]);
            Chunk banned_player_chunk = banned_player.getLocation().getChunk();

            if (ChunksManager.isChunkClaimed(banned_player_chunk) && LandsManager
                    .getLandDetailById(new Integer(ChunksManager.getChunkDetail(banned_player_chunk, "land_id")),
                            "land_name")
                    .equals(LandsManager.getLandDetail(player.getUniqueId().toString(), "land_name"))) {
                ChunksManager.findUnclaimedChunkPositionAndTeleportPlayer(banned_player, new Integer(land_id));

                boolean isclaimed = true;

                while (isclaimed) {
                    Chunk newchunk = banned_player.getLocation().getChunk();

                    if (ChunksManager.isChunkClaimed(newchunk)) {
                        ChunksManager.findUnclaimedChunkPositionAndTeleportPlayer(banned_player, new Integer(land_id));
                    } else {
                        isclaimed = false;
                    }
                }

                String ban_reason = LandBansManager.getBanReason(new Integer(land_id),
                        banned_player.getUniqueId().toString());

                banned_player.sendMessage(
                        ChatColorTranslator.translate(
                                ((String) Language.get("general.player_chunk_entry.claimed.__PLAYER_BANNED__"))
                                        .replace("%land%",
                                                LandsManager.getLandDetailById(new Integer(land_id), "land_name"))
                                        .replace("%reason%", ban_reason)));
            }

            return true;
        } else {
            return false;
        }
    }
}