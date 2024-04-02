package realmprotection.commands.subcommands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import realmprotection.RealmProtection;
import realmprotection.managers.LandsManager;
import realmprotection.utils.ColoredString;
import realmprotection.utils.LoadConfig;

public class DepositCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (!LoadConfig.isVaultPluginLoaded()) {
                player.sendMessage(LoadConfig.commandString("deposit.vault_plugin_not_ready"));
                return true;
            }

            if (!LandsManager.hasLand(player.getName())) {
                player.sendMessage(LoadConfig.commandString("deposit.land_not_found"));
                return true;
            }

            String land_id = LandsManager.getLandDetail(player.getName(), "id");
            double land_balance = Double.parseDouble(LandsManager.getLandDetailById(new Integer(land_id), "balance"));

            if (args.length == 1) {
                player.sendMessage(LoadConfig.commandString("deposit.no_amount_provided"));
                return true;
            }

            double player_balance = RealmProtection.getEconomy().getBalance(player);

            try {
                double balance_input = Double.parseDouble(args[1]);

                if (balance_input <= 0) {
                    player.sendMessage(LoadConfig.commandString("deposit.input_negative_or_zero"));
                    return true;
                }

                if (balance_input > player_balance) {
                    player.sendMessage(LoadConfig.commandString("deposit.input_larger_than_player_balance"));
                    return true;
                }

                LandsManager.updateBalance(new Integer(land_id), land_balance + balance_input);
                RealmProtection.getEconomy().withdrawPlayer(player, balance_input);

                player.sendMessage(LoadConfig.commandString("deposit.deposited_success").replace("%amount%", args[1]).replace("%land_balance%", String.format("%.2f", (land_balance + balance_input))));

            } catch (NumberFormatException err) {
                player.sendMessage(LoadConfig.commandString("deposit.input_not_double_or_integer"));
            }

            return true;
        } else {
            return false;
        }
    }
}
