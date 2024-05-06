package realmprotection.commands.subcommands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import realmprotection.managers.LandsManager;
import realmprotection.utils.Language;
import realmprotection.utils.StringValidator;

public class RenameCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (!LandsManager.hasLand(player.getUniqueId().toString())) {
                player.sendMessage(Language.getCommand("rename.land_not_found"));
                return true;
            }

            if (args.length == 1) {
                player.sendMessage(Language.getCommand("rename.missing_new_land_name"));
                return true;
            }

            if (LandsManager.landNameExist(args[1])) {
                player.sendMessage(Language.getCommand("rename.land_name_already_taken"));
                return true;
            }

            if (!StringValidator.isCleanString(args[1])) {
                player.sendMessage(Language.getCommand("rename.land_name_not_alphanumeric"));
                return true;
            }

            String land_id = LandsManager.getLandDetail(player.getUniqueId().toString(), "id");

            LandsManager.updateLandName(new Integer(land_id), args[1]);

            player.sendMessage(Language.getCommand("rename.land_name_update_success").replace("%new%", args[1]));

            return true;
        } else {
            return false;
        }
    }
}
