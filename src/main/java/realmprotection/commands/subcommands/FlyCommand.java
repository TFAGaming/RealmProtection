package realmprotection.commands.subcommands;

import org.bukkit.Chunk;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import realmprotection.managers.ChunksManager;
import realmprotection.managers.LandsManager;
import realmprotection.utils.LoadConfig;

public class FlyCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            Chunk chunk = player.getLocation().getChunk();

            if (!LandsManager.hasLand(player.getName())) {
                player.sendMessage(LoadConfig.commandString("fly.land_not_found"));
                return true;
            }

            if (!ChunksManager.isChunkClaimed(chunk)) {
                player.sendMessage(LoadConfig.commandString("fly.chunk_not_claimed"));
                return true;
            }

            String chunk_owner_name = ChunksManager.getOwnerByChunk(chunk);

            if (!chunk_owner_name.equalsIgnoreCase(player.getName())) {
                player.sendMessage(LoadConfig.commandString("fly.chunk_not_owned_by_player"));
                return true;
            }

            if (player.getAllowFlight()) {
                player.setAllowFlight(false);

                player.sendMessage(LoadConfig.commandString("fly.fly_disabled_success"));
            } else {
                player.setAllowFlight(true);

                player.sendMessage(LoadConfig.commandString("fly.fly_enabled_success"));
            }

            return true;
        } else {
            return false;
        }
    }
}
