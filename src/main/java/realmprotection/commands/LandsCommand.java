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
import realmprotection.commands.subcommands.AcceptCommand;
import realmprotection.commands.subcommands.BalanceCommand;
import realmprotection.commands.subcommands.BanCommand;
import realmprotection.commands.subcommands.BanlistCommand;
import realmprotection.commands.subcommands.ClaimCommand;
import realmprotection.commands.subcommands.DeleteCommand;
import realmprotection.commands.subcommands.DeleteRoleCommand;
import realmprotection.commands.subcommands.DepositCommand;
import realmprotection.commands.subcommands.FlyCommand;
import realmprotection.commands.subcommands.HelpCommand;
import realmprotection.commands.subcommands.InfoCommand;
import realmprotection.commands.subcommands.InvitesCommand;
import realmprotection.commands.subcommands.LeaveCommand;
import realmprotection.commands.subcommands.NewRoleCommand;
import realmprotection.commands.subcommands.RenameCommand;
import realmprotection.commands.subcommands.RenameRoleCommand;
import realmprotection.commands.subcommands.SetSpawnCommand;
import realmprotection.commands.subcommands.SpawnCommand;
import realmprotection.commands.subcommands.StorageCommand;
import realmprotection.commands.subcommands.TrustCommand;
import realmprotection.commands.subcommands.UnbanCommand;
import realmprotection.commands.subcommands.UnclaimCommand;
import realmprotection.commands.subcommands.UntrustCommand;
import realmprotection.commands.subcommands.UpdateNatureFlagsCommand;
import realmprotection.commands.subcommands.UpdateRoleFlagsCommand;
import realmprotection.managers.LandBansManager;
import realmprotection.managers.LandsManager;
import realmprotection.managers.RolesManager;
import realmprotection.utils.ChatColorTranslator;
import realmprotection.utils.Language;

public class LandsCommand implements TabExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            if (args.length > 0) {
                if (!sender.hasPermission("realmprotection.lands." + args[0])) {

                    sender.sendMessage(ChatColorTranslator.translate((String) Language.get("permissions.commands")));

                    return true;
                }

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
                    case "rename":
                        new RenameCommand().onCommand(sender, command, label, args);
                        break;
                    case "leave":
                        new LeaveCommand().onCommand(sender, command, label, args);
                        break;
                    case "help":
                        new HelpCommand().onCommand(sender, command, label, args);
                        break;
                    case "ban":
                        new BanCommand().onCommand(sender, command, label, args);
                        break;
                    case "unban":
                        new UnbanCommand().onCommand(sender, command, label, args);
                        break;
                    case "banlist":
                        new BanlistCommand().onCommand(sender, command, label, args);
                        break;
                    case "delete":
                        new DeleteCommand().onCommand(sender, command, label, args);
                        break;
                    case "fly":
                        new FlyCommand().onCommand(sender, command, label, args);
                        break;
                    case "storage":
                        new StorageCommand().onCommand(sender, command, label, args);
                        break;
                    case "accept":
                        new AcceptCommand().onCommand(sender, command, label, args);
                        break;
                    case "invites":
                        new InvitesCommand().onCommand(sender, command, label, args);
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
        List<String> arraylist = new ArrayList<>();
        int currentindex = 0;

