package realmprotection.commands.subcommands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import realmprotection.managers.LandInvitesManager;
import realmprotection.managers.LandMembersManager;
import realmprotection.managers.LandsManager;
import realmprotection.utils.Language;

public class UntrustCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (!LandsManager.hasLand(player.getName())) {
                player.sendMessage(Language.getCommand("untrust.land_not_found"));
                return true;
            }

            if (args.length == 1) {
                player.sendMessage(Language.getCommand("untrust.no_playername_provided"));
                return true;
            }

            String land_id = LandsManager.getLandDetail(player.getName(), "id");

            if (LandInvitesManager.isPlayerInvited(new Integer(land_id), args[1])) {
                LandInvitesManager.removeInviteFromPlayer(new Integer(land_id), args[1]);

                player.sendMessage(Language.getCommand("untrust.player_invite_removed_success").replace("%player%", args[1]));
                return true;
            }

            if (!LandMembersManager.isPlayerInTheLand(new Integer(land_id), args[1])) {
                player.sendMessage(Language.getCommand("untrust.playername_not_trusted"));
                return true;
            }

            LandMembersManager.removePlayerFromLand(new Integer(land_id), args[1]);

            player.sendMessage(Language.getCommand("untrust.player_untrusted_success").replace("%player%", args[1]));

            return true;
        } else {
            return false;
        }
    }
}
