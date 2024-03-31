package realmprotection.commands.subcommands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import realmprotection.managers.LandsManager;
import realmprotection.utils.ColoredString;
import realmprotection.utils.LoadConfig;

public class BalanceCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (!LoadConfig.isVaultPluginLoaded()) {
                player.sendMessage(ColoredString.translate(LoadConfig.commandString("balance.vault_plugin_not_ready")));
                return true;
            }

            if (!LandsManager.hasLand(player.getName())) {
                player.sendMessage(ColoredString.translate(LoadConfig.commandString("balance.land_not_found")));
                return true;
            }

            String land_id = LandsManager.getLandDetail(player.getName(), "id");
            double land_balance = Double.parseDouble(LandsManager.getLandDetailById(new Integer(land_id), "balance"));

            player.sendMessage(ColoredString.translate(LoadConfig.commandString("balance.balance_string").replace("%land_balance%", String.format("%.2f", land_balance))));

            return true;
        } else {
            return false;
        }
    }
}
