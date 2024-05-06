package realmprotection.commands.subcommands;

import org.bukkit.Chunk;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import realmprotection.managers.ChunksManager;
import realmprotection.managers.LandMembersManager;
import realmprotection.managers.LandsManager;
import realmprotection.utils.Language;

public class ViewCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            Chunk chunk = player.getLocation().getChunk();

            if (!ChunksManager.isChunkClaimed(chunk)) {
                player.sendMessage(Language.getCommand("view.chunk_wilderness"));
                return true;
            }

            boolean is_owner = false;
            boolean is_trusted = false;

            String land_id = ChunksManager.getChunkDetail(chunk, "land_id");

            String land_owner_land = LandsManager.getLandDetailById(new Integer(land_id), "owner_uuid");

            if (player.getUniqueId().toString().equals(land_owner_land))
                is_owner = true;
            if (LandMembersManager.isPlayerInTheLand(new Integer(land_id), player.getUniqueId().toString()))
                is_trusted = true;

            ChunksManager.startParticleTask(player, new Integer(land_id), 1, is_owner, is_trusted);

            player.sendMessage(Language.getCommand("view.particles_spawned_success"));

            return true;
        } else {
            return false;
        }
    }
}
