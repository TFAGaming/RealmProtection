package realmprotection.commands.subcommands;

import org.bukkit.Chunk;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import realmprotection.managers.ChunksManager;
import realmprotection.managers.LandsManager;
import realmprotection.managers.RolesManager;
import realmprotection.utils.LoadConfigString;

public class UnclaimCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            Chunk chunk = player.getLocation().getChunk();

            if (!LandsManager.hasLand(player.getName())) {
                player.sendMessage(LoadConfigString.load("unclaim.land_not_found"));
                return true;
            }

            boolean isClaimed = ChunksManager.isChunkClaimed(chunk);

            if (!isClaimed) {
                player.sendMessage(LoadConfigString.load("unclaim.chunk_not_claimed"));
                return true;
            }

            String land_id = LandsManager.getLandDetail(player.getName(), "id");
            String chunk_land_id = ChunksManager.getChunkDetail(chunk, "land_id");
            String land_owner_name = LandsManager.getLandDetailById(new Integer(chunk_land_id), "owner_name");

            if (isClaimed && !land_owner_name.equalsIgnoreCase(player.getName())) {
                player.sendMessage(LoadConfigString.load("unclaim.chunk_not_owned_by_sender"));
                return true;
            }

            if (args.length == 1 || !args[1].equalsIgnoreCase("confirm")) {
                player.sendMessage(LoadConfigString.load("unclaim.confirm_missing"));
                return true;
            }

            ChunksManager.removeClaimedChunk(chunk);

            player.sendMessage(LoadConfigString.load("unclaim.chunk_unclaimed_success"));

            Boolean hasOneChunkClaimedForLand = ChunksManager.isLandHaveAtLeastOneChunk(new Integer(land_id));

            if (!hasOneChunkClaimedForLand) {
                LandsManager.deleteLand(new Integer(land_id));
                RolesManager.deleteAllRolesFromLand(new Integer(land_id));

                player.sendMessage(LoadConfigString.load("unclaim.show_note_land_deleted"));
            }

            return true;
        } else {
            return false;
        }
    }
}
