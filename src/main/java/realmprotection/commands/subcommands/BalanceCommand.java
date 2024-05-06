package realmprotection.commands.subcommands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import realmprotection.managers.LandsManager;
import realmprotection.utils.Language;
import realmprotection.utils.VaultAPIEconomy;

public class BalanceCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (!VaultAPIEconomy.isReady()) {
                player.sendMessage(Language.getCommand("balance.vault_plugin_not_ready"));
                return true;
            }

            if (!LandsManager.hasLand(player.getUniqueId().toString())) {
                player.sendMessage(Language.getCommand("balance.land_not_found"));
                return true;
            }

            String land_id = LandsManager.getLandDetail(player.getUniqueId().toString(), "id");
            double land_balance = Double.parseDouble(LandsManager.getLandDetailById(new Integer(land_id), "balance"));

            player.sendMessage(Language.getCommand("balance.balance_string").replace("%land_balance%", String.format("%.2f", land_balance)));

            return true;
        } else {
            return false;
        }
    }
}
