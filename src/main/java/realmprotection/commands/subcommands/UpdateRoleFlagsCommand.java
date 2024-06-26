package realmprotection.commands.subcommands;

import java.util.List;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import realmprotection.gui.RoleFlagsGUI;
import realmprotection.managers.LandsManager;
import realmprotection.managers.RolesManager;
import realmprotection.utils.Language;

public class UpdateRoleFlagsCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (!LandsManager.hasLand(player.getUniqueId().toString())) {
                player.sendMessage(Language.getCommand("update_role_flags.land_not_found"));
            } else {
                if (args.length == 2) {
                    player.sendMessage(Language.getCommand("update_role_flags.no_role_provided"));
                    return true;
                }

                String land_id = LandsManager.getLandDetail(player.getUniqueId().toString(), "id");

                if (!RolesManager.hasRole(new Integer(land_id), args[2])) {
                    player.sendMessage(Language.getCommand("update_role_flags.role_not_found"));
                    return true;
                }

                if (args.length == 3) {
                    RoleFlagsGUI.create(player, args[2], 0);
                } else {
                    List<String> listofflags = RolesManager.listAllPermissions();

                    if (!listofflags.contains(args[3])) {
                        player.sendMessage(Language.getCommand("update_role_flags.not_valid_flag"));
                        return true;
                    }

                    if (args.length == 4) {
                        player.sendMessage(Language.getCommand("update_role_flags.missing_boolean_value"));
                        return true;
                    }

                    if (!(args[4].equalsIgnoreCase("true") || args[4].equalsIgnoreCase("false")
                            || args[4].equalsIgnoreCase("1") || args[4].equalsIgnoreCase("0"))) {
                        player.sendMessage(Language.getCommand("update_role_flags.not_boolean_value"));
                        return true;
                    }

                    boolean value;

                    if (args[4].equalsIgnoreCase("true") || args[4].equalsIgnoreCase("1")) {
                        value = true;
                    } else {
                        value = false;
                    }

                    RolesManager.updatePermissionValue(new Integer(land_id), args[2], args[3], value);

                    player.sendMessage(Language.getCommand("update_role_flags.role_flag_updated_success")
                            .replace("%flag%", args[3]).replace("%role_name%", args[2])
                            .replace("%new_value%", "" + value));

                }
            }

            return true;
        } else {
            return false;
        }
    }
}
