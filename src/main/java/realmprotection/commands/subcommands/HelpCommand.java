package realmprotection.commands.subcommands;

import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import realmprotection.utils.ColoredString;
import realmprotection.utils.LoadConfig;
public class HelpCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            
            List<String> stringlist = LoadConfig.commandStringList("help.stringlist");
            String finalstring = "";

            for (String string : stringlist) {
                if (string.length() == 0) {
                    finalstring += '\n';
                } else {
                    finalstring += string;
                }
            }

            player.sendMessage(ColoredString.translate(finalstring));

            return true;
        } else {
            return false;
        }
    }
}
