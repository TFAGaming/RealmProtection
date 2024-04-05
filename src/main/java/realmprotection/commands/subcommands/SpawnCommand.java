package realmprotection.commands.subcommands;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import realmprotection.managers.LandBansManager;
import realmprotection.managers.LandMembersManager;
import realmprotection.managers.LandsManager;
import realmprotection.utils.LoadConfig;

public class SpawnCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (args.length == 1) {
                player.sendMessage(LoadConfig.commandString("spawn.missing_land_name"));
                return true;
            }

            if (!LandsManager.landNameExist(args[1])) {
                player.sendMessage(LoadConfig.commandString("spawn.land_name_not_found"));
                return true;
            }

            String land_id = LandsManager.getLandDetailByLandName(args[1], "id");
            String land_owner_name = LandsManager.getLandDetailById(new Integer(land_id), "owner_name");

            if (!player.getName().equalsIgnoreCase(land_owner_name) && !LandMembersManager
                    .hasPlayerThePermissionToDo(new Integer(land_id), player.getName(), "teleporttospawn")) {
                player.sendMessage(LoadConfig.commandString("spawn.missing_permissions_to_teleport")
                        .replace("%land_name%", args[1]));
                return true;
            }

            if (LandBansManager.isPlayerBannedFromLand(new Integer(land_id), player.getName())) {
                String ban_reason = LandBansManager.getBanReason(new Integer(land_id), player.getName());
                String land_name = LandsManager.getLandDetailById(new Integer(land_id), "land_name");

                player.sendMessage(LoadConfig.commandString("spawn.player_banned").replace("%land%", land_name)
                        .replace("%reason%", ban_reason));

                return true;
            }

            String land_world = LandsManager.getLandDetailByLandName(args[1], "location_world");
            String land_x = LandsManager.getLandDetailByLandName(args[1], "location_x");
            String land_y = LandsManager.getLandDetailByLandName(args[1], "location_y");
            String land_z = LandsManager.getLandDetailByLandName(args[1], "location_z");

            World world = Bukkit.getWorld(land_world);

            Location location = new Location(world, Double.parseDouble(land_x), Double.parseDouble(land_y),
                    Double.parseDouble(land_z));

            Block newblock = location.getBlock();
            Block underplayer = newblock.getRelative(0, -1, 0);

            if (underplayer.getType().name().contains("AIR") || underplayer.getType().name().contains("LAVA")) {
                player.sendMessage(LoadConfig.commandString("spawn.location_not_safe"));
                return true;
            }

            player.teleport(location);

            player.sendMessage(LoadConfig.commandString("spawn.spawn_teleport_success").replace("%land_name%", args[1])
                    .replace("%x%", String.format("%.2f", new Double(land_x)))
                    .replace("%y%", String.format("%.2f", new Double(land_y)))
                    .replace("%z%", String.format("%.2f", new Double(land_z))).replace("%world%", land_world));

            return true;
        } else {
            return false;
        }
    }
}
