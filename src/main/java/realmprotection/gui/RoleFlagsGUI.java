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
import realmprotection.managers.RolesManager;
import realmprotection.utils.ColoredString;
import realmprotection.utils.LoadConfig;

public class RoleFlagsGUI {
    public static void create(Player player, String role_name) {
        String land_id = LandsManager.getLandDetail(player.getName(), "id");

        Inventory inventory = Bukkit.createInventory(player, 9 * 6,
                ColoredString.translate(LoadConfig.guiString("role_flags.title")));

        List<List<Object>> allflags = RolesManager.listEnabledAndDisabledFlagsForRole(new Integer(land_id),
                role_name);

        for (List<Object> flag : allflags) {
            ItemStack flagButton = new ItemStack(
                    getMaterialItemFromPermissionName("permissions_" + flag.get(0)));

            ItemMeta flagButtonMeta = flagButton.getItemMeta();
            flagButtonMeta.setDisplayName(ColoredString
                    .translate(LoadConfig.guiString("role_flags.content.flag_style.displayname")
                            .replace("%flag%", "" + flag.get(0)).replace("%displayname%",
                                    LoadConfig.guiString("flags_displayname.role_flags." + flag.get(0)))));

            ArrayList<String> flagButtonLore = new ArrayList<>();
            List<String> roleflagslore = LoadConfig
                    .guiStringList("role_flags.content.flag_style.lore");

            for (String lore : roleflagslore) {
                if ((Boolean) flag.get(1) == true) {
                    flagButtonLore.add(ColoredString
                            .translate(lore
                                    .replace("%description%",
                                            "" + LoadConfig.guiString(
                                                    "flags_description.role_flags." + flag.get(0)))
                                    .replace("%value%", LoadConfig.generalString("flags.enabled"))));
                } else {
                    flagButtonLore.add(ColoredString
                            .translate(lore
                                    .replace("%description%",
                                            "" + LoadConfig.guiString(
                                                    "flags_description.role_flags." + flag.get(0)))
                                    .replace("%value%", LoadConfig.generalString("flags.disabled"))));
                }
            }

            flagButtonMeta.setLore(flagButtonLore);
            flagButton.setItemMeta(flagButtonMeta);

            inventory.addItem(flagButton);
        }

        ItemStack roleNameButton = new ItemStack(
                Material.getMaterial(LoadConfig.guiString("role_flags.content.role_name.type")));
        ItemMeta roleNameButtonMeta = roleNameButton.getItemMeta();
        roleNameButtonMeta.setDisplayName(ColoredString.translate(LoadConfig
                .guiString("role_flags.content.role_name.displayname").replace("%role%", role_name)));
        roleNameButton.setItemMeta(roleNameButtonMeta);

        ItemStack closeButton = new ItemStack(
                Material.getMaterial(LoadConfig.generalString("gui.close_button.type")));
        ItemMeta closeButtonMeta = closeButton.getItemMeta();
        closeButtonMeta.setDisplayName(
                ColoredString.translate(LoadConfig.generalString("gui.close_button.displayname")));
        closeButton.setItemMeta(closeButtonMeta);

        inventory.setItem(45, roleNameButton);
        if (LoadConfig.generalBoolean("gui.close_button.enabled") == true) {
            inventory.setItem(53, closeButton);
        }

        player.openInventory(inventory);
    }

    public static Material getMaterialItemFromPermissionName(String permission) {
        String[] splitted = permission.split("_");
        List<String> splittedlist = new ArrayList<>();

        for (String split : splitted) {
            splittedlist.add(split);
        }

        Material material = Material.getMaterial(LoadConfig.guiString("role_flags.items." + splittedlist.get(1)));

        if (material != null) {
            return material;
        } else {
            return Material.STRUCTURE_VOID;
        }
    }
}
