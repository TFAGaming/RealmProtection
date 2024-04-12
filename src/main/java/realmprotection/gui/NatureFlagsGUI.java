package realmprotection.gui;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import realmprotection.managers.LandsManager;
import realmprotection.utils.ColoredString;
import realmprotection.utils.LoadConfig;

public class NatureFlagsGUI {
    public static void create(Player player) {
        String land_id = LandsManager.getLandDetail(player.getName(), "id");
        String land_name = LandsManager.getLandDetailById(new Integer(land_id), "land_name");

        Inventory inventory = Bukkit.createInventory(player, 9 * 3,
                ColoredString.translate(
                        ColoredString.translate(LoadConfig.guiString("nature_flags.title"))));

        List<List<Object>> allflags = LandsManager.listEnabledAndDisabledFlagsForLand(new Integer(land_id));

        for (List<Object> flag : allflags) {
            ItemStack flagButton = new ItemStack(
                    getMaterialItemFromFlagName("nature_" + flag.get(0)));

            ItemMeta flagButtonMeta = flagButton.getItemMeta();
            flagButtonMeta.setDisplayName(ColoredString
                    .translate(LoadConfig.guiString("nature_flags.content.flag_style.displayname")
                            .replace("%flag%", "" + flag.get(0)).replace("%displayname%",
                                    LoadConfig.guiString("flags_displayname.nature_flags." + flag.get(0)))));

            ArrayList<String> flagButtonLore = new ArrayList<>();
            List<String> natureflagslore = LoadConfig
                    .guiStringList("nature_flags.content.flag_style.lore");

            for (String lore : natureflagslore) {
                if ((boolean) flag.get(1) == true) {
                    flagButtonLore.add(ColoredString
                            .translate(lore
                                    .replace("%description%",
                                            "" + LoadConfig.guiString(
                                                    "flags_description.nature_flags." + flag.get(0)))
                                    .replace("%value%", LoadConfig.generalString("flags.enabled"))));
                } else {
                    flagButtonLore.add(ColoredString
                            .translate(lore
                                    .replace("%description%",
                                            "" + LoadConfig.guiString(
                                                    "flags_description.nature_flags." + flag.get(0)))
                                    .replace("%value%", LoadConfig.generalString("flags.disabled"))));
                }
            }

            flagButtonMeta.setLore(flagButtonLore);
            flagButton.setItemMeta(flagButtonMeta);

            inventory.addItem(flagButton);
        }

        ItemStack landNameButton = new ItemStack(
                Material.getMaterial(LoadConfig.guiString("nature_flags.content.land_name.type")));
        ItemMeta landNameButtonMeta = landNameButton.getItemMeta();
        landNameButtonMeta.setDisplayName(ColoredString
                .translate(LoadConfig.guiString("nature_flags.content.land_name.displayname")
                        .replace("%land_name%", land_name)));
        landNameButton.setItemMeta(landNameButtonMeta);

        ItemStack closeButton = new ItemStack(
                Material.getMaterial(LoadConfig.generalString("gui.close_button.type")));
        ItemMeta closeButtonMeta = closeButton.getItemMeta();
        closeButtonMeta.setDisplayName(
                ColoredString.translate(LoadConfig.generalString("gui.close_button.displayname")));
        closeButton.setItemMeta(closeButtonMeta);

        inventory.setItem(18, landNameButton);
        if (LoadConfig.generalBoolean("gui.close_button.enabled") == true) {
            inventory.setItem(26, closeButton);
        }

        player.openInventory(inventory);
    }

    public static Material getMaterialItemFromFlagName(String flag) {
        String[] splitted = flag.split("_");
        List<String> splittedlist = new ArrayList<>();

        for (String split : splitted) {
            splittedlist.add(split);
        }

        Material material = Material.getMaterial(LoadConfig.guiString("nature_flags.items." + splittedlist.get(1)));

        if (material != null) {
            return material;
        } else {
            return Material.STRUCTURE_VOID;
        }
    }
}
