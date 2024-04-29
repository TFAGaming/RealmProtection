package realmprotection.commands.subcommands;

import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import realmprotection.managers.LandBansManager;
import realmprotection.managers.LandsManager;
import realmprotection.utils.ChatColorTranslator;
import realmprotection.utils.Language;

public class BanlistCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (!LandsManager.hasLand(player.getName())) {
                player.sendMessage(Language.getCommand("banlist.land_not_found"));
                return true;
            }

            String land_id = LandsManager.getLandDetail(player.getName(), "id");
            String land_name = LandsManager.getLandDetailById(new Integer(land_id), "land_name");

            List<List<String>> alldata = LandBansManager.listAllBannedPlayersData(new Integer(land_id));

            if (alldata.size() == 0) {
                player.sendMessage(Language.getCommand("banlist.no_bans_found"));
                return true;
            }

            String repeatstyle = (String) Language.get("commands.banlist.__REPEAT_STYLE__");
            String repeatstylefinalstring = "";

            for (List<String> data : alldata) {
                repeatstylefinalstring += repeatstyle.replace("%player%", data.get(0)).replace("%reason%", data.get(1));
            }

            @SuppressWarnings("unchecked")
            List<String> stringlist = (List<String>) Language.get("commands.banlist.__STRING_LIST__");
            String finalstring = "";

            for (String string : stringlist) {
                if (string.length() == 0) {
                    finalstring += '\n';
                } else {
                    finalstring += string.replace("%land%", land_name).replace("%repeatstyle%",
                            repeatstylefinalstring);
                }
            }

            player.sendMessage(ChatColorTranslator.translate(finalstring));

            return true;
        } else {
            return false;
        }
    }
}