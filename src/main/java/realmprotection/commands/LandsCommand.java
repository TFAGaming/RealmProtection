package realmprotection.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import realmprotection.commands.subcommands.ViewCommand;
import realmprotection.commands.subcommands.WithdrawCommand;
import realmprotection.commands.subcommands.BalanceCommand;
import realmprotection.commands.subcommands.ClaimCommand;
import realmprotection.commands.subcommands.DeleteRoleCommand;
import realmprotection.commands.subcommands.DepositCommand;
import realmprotection.commands.subcommands.InfoCommand;
import realmprotection.commands.subcommands.NewRoleCommand;
import realmprotection.commands.subcommands.RenameRoleCommand;
import realmprotection.commands.subcommands.SetSpawnCommand;
import realmprotection.commands.subcommands.SpawnCommand;
import realmprotection.commands.subcommands.TrustCommand;
import realmprotection.commands.subcommands.UnclaimCommand;
import realmprotection.commands.subcommands.UntrustCommand;
import realmprotection.commands.subcommands.UpdateNatureFlagsCommand;
import realmprotection.commands.subcommands.UpdateRoleFlagsCommand;
import realmprotection.managers.LandMembersManager;
import realmprotection.managers.LandsManager;
import realmprotection.managers.RolesManager;

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
                    case "deposit":
                        new DepositCommand().onCommand(sender, command, label, args);
                        break;
                    case "withdraw":
                        new WithdrawCommand().onCommand(sender, command, label, args);
                        break;
                    case "balance":
                        new BalanceCommand().onCommand(sender, command, label, args);
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
            subcommands.add("deposit");
            subcommands.add("withdraw");
            subcommands.add("balance");

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
            arguments.add("throwenderpearls");
            arguments.add("throwpotions");
            arguments.add("damagehostilemobs");
            arguments.add("damagepassivemobs");
            arguments.add("pvp");
            arguments.add("usecauldron");
            arguments.add("pickupitems");
            arguments.add("useanvil");
            arguments.add("createfire");
            arguments.add("usevehicles");

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