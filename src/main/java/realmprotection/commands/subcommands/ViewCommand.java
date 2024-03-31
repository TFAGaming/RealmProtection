package realmprotection.commands.subcommands;

import org.bukkit.Chunk;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import realmprotection.managers.ChunksManager;
import realmprotection.managers.LandMembersManager;
import realmprotection.managers.LandsManager;
import realmprotection.utils.LoadConfig;

public class ViewCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            Chunk chunk = player.getLocation().getChunk();

            if (!ChunksManager.isChunkClaimed(chunk)) {
                player.sendMessage(LoadConfig.commandString("view.chunk_wilderness"));
                return true;
            }

            boolean is_owner = false;
            boolean is_trusted = false;

            String land_id = ChunksManager.getChunkDetail(chunk, "land_id");

            String land_owner_land = LandsManager.getLandDetailById(new Integer(land_id), "owner_name");

            if (player.getName().equalsIgnoreCase(land_owner_land))
                is_owner = true;
            if (LandMembersManager.isPlayerInTheLand(new Integer(land_id), player.getName()))
                is_trusted = true;

            ChunksManager.startParticleTask(player, new Integer(land_id), 1, is_owner, is_trusted);

            player.sendMessage(LoadConfig.commandString("view.particles_spawned_success"));

            return true;
        } else {
            return false;
        }
    }
}
