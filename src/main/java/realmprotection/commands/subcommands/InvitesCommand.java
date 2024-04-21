package realmprotection.commands.subcommands;

import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import realmprotection.managers.LandInvitesManager;
import realmprotection.utils.ColoredString;
import realmprotection.utils.LoadConfig;

public class InvitesCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            List<List<String>> alldata = LandInvitesManager.listAllInvitesForPlayer(player.getName());

            if (alldata.size() == 0) {
                player.sendMessage(LoadConfig.commandString("invites.no_invites"));
                return true;
            }

            String repeatstyle = LoadConfig.commandStringWithoutPrefix("invites.repeatstyle");
            String repeatstylefinalstring = "";

            for (List<String> data : alldata) {
                repeatstylefinalstring += repeatstyle.replace("%inviter%", data.get(0)).replace("%land_id%", data.get(1)).replace("%land_name%", data.get(2));
            }

            List<String> stringlist = LoadConfig.commandStringList("invites.stringlist");
            String finalstring = "";

            for (String string : stringlist) {
                if (string.length() == 0) {
                    finalstring += '\n';
                } else {
                    finalstring += string.replace("%player%", player.getName()).replace("%repeatstylelist%",
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
