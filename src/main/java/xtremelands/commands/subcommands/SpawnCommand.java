package xtremelands.commands.subcommands;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import xtremelands.managers.LandMembersManager;
import xtremelands.managers.LandsManager;
import xtremelands.utils.LoadConfigString;

public class SpawnCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (args.length == 1) {
                player.sendMessage(LoadConfigString.load("spawn.missing_land_name"));
                return true;
            }

            if (!LandsManager.landNameExist(args[1])) {
                player.sendMessage(LoadConfigString.load("spawn.land_name_not_found"));
                return true;
            }

            String land_id = LandsManager.getLandDetailByLandName(args[1], "id");
            String land_owner_name = LandsManager.getLandDetailById(new Integer(land_id), "owner_name");

            if (!player.getName().equalsIgnoreCase(land_owner_name) && !LandMembersManager
                    .hasPlayerThePermissionToDo(new Integer(land_id), player.getName(), "teleporttospawn")) {
                player.sendMessage(LoadConfigString.load("spawn.missing_permissions_to_teleport").replace("%land_name%", args[1]));
                return true;
            }

            String land_world = LandsManager.getLandDetailByLandName(args[1], "location_world");
            String land_x = LandsManager.getLandDetailByLandName(args[1], "location_x");
            String land_y = LandsManager.getLandDetailByLandName(args[1], "location_y");
            String land_z = LandsManager.getLandDetailByLandName(args[1], "location_z");

            World world = Bukkit.getWorld(land_world);

            Location location = new Location(world, Double.parseDouble(land_x), Double.parseDouble(land_y),
                    Double.parseDouble(land_z));

            player.teleport(location);

            player.sendMessage(LoadConfigString.load("spawn.spawn_teleport_success").replace("%land_name%", args[1]).replace("%x%", land_x).replace("%y%", land_y).replace("%z%", land_z).replace("%world%", land_world));

            return true;
        } else {
            return false;
        }
    }
}
