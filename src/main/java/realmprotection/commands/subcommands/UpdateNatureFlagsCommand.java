package realmprotection.commands.subcommands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import realmprotection.gui.NatureFlagsGUI;
import realmprotection.managers.LandsManager;
import realmprotection.utils.Language;

public class UpdateNatureFlagsCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (!LandsManager.hasLand(player.getUniqueId().toString())) {
                player.sendMessage(Language.getCommand("update_nature_flags.land_not_found"));
            } else {
                String land_id = LandsManager.getLandDetail(player.getUniqueId().toString(), "id");

                if (args.length == 2) {
                    NatureFlagsGUI.create(player);
                } else {
                    List<String> listofflags = new ArrayList<>();

                    listofflags.add("hostilemobsspawn");
                    listofflags.add("passivemobsspawn");
                    listofflags.add("leavesdecay");
                    listofflags.add("firespread");
                    listofflags.add("liquidflow");
                    listofflags.add("tntblockdamage");
                    listofflags.add("respawnanchorblockdamage");
                    listofflags.add("pistonsfromwilderness");
                    listofflags.add("dispensersfromwilderness");
                    listofflags.add("plantgrowth");

                    if (!listofflags.contains(args[2])) {
                        player.sendMessage(Language.getCommand("update_nature_flags.not_valid_flag"));
                        return true;
                    }

                    if (args.length == 3) {
                        player.sendMessage(Language.getCommand("update_nature_flags.missing_boolean_value"));
                        return true;
                    }

                    if (!(args[3].equalsIgnoreCase("true") || args[3].equalsIgnoreCase("false")
                            || args[3].equalsIgnoreCase("1") || args[3].equalsIgnoreCase("0"))) {
                        player.sendMessage(Language.getCommand("update_nature_flags.not_boolean_value"));
                        return true;
                    }

                    boolean value;

                    if (args[3].equalsIgnoreCase("true") || args[3].equalsIgnoreCase("1")) {
                        value = true;
                    } else {
                        value = false;
                    }

                    LandsManager.updateNatureFlagValue(new Integer(land_id), args[2], value);

                    player.sendMessage(Language.getCommand("update_nature_flags.nature_flag_updated_success")
                            .replace("%flag%", args[2]).replace("%new_value%", "" + value));

                }
            }

            return true;
        } else {
            return false;
        }
    }
}
