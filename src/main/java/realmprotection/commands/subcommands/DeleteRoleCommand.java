package realmprotection.commands.subcommands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import realmprotection.RealmProtection;
import realmprotection.managers.LandMembersManager;
import realmprotection.managers.LandsManager;
import realmprotection.managers.RolesManager;
import realmprotection.utils.Language;

public class DeleteRoleCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (!LandsManager.hasLand(player.getUniqueId().toString())) {
                player.sendMessage(Language.getCommand("delete_role.land_not_found"));
                return true;
            }

            String land_id = LandsManager.getLandDetail(player.getUniqueId().toString(), "id");

            int roles_counted = RolesManager.countRolesFromLand(new Integer(land_id));

            if (roles_counted <= 2) {
                player.sendMessage(Language.getCommand("delete_role.min_roles_reached"));
                return true;
            }

            if (args.length == 2) {
                player.sendMessage(Language.getCommand("delete_role.no_role_provided"));
                return true;
            }

            if (!RolesManager.hasRole(new Integer(land_id), args[2])) {
                player.sendMessage(Language.getCommand("delete_role.role_not_found"));
                return true;
            }

            RealmProtection plugin = RealmProtection.getPlugin(RealmProtection.class);

            if (args[2].equalsIgnoreCase(plugin.getConfig().getString("roles.__DEFAULT_VISITOR_ROLE__"))) {
                player.sendMessage(Language.getCommand("delete_role.role_name_is_visitor"));
                return true;
            }

            if (LandMembersManager.hasAtLeastOneRole(new Integer(land_id), args[2])) {
                player.sendMessage(Language.getCommand("delete_role.role_has_member_with_it"));
                return true;
            }

            RolesManager.deleteRole(new Integer(land_id), args[2]);

            player.sendMessage(Language.getCommand("delete_role.role_delete_success").replace("%role%", args[2]));

            return true;
        } else {
            return false;
        }
    }
}
