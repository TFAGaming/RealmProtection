package realmprotection.commands.subcommands;

import java.util.Arrays;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import realmprotection.managers.LandBansManager;
import realmprotection.managers.LandMembersManager;
import realmprotection.managers.LandsManager;
import realmprotection.utils.Language;

public class BanCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (!LandsManager.hasLand(player.getName())) {
                player.sendMessage(Language.getCommand("ban.land_not_found"));
                return true;
            }

            String land_id = LandsManager.getLandDetail(player.getName(), "id");

            if (args.length == 1) {
                player.sendMessage(Language.getCommand("ban.no_playername_provided"));
                return true;
            }
            
            if (LandBansManager.isPlayerBannedFromLand(new Integer(land_id), args[1])) {
                player.sendMessage(Language.getCommand("ban.player_already_banned"));
                return true;
            }

            String land_owner_name = LandsManager.getLandDetailById(new Integer(land_id), "owner_name");

            if (args[1].equalsIgnoreCase(land_owner_name)) {
                player.sendMessage(Language.getCommand("ban.player_owner_of_land"));
                return true;
            }

            String reason = "";

            if (args.length == 2) {
                reason = (String) Language.get("commands.ban.__DEFAULT_REASON__");
            } else {
                String[] newargs = Arrays.copyOfRange(args, 2, args.length);

                reason = String.join(" ", newargs);
            }

            LandBansManager.banPlayerFromLand(new Integer(land_id), args[1], reason);

            if (LandMembersManager.isPlayerInTheLand(new Integer(land_id), args[1])) {
                LandMembersManager.removePlayerFromLand(new Integer(land_id), args[1]);
            }

            player.sendMessage(Language.getCommand("ban.player_banned_success").replace("%player%", args[1]).replace("%reason%", reason));

            return true;
        } else {
            return false;
        }
    }
}