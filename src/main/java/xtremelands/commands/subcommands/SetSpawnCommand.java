package xtremelands.commands.subcommands;

import org.bukkit.Chunk;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import xtremelands.managers.ChunksManager;
import xtremelands.managers.LandsManager;
import xtremelands.utils.LoadConfigString;

public class SetSpawnCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            Chunk chunk = player.getLocation().getChunk();

            if (!LandsManager.hasLand(player.getName())) {
                player.sendMessage(LoadConfigString.load("set_spawn.land_not_found"));
                return true;
            }

            if (!ChunksManager.isChunkClaimed(chunk)) {
                player.sendMessage(LoadConfigString.load("set_spawn.chunk_not_claimed"));
                return true;
            }

            String land_id = ChunksManager.getChunkDetail(chunk, "land_id");
            String land_owner_name = LandsManager.getLandDetailById(new Integer(land_id), "owner_name");

            if (!land_owner_name.equalsIgnoreCase(player.getName())) {
                player.sendMessage(LoadConfigString.load("set_spawn.not_owner_of_chunk"));
                return true;
            }

            String player_world = player.getLocation().getWorld().getName();
            double player_x = player.getLocation().getX();
            double player_y = player.getLocation().getY();
            double player_z = player.getLocation().getZ();

            LandsManager.updateSpawnLocation(new Integer(land_id), player_x, player_y, player_z, player_world);

            player.sendMessage(LoadConfigString.load("set_spawn.spawn_set_success").replace("%x%", "" + player_x).replace("%y%", "" + player_y).replace("%z%", "" + player_z));

            return true;
        } else {
            return false;
        }
    }
}
