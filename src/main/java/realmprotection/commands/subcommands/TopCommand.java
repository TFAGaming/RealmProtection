package realmprotection.commands.subcommands;

import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import realmprotection.managers.LandsManager;
import realmprotection.utils.ChatColorTranslator;
import realmprotection.utils.Language;

public class TopCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (LandsManager.getTotalLandsCount() == 0) {
                player.sendMessage(Language.getCommand("top.no_lands"));
                return true;
            }

            int limit = (int) Language.get("commands.top.__LIMIT__");

            List<List<Object>> alldata = LandsManager.getTopLands(limit);

            String repeatstyle = (String) Language.get("commands.top.__REPEAT_STYLE__");
            String repeatstylefinalstring = "";
            int rank = 1;

            for (List<Object> data : alldata) {
                repeatstylefinalstring += repeatstyle.replace("%rank%", "" + rank).replace("%land_id%", ""+ data.get(0))
                        .replace("%land_name%", (String) data.get(1)).replace("%land_owner%", Bukkit.getOfflinePlayer(UUID.fromString((String) data.get(2))).getName())
                        .replace("%chunks_count%", "" + data.get(3)).replace("%balance%", String.format("%.2f", data.get(4)));

                rank++;
            }

            @SuppressWarnings("unchecked")
            List<String> stringlist = (List<String>) Language.get("commands.top.__STRING_LIST__");
            String finalstring = "";

            for (String string : stringlist) {
                if (string.length() == 0) {
                    finalstring += '\n';
                } else {
                    finalstring += string.replace("%limit%", "" + limit).replace("%repeatstyle%",
                            repeatstylefinalstring).replace("%player_land_rank%", "" + (LandsManager.getPlayerLandRank(player) + 1));
                }
            }

            player.sendMessage(ChatColorTranslator.translate(finalstring));

            return true;
        } else {
            return false;
        }
    }
}
