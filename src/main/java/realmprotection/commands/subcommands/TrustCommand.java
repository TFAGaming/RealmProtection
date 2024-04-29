package realmprotection.commands.subcommands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import realmprotection.RealmProtection;
import realmprotection.managers.LandInvitesManager;
import realmprotection.managers.LandMembersManager;
import realmprotection.managers.LandsManager;
import realmprotection.managers.RolesManager;
import realmprotection.utils.Language;

public class TrustCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (!LandsManager.hasLand(player.getName())) {
                player.sendMessage(Language.getCommand("trust.land_not_found"));
                return true;
            }

            if (args.length == 1) {
                player.sendMessage(Language.getCommand("trust.no_playername_provided"));
                return true;
            }

            Player player_searched = Bukkit.getPlayer(args[1]);

            if (player_searched == null) {
                player.sendMessage(Language.getCommand("trust.playername_not_found"));
                return true;
            }

            String land_id = LandsManager.getLandDetail(player.getName(), "id");
            String land_owner_name = LandsManager.getLandDetailById(new Integer(land_id), "owner_name");

            if (LandMembersManager.isPlayerInTheLand(new Integer(land_id), args[1])) {
                player.sendMessage(Language.getCommand("trust.playername_already_trusted"));
                return true;
            }

            if (LandInvitesManager.isPlayerInvited(new Integer(land_id), args[1])) {
                player.sendMessage(Language.getCommand("trust.playername_already_invited"));
                return true;
            }

            if (args[1].equalsIgnoreCase(land_owner_name)) {
                player.sendMessage(Language.getCommand("trust.playername_owner_of_land"));
                return true;
            }

            if (args.length == 2) {
                player.sendMessage(Language.getCommand("trust.no_role_provided"));
                return true;
            }

            if (!RolesManager.hasRole(new Integer(land_id), args[2])) {
                player.sendMessage(Language.getCommand("trust.role_not_found"));
                return true;
            }

            RealmProtection plugin = RealmProtection.getPlugin(RealmProtection.class);

            if (args[2].equalsIgnoreCase(plugin.getConfig().getString("roles.__DEFAULT_VISITOR_ROLE__"))) {
                player.sendMessage(Language.getCommand("trust.player_cannot_have_default_role"));
                return true;
            }

            //LandMembersManager.invitePlayerToLand(new Integer(land_id), args[1], args[2]);
            LandInvitesManager.invitePlayerToLand(new Integer(land_id), player.getName(), args[1], new Integer(RolesManager.getRoleDetail(new Integer(land_id), args[2], "id")));

            player.sendMessage(Language.getCommand("trust.player_invited_success").replace("%player%", args[1]));

            return true;
        } else {
            return false;
        }
    }
}
