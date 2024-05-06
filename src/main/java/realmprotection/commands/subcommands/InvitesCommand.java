package realmprotection.commands.subcommands;

import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import realmprotection.managers.LandInvitesManager;
import realmprotection.utils.ChatColorTranslator;
import realmprotection.utils.Language;

public class InvitesCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            List<List<String>> alldata = LandInvitesManager.listAllInvitesForPlayer(player.getUniqueId().toString());

            if (alldata.size() == 0) {
                player.sendMessage(Language.getCommand("invites.no_invites"));
                return true;
            }

            String repeatstyle = (String) Language.get("commands.invites.__REPEAT_STYLE__");
            String repeatstylefinalstring = "";

            for (List<String> data : alldata) {
                repeatstylefinalstring += repeatstyle.replace("%inviter%", data.get(0)).replace("%land_id%", data.get(1)).replace("%land_name%", data.get(2));
            }

            @SuppressWarnings("unchecked")
            List<String> stringlist = (List<String>) Language.get("commands.invites.__STRING_LIST__");
            String finalstring = "";

            for (String string : stringlist) {
                if (string.length() == 0) {
                    finalstring += '\n';
                } else {
                    finalstring += string.replace("%player%", player.getName()).replace("%repeatstyle%",
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
