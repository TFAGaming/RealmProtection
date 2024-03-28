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
import realmprotection.utils.ColoredString;
import realmprotection.utils.LoadConfigString;

public class UpdateNatureFlagsCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (!LandsManager.hasLand(player.getName())) {
                player.sendMessage(LoadConfigString.load("update_nature_flags.land_not_found"));
            } else {
                String land_id = LandsManager.getLandDetail(player.getName(), "id");
                String land_name = LandsManager.getLandDetailById(new Integer(land_id), "land_name");

                if (args.length == 2) {
                    Inventory inventory = Bukkit.createInventory(player, 9 * 3,
                            ColoredString.translate(ColoredString.translate(LoadConfigString.guiString("nature_flags.title"))));

                    List<List<Object>> allflags = LandsManager.listEnabledAndDisabledFlagsForLand(new Integer(land_id));

                    for (List<Object> flag : allflags) {
                        ItemStack flagButton = new ItemStack(
                                getMaterialItemFromFlagName("nature_" + flag.get(0)));

                        ItemMeta flagButtonMeta = flagButton.getItemMeta();
                        flagButtonMeta.setDisplayName(ColoredString.translate(LoadConfigString.guiString("nature_flags.content.flag_style.displayname").replace("%flag%", "" + flag.get(0))));

                        ArrayList<String> flagButtonLore = new ArrayList<>();

                        if ((Boolean) flag.get(1) == true) {
                            flagButtonLore.add(ColoredString
                                    .translate(LoadConfigString.guiString("nature_flags.content.flag_style.lore_style")
                                            .replace("%value%", LoadConfigString.generalString("flags.enabled"))));
                        } else {
                            flagButtonLore.add(ColoredString
                                    .translate(LoadConfigString.guiString("nature_flags.content.flag_style.lore_style")
                                            .replace("%value%", LoadConfigString.generalString("flags.disabled"))));
                        }

                        flagButtonMeta.setLore(flagButtonLore);
                        flagButton.setItemMeta(flagButtonMeta);

                        inventory.addItem(flagButton);
                    }

                    ItemStack landNameButton = new ItemStack(
                            Material.getMaterial(LoadConfigString.guiString("nature_flags.content.land_name.type")));
                    ItemMeta landNameButtonMeta = landNameButton.getItemMeta();
                    landNameButtonMeta.setDisplayName(ColoredString.translate(LoadConfigString.guiString("nature_flags.content.land_name.displayname").replace("%land_name%", land_name)));
                    landNameButton.setItemMeta(landNameButtonMeta);

                    ItemStack closeButton = new ItemStack(
                            Material.getMaterial(LoadConfigString.generalString("gui.close_button.type")));
                    ItemMeta closeButtonMeta = closeButton.getItemMeta();
                    closeButtonMeta.setDisplayName(ColoredString.translate(LoadConfigString.generalString("gui.close_button.displayname")));
                    closeButton.setItemMeta(closeButtonMeta);

                    inventory.setItem(18, landNameButton);
                    if (LoadConfigString.generalBoolean("gui.close_button.enabled") == true) {
                        inventory.setItem(26, closeButton);
                    }

                    player.openInventory(inventory);
                } else {
                    List<String> listofflags = new ArrayList<>();

                    listofflags.add("hostilemobsspawn");
                    listofflags.add("passivemobsspawn");
                    listofflags.add("leavesdecay");
                    listofflags.add("firespread");
                    listofflags.add("liquidflow");
                    listofflags.add("tntblockdamage");
                    listofflags.add("respawnanchorblockdamage");
                    listofflags.add("pistonsfromwilderness");
                    listofflags.add("plantgrowth");

                    if (!listofflags.contains(args[2])) {
                        player.sendMessage(LoadConfigString.load("update_nature_flags.not_valid_flag"));
                        return true;
                    }

                    if (args.length == 3) {
                        player.sendMessage(LoadConfigString.load("update_nature_flags.missing_boolean_value"));
                        return true;
                    }

                    if (!(args[3].equalsIgnoreCase("true") || args[3].equalsIgnoreCase("false")
                            || args[3].equalsIgnoreCase("1") || args[3].equalsIgnoreCase("0"))) {
                        player.sendMessage(LoadConfigString.load("update_nature_flags.not_boolean_value"));
                        return true;
                    }

                    Boolean value;

                    if (args[3].equalsIgnoreCase("true") || args[3].equalsIgnoreCase("1")) {
                        value = true;
                    } else {
                        value = false;
                    }

                    LandsManager.updateNatureFlagValue(new Integer(land_id), args[2], value);

                    player.sendMessage(LoadConfigString.load("update_nature_flags.nature_flag_updated_success")
                            .replace("%flag%", args[2]).replace("%new_value%", "" + value));

                }
            }

            return true;
        } else {
            return false;
        }
    }

    public Material getMaterialItemFromFlagName(String flag) {
        switch (flag) {
            case "nature_hostilemobsspawn":
                return Material.ZOMBIE_HEAD;
            case "nature_passivemobsspawn":
                return Material.SPAWNER;
            case "nature_leavesdecay":
                return Material.VINE;
            case "nature_firespread":
                return Material.FLINT_AND_STEEL;
            case "nature_liquidflow":
                return Material.WATER_BUCKET;
            case "nature_tntblockdamage":
                return Material.TNT;
            case "nature_respawnanchorblockdamage":
                return Material.RESPAWN_ANCHOR;
            case "nature_pistonsfromwilderness":
                return Material.PISTON;
            case "nature_plantgrowth":
                return Material.OAK_SAPLING;
            default:
                return Material.STRUCTURE_VOID;
        }
    }
}
