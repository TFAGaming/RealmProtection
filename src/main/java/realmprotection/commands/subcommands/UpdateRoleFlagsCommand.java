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

                        if ((Boolean) flag.get(1) == true) {
                            flagButtonLore.add(ColoredString
                                    .translate(LoadConfigString.guiString("role_flags.content.flag_style.lore_style")
                                            .replace("%value%", LoadConfigString.generalString("flags.enabled"))));
                        } else {
                            flagButtonLore.add(ColoredString
                                    .translate(LoadConfigString.guiString("role_flags.content.flag_style.lore_style")
                                            .replace("%value%", LoadConfigString.generalString("flags.disabled"))));
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
        switch (permission) {
            case "permissions_breakblocks":
                return Material.IRON_PICKAXE;
            case "permissions_placeblocks":
                return Material.OAK_LOG;
            case "permissions_containers":
                return Material.CHEST;
            case "permissions_redstone":
                return Material.REDSTONE;
            case "permissions_doors":
                return Material.OAK_DOOR;
            case "permissions_trapdoors":
                return Material.OAK_TRAPDOOR;
            case "permissions_editsigns":
                return Material.OAK_SIGN;
            case "permissions_emptybuckets":
                return Material.BUCKET;
            case "permissions_fillbuckets":
                return Material.WATER_BUCKET;
            case "permissions_harvestcrops":
                return Material.WHEAT_SEEDS;
            case "permissions_frostwalker":
                return Material.DIAMOND_BOOTS;
            case "permissions_shearentities":
                return Material.SHEARS;
            case "permissions_itemframes":
                return Material.ITEM_FRAME;
            case "permissions_fencegates":
                return Material.OAK_FENCE_GATE;
            case "permissions_buttons":
                return Material.STONE_BUTTON;
            case "permissions_levers":
                return Material.LEVER;
            case "permissions_pressureplates":
                return Material.STONE_PRESSURE_PLATE;
            case "permissions_bells":
                return Material.BELL;
            case "permissions_tripwires":
                return Material.TRIPWIRE_HOOK;
            case "permissions_armorstands":
                return Material.ARMOR_STAND;
            case "permissions_teleporttospawn":
                return Material.ENDER_EYE;
            case "permissions_throwenderpearls":
                return Material.ENDER_PEARL;
            case "permissions_throwpotions":
                return Material.SPLASH_POTION;
            case "permissions_damagehostilemobs":
                return Material.ZOMBIE_HEAD;
            case "permissions_damagepassivemobs":
                return Material.IRON_SWORD;
            case "permissions_pvp":
                return Material.DIAMOND_SWORD;
            case "permissions_usecauldron":
                return Material.CAULDRON;
            case "permissions_pickupitems":
                return Material.SUNFLOWER;
            case "permissions_useanvil":
                return Material.ANVIL;
            case "permissions_createfire":
                return Material.FLINT_AND_STEEL;
            default:
                return Material.STRUCTURE_VOID;
        }
    }
}
