package realmprotection.commands.subcommands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import realmprotection.gui.RolesListPaginationGUI;
import realmprotection.managers.LandsManager;
import realmprotection.utils.Language;

public class RolesListCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (!LandsManager.hasLand(player.getUniqueId().toString())) {
                player.sendMessage(Language.getCommand("roles.land_not_found"));
                return true;
            }

            RolesListPaginationGUI.create(player);

            return true;
        } else {
            return false;
        }
    }
}