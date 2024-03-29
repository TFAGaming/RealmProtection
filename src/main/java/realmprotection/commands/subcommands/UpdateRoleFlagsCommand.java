package realmprotection.commands.subcommands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import realmprotection.managers.LandsManager;
import realmprotection.managers.RolesManager;
import realmprotection.utils.ColoredString;
import realmprotection.utils.LoadConfigString;

public class UpdateRoleFlagsCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (!LandsManager.hasLand(player.getName())) {
                player.sendMessage(LoadConfigString.load("update_role_flags.land_not_found"));
            } else {
                if (args.length == 2) {
                    player.sendMessage(LoadConfigString.load("update_role_flags.no_role_provided"));
                    return true;
                }

                String land_id = LandsManager.getLandDetail(player.getName(), "id");

                if (!RolesManager.hasRole(new Integer(land_id), args[2])) {
                    player.sendMessage(LoadConfigString.load("update_role_flags.role_not_found"));
                    return true;
                }

                if (args.length == 3) {
                    Inventory inventory = Bukkit.createInventory(player, 9 * 6,
                            ColoredString.translate(LoadConfigString.guiString("role_flags.title")));

                    List<List<Object>> allflags = RolesManager.listEnabledAndDisabledFlagsForRole(new Integer(land_id),
                            args[2]);

                    for (List<Object> flag : allflags) {
                        ItemStack flagButton = new ItemStack(
                                getMaterialItemFromPermissionName("permissions_" + flag.get(0)));

                        ItemMeta flagButtonMeta = flagButton.getItemMeta();
                        flagButtonMeta.setDisplayName(ColoredString
                                .translate(LoadConfigString.guiString("role_flags.content.flag_style.displayname")
                                        .replace("%flag%", "" + flag.get(0))));

                        ArrayList<String> flagButtonLore = new ArrayList<>();
                        List<String> roleflagslore = LoadConfigString
                                .guiStringList("role_flags.content.flag_style.lore");

                        for (String lore : roleflagslore) {
                            if ((Boolean) flag.get(1) == true) {
                                flagButtonLore.add(ColoredString
                                        .translate(lore
                                                .replace("%description%",
                                                        "" + LoadConfigString.guiString(
                                                                "flags_description.role_flags." + flag.get(0)))
                                                .replace("%value%", LoadConfigString.generalString("flags.enabled"))));
                            } else {
                                flagButtonLore.add(ColoredString
                                        .translate(lore
                                                .replace("%description%",
                                                        "" + LoadConfigString.guiString(
                                                                "flags_description.role_flags." + flag.get(0)))
                                                .replace("%value%", LoadConfigString.generalString("flags.disabled"))));
                            }
                        }

                        flagButtonMeta.setLore(flagButtonLore);
                        flagButton.setItemMeta(flagButtonMeta);

                        inventory.addItem(flagButton);
                    }

                    ItemStack roleNameButton = new ItemStack(
                            Material.getMaterial(LoadConfigString.guiString("role_flags.content.role_name.type")));
                    ItemMeta roleNameButtonMeta = roleNameButton.getItemMeta();
                    roleNameButtonMeta.setDisplayName(ColoredString.translate(LoadConfigString
                            .guiString("role_flags.content.role_name.displayname").replace("%role%", args[2])));
                    roleNameButton.setItemMeta(roleNameButtonMeta);

                    ItemStack closeButton = new ItemStack(
                            Material.getMaterial(LoadConfigString.generalString("gui.close_button.type")));
                    ItemMeta closeButtonMeta = closeButton.getItemMeta();
                    closeButtonMeta.setDisplayName(
                            ColoredString.translate(LoadConfigString.generalString("gui.close_button.displayname")));
                    closeButton.setItemMeta(closeButtonMeta);

                    inventory.setItem(45, roleNameButton);
                    if (LoadConfigString.generalBoolean("gui.close_button.enabled") == true) {
                        inventory.setItem(53, closeButton);
                    }

                    player.openInventory(inventory);
                } else {
                    List<String> listofflags = new ArrayList<>();

                    listofflags.add("breakblocks");
                    listofflags.add("placeblocks");
                    listofflags.add("containers");
                    listofflags.add("redstone");
                    listofflags.add("doors");
                    listofflags.add("trapdoors");
                    listofflags.add("editsigns");
                    listofflags.add("emptybuckets");
                    listofflags.add("fillbuckets");
                    listofflags.add("harvestcrops");
                    listofflags.add("frostwalker");
                    listofflags.add("shearentities");
                    listofflags.add("itemframes");
                    listofflags.add("fencegates");
                    listofflags.add("buttons");
                    listofflags.add("levers");
                    listofflags.add("pressureplates");
                    listofflags.add("bells");
                    listofflags.add("tripwires");
                    listofflags.add("armorstands");
                    listofflags.add("teleporttospawn");
                    listofflags.add("throwenderpearls");
                    listofflags.add("throwpotions");
                    listofflags.add("damagehostilemobs");
                    listofflags.add("damagepassivemobs");
                    listofflags.add("pvp");
                    listofflags.add("usecauldron");
                    listofflags.add("pickupitems");
                    listofflags.add("useanvil");
                    listofflags.add("createfire");
                    listofflags.add("usevehicles");

                    if (!listofflags.contains(args[3])) {
                        player.sendMessage(LoadConfigString.load("update_role_flags.not_valid_flag"));
                        return true;
                    }

                    if (args.length == 4) {
                        player.sendMessage(LoadConfigString.load("update_role_flags.missing_boolean_value"));
                        return true;
                    }

                    if (!(args[4].equalsIgnoreCase("true") || args[4].equalsIgnoreCase("false")
                            || args[4].equalsIgnoreCase("1") || args[4].equalsIgnoreCase("0"))) {
                        player.sendMessage(LoadConfigString.load("update_role_flags.not_boolean_value"));
                        return true;
                    }

                    Boolean value;

                    if (args[4].equalsIgnoreCase("true") || args[4].equalsIgnoreCase("1")) {
                        value = true;
                    } else {
                        value = false;
                    }

                    RolesManager.updatePermissionValue(new Integer(land_id), args[2], args[3], value);

                    player.sendMessage(LoadConfigString.load("update_role_flags.role_flag_updated_success")
                            .replace("%flag%", args[3]).replace("%role_name%", args[2])
                            .replace("%new_value%", "" + value));

                }
            }

            return true;
        } else {
            return false;
        }
    }

    public Material getMaterialItemFromPermissionName(String permission) {
        String[] splitted = permission.split("_");
        List<String> splittedlist = new ArrayList<>();

        for (String split : splitted) {
            splittedlist.add(split);
        }

        Material material = Material.getMaterial(LoadConfigString.guiString("role_flags.items." + splittedlist.get(1)));

        if (material != null) {
            return material;
        } else {
            return Material.STRUCTURE_VOID;
        }
    }
}
