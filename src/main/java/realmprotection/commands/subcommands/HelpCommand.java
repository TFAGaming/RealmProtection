package realmprotection.commands.subcommands;

import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import realmprotection.utils.ChatColorTranslator;
import realmprotection.utils.Language;
public class HelpCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            
            @SuppressWarnings("unchecked")
            List<String> stringlist = (List<String>) Language.get("commands.help.__STRING_LIST__");
            String finalstring = "";

            for (String string : stringlist) {
                if (string.length() == 0) {
                    finalstring += '\n';
                } else {
                    finalstring += string;
                }
            }

            player.sendMessage(ChatColorTranslator.translate(finalstring));

            return true;
        } else {
            return false;
        }
    }
}
