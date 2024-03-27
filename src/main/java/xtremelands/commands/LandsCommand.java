package xtremelands.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import xtremelands.commands.subcommands.ViewCommand;
import xtremelands.commands.subcommands.ClaimCommand;
import xtremelands.commands.subcommands.DeleteRoleCommand;
import xtremelands.commands.subcommands.InfoCommand;
import xtremelands.commands.subcommands.NewRoleCommand;
import xtremelands.commands.subcommands.RenameRoleCommand;
import xtremelands.commands.subcommands.SetSpawnCommand;
import xtremelands.commands.subcommands.SpawnCommand;
import xtremelands.commands.subcommands.TrustCommand;
import xtremelands.commands.subcommands.UnclaimCommand;
import xtremelands.commands.subcommands.UntrustCommand;
import xtremelands.commands.subcommands.UpdateNatureFlagsCommand;
import xtremelands.commands.subcommands.UpdateRoleFlagsCommand;
import xtremelands.managers.LandMembersManager;
import xtremelands.managers.LandsManager;
import xtremelands.managers.RolesManager;

public class LandsCommand implements TabExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            if (args.length > 0) {
                switch (args[0]) {
                    case "claim":
                        new ClaimCommand().onCommand(sender, command, label, args);
                        break;
                    case "unclaim":
                        new UnclaimCommand().onCommand(sender, command, label, args);
                        break;
                    case "info":
                        new InfoCommand().onCommand(sender, command, label, args);
                        break;
                    case "view":
                        new ViewCommand().onCommand(sender, command, label, args);
                        break;
                    case "trust":
                        new TrustCommand().onCommand(sender, command, label, args);
                        break;
                    case "untrust":
                        new UntrustCommand().onCommand(sender, command, label, args);
                        break;
                    case "spawn":
                        new SpawnCommand().onCommand(sender, command, label, args);
                        break;
                    case "setspawn":
                        new SetSpawnCommand().onCommand(sender, command, label, args);
                        break;
                    default:
                        break;
                }
            }

            if (args.length > 1 && args[0].equalsIgnoreCase("roles")) {
                switch (args[1]) {
                    case "create":
                        new NewRoleCommand().onCommand(sender, command, label, args);
                        break;
                    case "delete":
                        new DeleteRoleCommand().onCommand(sender, command, label, args);
                        break;
                    case "rename":
                        new RenameRoleCommand().onCommand(sender, command, label, args);
                        break;
                    case "flags":
                        new UpdateRoleFlagsCommand().onCommand(sender, command, label, args);
                        break;
                    default:
                        break;
                }
            } else if (args.length > 1 && args[0].equalsIgnoreCase("nature")) {
                switch (args[1]) {
                    case "flags":
                        new UpdateNatureFlagsCommand().onCommand(sender, command, label, args);
                        break;
                    default:
                        break;
                }
            }

            return true;
        } else {
            return false;
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            List<String> subcommands = new ArrayList<>();

            subcommands.add("claim");
            subcommands.add("unclaim");
            subcommands.add("info");
            subcommands.add("roles");
            subcommands.add("view");
            subcommands.add("trust");
            subcommands.add("untrust");
            subcommands.add("spawn");
            subcommands.add("setspawn");
            subcommands.add("nature");

            return subcommands;
        } else if (args.length == 2) {
            List<String> arguments = new ArrayList<>();

            switch (args[0]) {
                case "claim":
                    break;
                case "unclaim":
                    arguments.add("confirm");
                    break;
                case "info":
                    break;
                case "roles":
                    arguments.add("create");
                    arguments.add("delete");
                    arguments.add("flags");
                    arguments.add("rename");
                    break;
                case "view":
                    break;
                case "trust":
                    List<String> playernames = new ArrayList<>();
                    Player[] players = new Player[Bukkit.getServer().getOnlinePlayers().size()];

                    Bukkit.getServer().getOnlinePlayers().toArray(players);

                    for (int i = 0; i < players.length; i++) {
                        playernames.add(players[i].getName());
                    }

                    arguments = playernames;
                    break;
                case "untrust":
                    if (LandsManager.hasLand(sender.getName())) {
                        String land_id = LandsManager.getLandDetail(sender.getName(), "id");

                        List<List<String>> alldata = LandMembersManager.listAllMembersData(new Integer(land_id));

                        for (List<String> data : alldata) {
                            arguments.add(data.get(0));
                        }
                    }

                    break;
                case "spawn":
                    arguments = LandsManager.listAllLandNames();
                case "setspawn":
                    break;
                case "nature":
                    arguments.add("flags");
                    break;
                default:
                    break;
            }

            return arguments;
        } else if (args.length == 3 && args[0].equalsIgnoreCase("roles")) {
            List<String> arguments = new ArrayList<>();

            switch (args[1]) {
                case "create":
                    break;
                case "delete":
                    if (LandsManager.hasLand(sender.getName())) {
                        String land_id = LandsManager.getLandDetail(sender.getName(), "id");

                        arguments = RolesManager.listAllRolesNames(new Integer(land_id));
                    }

                    break;
                case "flags":
                    if (LandsManager.hasLand(sender.getName())) {
                        String land_id = LandsManager.getLandDetail(sender.getName(), "id");

                        arguments = RolesManager.listAllRolesNames(new Integer(land_id));
                    }

                    break;
                case "rename":
                    if (LandsManager.hasLand(sender.getName())) {
                        String land_id = LandsManager.getLandDetail(sender.getName(), "id");

                        arguments = RolesManager.listAllRolesNames(new Integer(land_id));
                    }
                default:
                    break;
            }

            return arguments;
        } else if (args.length == 3 && args[0].equalsIgnoreCase("trust")) {
            List<String> arguments = new ArrayList<>();

            if (LandsManager.hasLand(sender.getName())) {
                String land_id = LandsManager.getLandDetail(sender.getName(), "id");

                arguments = RolesManager.listAllRolesNames(new Integer(land_id));
            }

            return arguments;
        } else if (args.length == 3 && args[0].equalsIgnoreCase("nature") && args[1].equalsIgnoreCase("flags")) {
            List<String> arguments = new ArrayList<>();

            arguments.add("hostilemobsspawn");
            arguments.add("passivemobsspawn");
            arguments.add("leavesdecay");
            arguments.add("firespread");
            arguments.add("liquidflow");
            arguments.add("tntblockdamage");
            arguments.add("respawnanchorblockdamage");
            arguments.add("pistonsfromwilderness");
            arguments.add("plantgrowth");

            return arguments;
        } else if (args.length == 4 && args[0].equalsIgnoreCase("roles") && args[1].equalsIgnoreCase("flags")) {
            List<String> arguments = new ArrayList<>();

            arguments.add("breakblocks");
            arguments.add("placeblocks");
            arguments.add("containers");
            arguments.add("redstone");
            arguments.add("doors");
            arguments.add("trapdoors");
            arguments.add("editsigns");
            arguments.add("emptybuckets");
            arguments.add("fillbuckets");
            arguments.add("harvestcrops");
            arguments.add("frostwalker");
            arguments.add("shearentities");
            arguments.add("itemframes");
            arguments.add("fencegates");
            arguments.add("buttons");
            arguments.add("levers");
            arguments.add("pressureplates");
            arguments.add("bells");
            arguments.add("tripwires");
            arguments.add("armorstands");
            arguments.add("teleporttospawn");
            arguments.add("damagehostilemobs");
            arguments.add("damagepassivemobs");
            arguments.add("pvp");
            arguments.add("usecauldron");
            arguments.add("pickupitems");
            arguments.add("useanvil");
            arguments.add("createfire");

            return arguments;
        } else if (args.length == 4 && args[0].equalsIgnoreCase("nature") && args[1].equalsIgnoreCase("flags")) {
            List<String> arguments = new ArrayList<>();

            arguments.add("true");
            arguments.add("false");
            arguments.add("1");
            arguments.add("0");

            return arguments;
        } else if (args.length == 5 && args[0].equalsIgnoreCase("roles") && args[1].equalsIgnoreCase("flags")) {
            List<String> arguments = new ArrayList<>();

            arguments.add("true");
            arguments.add("false");
            arguments.add("1");
            arguments.add("0");

            return arguments;
        }

        return null;
    }
}
