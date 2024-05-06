package realmprotection.commands.subcommands;

import org.bukkit.Chunk;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import realmprotection.managers.ChunksManager;
import realmprotection.managers.LandBansManager;
import realmprotection.managers.LandInvitesManager;
import realmprotection.managers.LandMembersManager;
import realmprotection.managers.LandsManager;
import realmprotection.managers.RolesManager;
import realmprotection.utils.Language;

public class UnclaimCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            Chunk chunk = player.getLocation().getChunk();

            if (!LandsManager.hasLand(player.getUniqueId().toString())) {
                player.sendMessage(Language.getCommand("unclaim.land_not_found"));
                return true;
            }

            boolean isClaimed = ChunksManager.isChunkClaimed(chunk);

            if (!isClaimed) {
                player.sendMessage(Language.getCommand("unclaim.chunk_not_claimed"));
                return true;
            }

            String land_id = LandsManager.getLandDetail(player.getUniqueId().toString(), "id");
            String chunk_land_id = ChunksManager.getChunkDetail(chunk, "land_id");
            String land_owner_uuid = LandsManager.getLandDetailById(new Integer(chunk_land_id), "owner_uuid");

            if (isClaimed && !land_owner_uuid.equals(player.getUniqueId().toString())) {
                player.sendMessage(Language.getCommand("unclaim.chunk_not_owned_by_sender"));
                return true;
            }

            if (args.length == 1 || !args[1].equalsIgnoreCase("confirm")) {
                player.sendMessage(Language.getCommand("unclaim.confirm_missing"));
                return true;
            }

            ChunksManager.removeClaimedChunk(chunk);

            player.sendMessage(Language.getCommand("unclaim.chunk_unclaimed_success"));

            boolean hasOneChunkClaimedForLand = ChunksManager.isLandHaveAtLeastOneChunk(new Integer(land_id));

            if (!hasOneChunkClaimedForLand) {
                LandsManager.deleteLand(new Integer(land_id));
                RolesManager.deleteAllRolesFromLand(new Integer(land_id));
                LandMembersManager.deleteAllMembersFromLand(new Integer(land_id));
                LandInvitesManager.deleteAllInvitesFromLand(new Integer(land_id));
                LandBansManager.deleteAllBannedPlayersFromLand(new Integer(land_id));
                ChunksManager.deleteAllChunksFromLand(new Integer(land_id));

                player.sendMessage(Language.getCommand("unclaim.show_note_land_deleted"));
            } else {
                // Show particles
                boolean is_owner = false;
                boolean is_trusted = false;
                
                if (player.getUniqueId().toString().equals(land_owner_uuid))
                    is_owner = true;
                if (LandMembersManager.isPlayerInTheLand(new Integer(land_id), player.getUniqueId().toString()))
                    is_trusted = true;

                ChunksManager.startParticleTask(player, new Integer(land_id), 1, is_owner, is_trusted);
            }

            return true;
        } else {
            return false;
        }
    }
}
