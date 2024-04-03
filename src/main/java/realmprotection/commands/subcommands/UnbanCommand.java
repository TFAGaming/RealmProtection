package realmprotection.commands.subcommands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import realmprotection.managers.LandBansManager;
import realmprotection.managers.LandsManager;
import realmprotection.utils.LoadConfig;

public class UnbanCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (!LandsManager.hasLand(player.getName())) {
                player.sendMessage(LoadConfig.commandString("unban.land_not_found"));
                return true;
            }

            String land_id = LandsManager.getLandDetail(player.getName(), "id");

            if (args.length == 1) {
                player.sendMessage(LoadConfig.commandString("unban.no_playername_provided"));
                return true;
            }
            
            if (!LandBansManager.isPlayerBannedFromLand(new Integer(land_id), args[1])) {
                player.sendMessage(LoadConfig.commandString("unban.player_not_banned"));
                return true;
            }

            LandBansManager.unbanPlayerFromLand(new Integer(land_id), args[1]);

            player.sendMessage(LoadConfig.commandString("unban.player_unbanned_success").replace("%player%", args[1]));

            return true;
        } else {
            return false;
        }
    }
}