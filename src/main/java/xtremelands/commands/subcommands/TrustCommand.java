package xtremelands.commands.subcommands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import xtremelands.managers.LandMembersManager;
import xtremelands.managers.LandsManager;
import xtremelands.managers.RolesManager;
import xtremelands.utils.LoadConfigString;

public class TrustCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (!LandsManager.hasLand(player.getName())) {
                player.sendMessage(LoadConfigString.load("trust.land_not_found"));
                return true;
            }

            if (args.length == 1) {
                player.sendMessage(LoadConfigString.load("trust.no_playername_provided"));
                return true;
            }

            Player player_searched = Bukkit.getPlayer(args[1]);

            if (player_searched == null) {
                player.sendMessage(LoadConfigString.load("trust.playername_not_found"));
                return true;
            }

            String land_id = LandsManager.getLandDetail(player.getName(), "id");
            String land_owner_name = LandsManager.getLandDetail(player.getName(), "id");

            if (LandMembersManager.isPlayerInTheLand(new Integer(land_id), args[1])) {
                player.sendMessage(LoadConfigString.load("trust.playername_already_trusted"));
                return true;
            }

            if (args[1].equalsIgnoreCase(land_owner_name)) {
                player.sendMessage(LoadConfigString.load("trust.playername_owner_of_land"));
                return true;
            }

            if (args.length == 2) {
                player.sendMessage(LoadConfigString.load("trust.no_role_provided"));
                return true;
            }

            if (!RolesManager.hasRole(new Integer(land_id), args[2])) {
                player.sendMessage(LoadConfigString.load("trust.role_not_found"));
                return true;
            }

            if (args[2].equalsIgnoreCase("Visitor")) {
                player.sendMessage(LoadConfigString.load("trust.player_cannot_have_default_role"));
                return true;
            }

            LandMembersManager.invitePlayerToLand(new Integer(land_id), args[1], args[2]);

            player.sendMessage(LoadConfigString.load("trust.player_trusted_success").replace("%player%", args[1]));

            return true;
        } else {
            return false;
        }
    }
}
