package realmprotection.commands.subcommands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import realmprotection.managers.LandsManager;
import realmprotection.utils.Language;
import realmprotection.utils.VaultAPIEconomy;

public class WithdrawCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (!VaultAPIEconomy.isReady()) {
                player.sendMessage(Language.getCommand("withdraw.vault_plugin_not_ready"));
                return true;
            }

            if (!LandsManager.hasLand(player.getName())) {
                player.sendMessage(Language.getCommand("withdraw.land_not_found"));
                return true;
            }

            String land_id = LandsManager.getLandDetail(player.getName(), "id");
            double land_balance = Double.parseDouble(LandsManager.getLandDetailById(new Integer(land_id), "balance"));

            if (args.length == 1) {
                player.sendMessage(Language.getCommand("withdraw.no_amount_provided"));
                return true;
            }

            try {
                double balance_input = Double.parseDouble(args[1]);

                if (balance_input <= 0) {
                    player.sendMessage(Language.getCommand("withdraw.input_negative_or_zero"));
                    return true;
                }

                if (balance_input > land_balance) {
                    player.sendMessage(Language.getCommand("withdraw.input_larger_than_land_balance"));
                    return true;
                }

                LandsManager.updateBalance(new Integer(land_id), land_balance - balance_input);
                VaultAPIEconomy.getEconomy().depositPlayer(player, balance_input);

                player.sendMessage(Language.getCommand("withdraw.withdrew_success").replace("%amount%", args[1]).replace("%land_balance%", String.format("%.2f", (land_balance - balance_input))));

            } catch (NumberFormatException err) {
                player.sendMessage(Language.getCommand("withdraw.input_not_double_or_integer"));
            }

            return true;
        } else {
            return false;
        }
    }
}
