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

            if (!VaultAPIEconomy.isEnabledInConfig() || !VaultAPIEconomy.isReady()) {
                player.sendMessage(Language.getCommand("__FEATURE_DISABLED__"));
                return true;
            }

            if (args.length == 1) {
                if (!LandsManager.hasLand(player.getUniqueId().toString())) {
                    player.sendMessage(Language.getCommand("balance.land_not_found"));
                    return true;
                }

                String land_id = LandsManager.getLandDetail(player.getUniqueId().toString(), "id");
                double land_balance = Double
                        .parseDouble(LandsManager.getLandDetailById(new Integer(land_id), "balance"));

                player.sendMessage(Language.getCommand("balance.balance_string_player_owns").replace("%land_balance%",
                        String.format("%.2f", land_balance)));
            } else {
                if (!LandsManager.landNameExist(args[1])) {
                    player.sendMessage(Language.getCommand("balance.land_name_not_found"));
                    return true;
                }

                String land_id = LandsManager.getLandDetailByLandName(args[1], "id");
                double land_balance = Double
                        .parseDouble(LandsManager.getLandDetailById(new Integer(land_id), "balance"));

                player.sendMessage(Language.getCommand("balance.balance_string_other_land").replace("%land_balance%",
                        String.format("%.2f", land_balance)).replace("%land%", args[1]));
            }

            return true;
        } else {
            return false;
        }
    }
}
