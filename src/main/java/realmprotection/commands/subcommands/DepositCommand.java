package realmprotection.commands.subcommands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import realmprotection.managers.LandsManager;
import realmprotection.utils.Language;
import realmprotection.utils.VaultAPIEconomy;

public class DepositCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (!VaultAPIEconomy.isReady()) {
                player.sendMessage(Language.getCommand("deposit.vault_plugin_not_ready"));
                return true;
            }

            if (!LandsManager.hasLand(player.getUniqueId().toString())) {
                player.sendMessage(Language.getCommand("deposit.land_not_found"));
                return true;
            }

            String land_id = LandsManager.getLandDetail(player.getUniqueId().toString(), "id");
            double land_balance = Double.parseDouble(LandsManager.getLandDetailById(new Integer(land_id), "balance"));

            if (args.length == 1) {
                player.sendMessage(Language.getCommand("deposit.no_amount_provided"));
                return true;
            }

            double player_balance = VaultAPIEconomy.getEconomy().getBalance(player);

            try {
                double balance_input = Double.parseDouble(args[1]);

                if (balance_input <= 0) {
                    player.sendMessage(Language.getCommand("deposit.input_negative_or_zero"));
                    return true;
                }

                if (balance_input > player_balance) {
                    player.sendMessage(Language.getCommand("deposit.input_larger_than_player_balance"));
                    return true;
                }

                LandsManager.updateBalance(new Integer(land_id), land_balance + balance_input);
                VaultAPIEconomy.getEconomy().withdrawPlayer(player, balance_input);

                player.sendMessage(Language.getCommand("deposit.deposited_success").replace("%amount%", args[1]).replace("%land_balance%", String.format("%.2f", (land_balance + balance_input))));

            } catch (NumberFormatException err) {
                player.sendMessage(Language.getCommand("deposit.input_not_double_or_integer"));
            }

            return true;
        } else {
            return false;
        }
    }
}
