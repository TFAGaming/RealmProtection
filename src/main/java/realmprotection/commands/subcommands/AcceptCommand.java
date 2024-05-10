package realmprotection.commands.subcommands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import realmprotection.managers.LandInvitesManager;
import realmprotection.managers.LandMembersManager;
import realmprotection.managers.LandsManager;
import realmprotection.managers.RolesManager;
import realmprotection.utils.Language;

public class AcceptCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (args.length == 1) {
                player.sendMessage(Language.getCommand("accept.missing_land_name"));
                return true;
            }

            if (!LandsManager.landNameExist(args[1])) {
                player.sendMessage(Language.getCommand("accept.land_name_not_found"));
                return true;
            }

            String land_id = LandsManager.getLandDetailByLandName(args[1], "id");

            if (!LandInvitesManager.isPlayerInvited(new Integer(land_id), player.getUniqueId().toString())) {
                player.sendMessage(Language.getCommand("accept.player_not_invited"));
                return true;
            }

            String role_id = LandInvitesManager.getInviteDetail(new Integer(land_id), player.getUniqueId().toString(), "role_id");

            String role_fetch = RolesManager.getRoleDetailById(new Integer(land_id), new Integer(role_id), "id");

            if (role_fetch == null) {
                LandInvitesManager.removeInviteFromPlayer(new Integer(land_id), player.getUniqueId().toString());

                player.sendMessage(Language.getCommand("accept.role_deleted"));
                
                return true;
            }

            LandMembersManager.invitePlayerToLand(new Integer(land_id), player.getUniqueId().toString(), new Integer(role_id));
            LandInvitesManager.removeInviteFromPlayer(new Integer(land_id), player.getUniqueId().toString());

            player.sendMessage(Language.getCommand("accept.player_trusted_success"));

            return true;
        } else {
            return false;
        }
    }
}
