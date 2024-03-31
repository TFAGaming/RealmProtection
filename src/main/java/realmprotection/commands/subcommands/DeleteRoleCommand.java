package realmprotection.commands.subcommands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import realmprotection.managers.LandsManager;
import realmprotection.managers.RolesManager;
import realmprotection.utils.LoadConfig;

public class DeleteRoleCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (!LandsManager.hasLand(player.getName())) {
                player.sendMessage(LoadConfig.commandString("delete_role.land_not_found"));
                return true;
            }

            String land_id = LandsManager.getLandDetail(player.getName(), "id");

            Integer roles_counted = RolesManager.countRolesFromLand(new Integer(land_id));

            if (roles_counted <= 2) {
                player.sendMessage(LoadConfig.commandString("delete_role.min_roles_reached"));
                return true;
            }

            if (args.length == 2) {
                player.sendMessage(LoadConfig.commandString("delete_role.no_role_provided"));
                return true;
            }

            if (!RolesManager.hasRole(new Integer(land_id), args[2])) {
                player.sendMessage(LoadConfig.commandString("delete_role.role_not_found"));
                return true;
            }

            RolesManager.deleteRole(new Integer(land_id), args[2]);

            player.sendMessage(LoadConfig.commandString("delete_role.role_delete_success").replace("%role%", args[2]));

            return true;
        } else {
            return false;
        }
    }
}
