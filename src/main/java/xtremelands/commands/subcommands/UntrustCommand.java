package xtremelands.commands.subcommands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import xtremelands.managers.LandMembersManager;
import xtremelands.managers.LandsManager;
import xtremelands.utils.LoadConfigString;

public class UntrustCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (!LandsManager.hasLand(player.getName())) {
                player.sendMessage(LoadConfigString.load("untrust.land_not_found"));
                return true;
            }

            if (args.length == 1) {
                player.sendMessage(LoadConfigString.load("untrust.no_playername_provided"));
                return true;
            }

            String land_id = LandsManager.getLandDetail(player.getName(), "id");

            if (!LandMembersManager.isPlayerInTheLand(new Integer(land_id), args[1])) {
                player.sendMessage(LoadConfigString.load("untrust.playername_not_found"));
                return true;
            }

            LandMembersManager.removePlayerFromLand(new Integer(land_id), args[1]);

            player.sendMessage(LoadConfigString.load("untrust.player_untrusted_success").replace("%player%", args[1]));

            return true;
        } else {
            return false;
        }
    }
}
