package realmprotection.commands.subcommands;

import org.bukkit.Chunk;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import realmprotection.managers.ChunksManager;
import realmprotection.managers.LandMembersManager;
import realmprotection.managers.LandsManager;
import realmprotection.managers.RolesManager;
import realmprotection.utils.LoadConfigString;

public class ClaimCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            Chunk chunk = player.getLocation().getChunk();

            boolean isClaimed = ChunksManager.isChunkClaimed(chunk);

            if (isClaimed) {
                player.sendMessage(LoadConfigString.load("claim.chunk_already_claimed"));
                return true;
            }

            if (!LandsManager.hasLand(player.getName())) {
                if (args.length == 1) {
                    player.sendMessage(LoadConfigString.load("claim.missing_land_name"));
                    return true;
                }

                if (LandsManager.landNameExist(args[1])) {
                    player.sendMessage(LoadConfigString.load("claim.land_name_already_taken"));
                    return true;
                }

                LandsManager.createNewLand(args[1], player.getName(), chunk.getWorld().getName(),
                        player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getZ());
            }

            String land_id = LandsManager.getLandDetail(player.getName(), "id");

            Boolean hasOneChunkClaimedForLand = ChunksManager.isLandHaveAtLeastOneChunk(new Integer(land_id));

            ChunksManager.claimNewChunk(chunk, new Integer(land_id));

            player.sendMessage(LoadConfigString.load("claim.chunk_claimed_success").replace("%chunk_x%", "" + chunk.getX()).replace("%chunk_z%", "" + chunk.getZ()));

            if (!hasOneChunkClaimedForLand) {
                RolesManager.createNewRole(new Integer(land_id), "Visitor", false);
                RolesManager.createNewRole(new Integer(land_id), "Member", true);
            }

            // Show particles
            boolean is_owner = false;
            boolean is_trusted = false;

            String land_owner_land = LandsManager.getLandDetailById(new Integer(land_id), "owner_name");

            if (player.getName().equalsIgnoreCase(land_owner_land))
                is_owner = true;
            if (LandMembersManager.isPlayerInTheLand(new Integer(land_id), player.getName()))
                is_trusted = true;

            ChunksManager.startParticleTask(player, new Integer(land_id), 1, is_owner, is_trusted);

            return true;
        } else {
            return false;
        }
    }
}
