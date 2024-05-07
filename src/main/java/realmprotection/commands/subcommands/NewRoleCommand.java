package realmprotection.commands.subcommands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import realmprotection.managers.LandsManager;
import realmprotection.managers.RolesManager;
import realmprotection.utils.Language;
import realmprotection.utils.StringUtils;

public class NewRoleCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (!LandsManager.hasLand(player.getUniqueId().toString())) {
                player.sendMessage(Language.getCommand("new_role.land_not_found"));
                return true;
            }

            if (RolesManager.hasReachedMaximumRolesCountForLand(player)) {
                player.sendMessage(Language.getCommand("new_role.max_roles_created"));
                return true;
            }

            if (args.length == 2) {
                player.sendMessage(Language.getCommand("new_role.no_role_provided"));
                return true;
            }

            String land_id = LandsManager.getLandDetail(player.getUniqueId().toString(), "id");

            if (RolesManager.hasRole(new Integer(land_id), args[2])) {
                player.sendMessage(Language.getCommand("new_role.role_found"));
                return true;
            }

            if (!StringUtils.isAlphanumericString(args[2])) {
                player.sendMessage(Language.getCommand("new_role.role_name_not_alphanumeric"));
                return true;
            }

            RolesManager.createNewRole(new Integer(land_id), args[2]);

            player.sendMessage(Language.getCommand("new_role.role_create_success"));

            return true;
        } else {
            return false;
        }
    }
}
