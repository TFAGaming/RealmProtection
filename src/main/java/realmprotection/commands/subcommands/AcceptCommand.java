package realmprotection.commands.subcommands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import realmprotection.managers.LandInvitesManager;
import realmprotection.managers.LandMembersManager;
import realmprotection.managers.LandsManager;
import realmprotection.utils.LoadConfig;

public class AcceptCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (args.length == 1) {
                player.sendMessage(LoadConfig.commandString("accept.missing_land_name"));
                return true;
            }

            if (!LandsManager.landNameExist(args[1])) {
                player.sendMessage(LoadConfig.commandString("accept.land_name_not_found"));
                return true;
            }

            String land_id = LandsManager.getLandDetailByLandName(args[1], "id");

            if (!LandInvitesManager.isPlayerInvited(new Integer(land_id), player.getName())) {
                player.sendMessage(LoadConfig.commandString("accept.player_not_invited"));
                return true;
            }

            String role_id = LandInvitesManager.getInviteDetail(new Integer(land_id), player.getName(), "role_id");

            LandMembersManager.invitePlayerToLand(new Integer(land_id), player.getName(), new Integer(role_id));
            LandInvitesManager.removeInviteFromPlayer(new Integer(land_id), player.getName());

            player.sendMessage(LoadConfig.commandString("accept.player_trusted_success"));

            return true;
        } else {
            return false;
        }
    }
}
