package realmprotection.commands.subcommands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import realmprotection.managers.ChunksManager;
import realmprotection.managers.LandBansManager;
import realmprotection.managers.LandMembersManager;
import realmprotection.managers.LandsManager;
import realmprotection.managers.RolesManager;
import realmprotection.utils.Language;

public class DeleteCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (!LandsManager.hasLand(player.getName())) {
                player.sendMessage(Language.getCommand("delete.land_not_found"));
                return true;
            }

            String land_id = LandsManager.getLandDetail(player.getName(), "id");

            if (args.length == 1 || !args[1].equalsIgnoreCase("confirm")) {
                player.sendMessage(Language.getCommand("delete.confirm_missing"));
                return true;
            }

            LandsManager.deleteLand(new Integer(land_id));
            RolesManager.deleteAllRolesFromLand(new Integer(land_id));
            LandMembersManager.deleteAllMembersFromLand(new Integer(land_id));
            LandBansManager.deleteAllBannedPlayersFromLand(new Integer(land_id));
            ChunksManager.deleteAllChunksFromLand(new Integer(land_id));

            player.sendMessage(Language.getCommand("delete.land_deleted_success"));

            return true;
        } else {
            return false;
        }
    }
}
