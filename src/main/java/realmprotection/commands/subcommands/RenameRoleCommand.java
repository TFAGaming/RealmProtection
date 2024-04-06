package realmprotection.commands.subcommands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import realmprotection.managers.LandsManager;
import realmprotection.managers.RolesManager;
import realmprotection.utils.LoadConfig;
import realmprotection.utils.StringValidator;

public class RenameRoleCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (!LandsManager.hasLand(player.getName())) {
                player.sendMessage(LoadConfig.commandString("rename_role.land_not_found"));
                return true;
            }

            if (args.length == 2) {
                player.sendMessage(LoadConfig.commandString("rename_role.no_role_provided"));
                return true;
            }

            String land_id = LandsManager.getLandDetail(player.getName(), "id");

            if (!RolesManager.hasRole(new Integer(land_id), args[2])) {
                player.sendMessage(LoadConfig.commandString("rename_role.role_not_found"));
                return true;
            }

            if (args[2].equalsIgnoreCase(LoadConfig.landRolesDefaultString("__DEFAULT_VISITOR_ROLE__"))) {
                player.sendMessage(LoadConfig.commandString("rename_role.role_name_is_visitor"));
                return true;
            }

            if (args.length == 3) {
                player.sendMessage(LoadConfig.commandString("rename_role.missing_new_role_name"));
                return true;
            }

            if (!StringValidator.isCleanString(args[3])) {
                player.sendMessage(LoadConfig.commandString("rename_role.role_name_not_alphanumeric"));
                return true;
            }

            String role_id = RolesManager.getRoleDetail(new Integer(land_id), args[2], "id");

            RolesManager.updateRoleName(new Integer(land_id), new Integer(role_id), args[3]);

            player.sendMessage(LoadConfig.commandString("rename_role.role_update_success").replace("%old%", args[2]).replace("%new%", args[3]));

            return true;
        } else {
            return false;
        }
    }
}
