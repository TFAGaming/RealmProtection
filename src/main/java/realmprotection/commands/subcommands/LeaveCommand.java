package realmprotection.commands.subcommands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import realmprotection.managers.LandMembersManager;
import realmprotection.managers.LandsManager;
import realmprotection.utils.Language;
import realmprotection.utils.StringValidator;

public class LeaveCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (args.length == 1) {
                player.sendMessage(Language.getCommand("leave.missing_land_name"));
                return true;
            }

            if (!StringValidator.isCleanString(args[1])) {
                player.sendMessage(Language.getCommand("leave.land_name_not_alphanumeric"));
                return true;
            }

            if (!LandsManager.landNameExist(args[1])) {
                player.sendMessage(Language.getCommand("leave.land_name_not_found"));
                return true;
            }

            String land_id = LandsManager.getLandDetailByLandName(args[1], "id");

            if (!LandMembersManager.isPlayerInTheLand(new Integer(land_id), player.getUniqueId().toString())) {
                player.sendMessage(Language.getCommand("leave.player_not_in_land"));
                return true;
            }

            LandMembersManager.removePlayerFromLand(new Integer(land_id), player.getUniqueId().toString());

            player.sendMessage(
                    Language.getCommand("leave.player_left_success").replace("%land_name%",
                            LandsManager.getLandDetailById(new Integer(land_id), "land_name")));

            return true;
        } else {
            return false;
        }
    }
}