        if (args.length == 1) {
            arraylist.add("claim");
            arraylist.add("unclaim");
            arraylist.add("info");
            arraylist.add("roles");
            arraylist.add("view");
            arraylist.add("trust");
            arraylist.add("untrust");
            arraylist.add("spawn");
            arraylist.add("setspawn");
            arraylist.add("nature");
            arraylist.add("deposit");
            arraylist.add("withdraw");
            arraylist.add("balance");
            arraylist.add("rename");
            arraylist.add("leave");
            arraylist.add("help");
            arraylist.add("ban");
            arraylist.add("unban");
            arraylist.add("banlist");
            arraylist.add("delete");
            arraylist.add("fly");
            arraylist.add("storage");
            arraylist.add("accept");
            arraylist.add("invites");

            currentindex = 1;
        } else if (args.length == 2) {
            switch (args[0]) {
                case "claim":
                    break;
                case "unclaim":
                    arraylist.add("confirm");
                    break;
                case "info":
                    arraylist = LandsManager.listAllLandNames();
                    break;
                case "roles":
                    arraylist.add("create");
                    arraylist.add("delete");
                    arraylist.add("flags");
                    arraylist.add("rename");
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

                    arraylist = playernames;
                    break;
                case "untrust":
                    arraylist = getOnlinePlayersList();

                    break;
                case "spawn":
                    arraylist = LandsManager.listAllLandNames();
                    break;
                case "setspawn":
                    break;
                case "nature":
                    arraylist.add("flags");
                    break;
                case "leave":
                    arraylist = LandsManager.listAllLandNames();
                    break;
                case "help":
                    break;
                case "ban":
                    arraylist = getOnlinePlayersList();
                    break;
                case "unban":
                    if (LandsManager.hasLand(sender.getName())) {
                        String land_id = LandsManager.getLandDetail(sender.getName(), "id");

                        List<List<String>> alldata = LandBansManager.listAllBannedPlayersData(new Integer(land_id));

                        for (List<String> data : alldata) {
                            arraylist.add(data.get(0));
                        }
                    }

                    break;
                case "banlist":
                    break;
                case "delete":
                    arraylist.add("confirm");
                    break;
                case "fly":
                    break;
                case "storage":
                    break;
                case "accept":
                    arraylist = LandsManager.listAllLandNames();
                    break;
                case "invites":
                    break;
                default:
                    break;
            }

            currentindex = 2;
        } else if (args.length == 3 && args[0].equalsIgnoreCase("roles")) {
            switch (args[1]) {
                case "create":
                    break;
                case "delete":
                    if (LandsManager.hasLand(sender.getName())) {
                        String land_id = LandsManager.getLandDetail(sender.getName(), "id");

                        arraylist = RolesManager.listAllRolesNames(new Integer(land_id));
                    }
                    break;
                case "flags":
                    if (LandsManager.hasLand(sender.getName())) {
                        String land_id = LandsManager.getLandDetail(sender.getName(), "id");

                        arraylist = RolesManager.listAllRolesNames(new Integer(land_id));
                    }
                    break;
                case "rename":
                    if (LandsManager.hasLand(sender.getName())) {
                        String land_id = LandsManager.getLandDetail(sender.getName(), "id");

                        arraylist = RolesManager.listAllRolesNames(new Integer(land_id));
                    }
                default:
                    break;
            }

            currentindex = 3;
        } else if (args.length == 3 && args[0].equalsIgnoreCase("trust")) {
            if (LandsManager.hasLand(sender.getName())) {
                String land_id = LandsManager.getLandDetail(sender.getName(), "id");

                arraylist = RolesManager.listAllRolesNames(new Integer(land_id));
            }

            currentindex = 3;
        } else if (args.length == 3 && args[0].equalsIgnoreCase("nature") && args[1].equalsIgnoreCase("flags")) {
            arraylist.add("hostilemobsspawn");
            arraylist.add("passivemobsspawn");
            arraylist.add("leavesdecay");
            arraylist.add("firespread");
            arraylist.add("liquidflow");
            arraylist.add("tntblockdamage");
            arraylist.add("respawnanchorblockdamage");
            arraylist.add("pistonsfromwilderness");
            arraylist.add("dispensersfromwilderness");
            arraylist.add("plantgrowth");

            currentindex = 3;
        } else if (args.length == 4 && args[0].equalsIgnoreCase("roles") && args[1].equalsIgnoreCase("flags")) {

            arraylist = RolesManager.listAllPermissions();

            currentindex = 4;
        } else if (args.length == 4 && args[0].equalsIgnoreCase("nature") && args[1].equalsIgnoreCase("flags")) {
            arraylist.add("true");
            arraylist.add("false");
            arraylist.add("1");
            arraylist.add("0");

            currentindex = 4;
        } else if (args.length == 5 && args[0].equalsIgnoreCase("roles") && args[1].equalsIgnoreCase("flags")) {
            arraylist.add("true");
            arraylist.add("false");
            arraylist.add("1");
            arraylist.add("0");

            currentindex = 5;
        } else {
            return null;
        }

        List<String> filteredlist = new ArrayList<>();

        for (String element : arraylist) {
            if (element.toLowerCase().startsWith(args[currentindex - 1].toLowerCase())) {
                filteredlist.add(element);
            }
        }

        return filteredlist;
    }

    public List<String> getOnlinePlayersList() {
        List<String> playernames = new ArrayList<>();
        Player[] players = new Player[Bukkit.getServer().getOnlinePlayers().size()];

        Bukkit.getServer().getOnlinePlayers().toArray(players);

        for (int i = 0; i < players.length; i++) {
            playernames.add(players[i].getName());
        }

        return playernames;
    }
}
