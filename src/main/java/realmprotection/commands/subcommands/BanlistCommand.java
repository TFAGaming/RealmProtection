package realmprotection.commands.subcommands;

import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import realmprotection.managers.LandBansManager;
import realmprotection.managers.LandsManager;
import realmprotection.utils.ColoredString;
import realmprotection.utils.LoadConfig;

public class BanlistCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (!LandsManager.hasLand(player.getName())) {
                player.sendMessage(LoadConfig.commandString("banlist.land_not_found"));
                return true;
            }

            String land_id = LandsManager.getLandDetail(player.getName(), "id");
            String land_name = LandsManager.getLandDetailById(new Integer(land_id), "land_name");

            List<List<String>> alldata = LandBansManager.listAllBannedPlayersData(new Integer(land_id));

            String repeatstyle = LoadConfig.commandStringWithoutPrefix("banlist.repeatstyle");
            String repeatstylefinalstring = "";

            for (List<String> data : alldata) {
                repeatstylefinalstring += repeatstyle.replace("%player%", data.get(0)).replace("%reason%", data.get(1));
            }

            List<String> stringlist = LoadConfig.commandStringList("banlist.stringlist");
            String finalstring = "";

            for (String string : stringlist) {
                if (string.length() == 0) {
                    finalstring += '\n';
                } else {
                    finalstring += string.replace("%land%", land_name).replace("%repeatstylelist%",
                            repeatstylefinalstring);
                }
            }

            player.sendMessage(ColoredString.translate(finalstring));

            return true;
        } else {
            return false;
        }
    }
}