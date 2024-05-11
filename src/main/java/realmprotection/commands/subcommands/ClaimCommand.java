package realmprotection.commands.subcommands;

import java.util.List;

import org.bukkit.Chunk;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import realmprotection.RealmProtection;
import realmprotection.events.ChunksProtection;
import realmprotection.managers.ChunksManager;
import realmprotection.managers.LandMembersManager;
import realmprotection.managers.LandsManager;
import realmprotection.managers.RolesManager;
import realmprotection.utils.Language;
import realmprotection.utils.ParticleSpawner;
import realmprotection.utils.StringUtils;

public class ClaimCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            Chunk chunk = player.getLocation().getChunk();

            boolean isClaimed = ChunksManager.isChunkClaimed(chunk);

            if (isClaimed) {
                player.sendMessage(Language.getCommand("claim.chunk_already_claimed"));
                return true;
            }

            if (ChunksManager.isThereNeighborChunkClaimed(player) && !ChunksProtection.isPlayerAnOperator(player)) {
                player.sendMessage(Language.getCommand("claim.neighbor_chunk_claimed"));
                return true;
            }

            if (!LandsManager.hasLand(player.getUniqueId().toString())) {
                if (args.length == 1) {
                    player.sendMessage(Language.getCommand("claim.missing_land_name"));
                    return true;
                }

                if (!StringUtils.isAlphanumericString(args[1])) {
                    player.sendMessage(Language.getCommand("claim.land_name_not_alphanumeric"));
                    return true;
                }

                if (LandsManager.landNameExist(args[1])) {
                    player.sendMessage(Language.getCommand("claim.land_name_already_taken"));
                    return true;
                }

                LandsManager.createNewLand(args[1], player.getUniqueId().toString(), chunk.getWorld().getName(),
                        player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getZ());
            }

            if (!ChunksManager.hasEnoughChunksToClaim(player)) {
                player.sendMessage(Language.getCommand("claim.max_chunks_claimed"));
                return true;
            }

            String land_id = LandsManager.getLandDetail(player.getUniqueId().toString(), "id");

            boolean hasOneChunkClaimedBefore = ChunksManager.getChunksCountOfLand(new Integer(land_id)) == 0 ? false : true;

            ChunksManager.claimNewChunk(chunk, new Integer(land_id));

            player.sendMessage(Language.getCommand("claim.chunk_claimed_success")
                    .replace("%chunk_x%", "" + chunk.getX()).replace("%chunk_z%", "" + chunk.getZ()));

            if (!hasOneChunkClaimedBefore) {
                RealmProtection plugin = RealmProtection.getPlugin(RealmProtection.class);

                List<String> rolenames = plugin.getConfig().getStringList("roles.names");

                for (String rolename : rolenames) {
                    RolesManager.createNewRole(new Integer(land_id), rolename);
                }
            }

            // Show particles
            boolean is_owner = false;
            boolean is_trusted = false;

            String land_owner_land = LandsManager.getLandDetailById(new Integer(land_id), "owner_uuid");

            if (player.getUniqueId().toString().equals(land_owner_land))
                is_owner = true;
            if (LandMembersManager.isPlayerInTheLand(new Integer(land_id), player.getUniqueId().toString()))
                is_trusted = true;

            ParticleSpawner.spawnDelayedParticlesAroundClaimedChunk(player, new Integer(land_id), 1, is_owner, is_trusted);

            return true;
        } else {
            return false;
        }
    }
}
