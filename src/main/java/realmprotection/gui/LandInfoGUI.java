package realmprotection.gui;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import realmprotection.managers.ChunksManager;
import realmprotection.managers.LandMembersManager;
import realmprotection.managers.LandsManager;
import realmprotection.utils.ColoredString;
import realmprotection.utils.LoadConfig;

public class LandInfoGUI {
    public static void create(Player player, String land_id) {
        Inventory inventory = Bukkit.createInventory(player, 9 * 3,
                ColoredString.translate(LoadConfig.guiString("land_info.title")));

        ItemStack landInformationButton = new ItemStack(
                Material.getMaterial(LoadConfig
                        .guiString("land_info.content.land_information.type")));
        ItemStack landOwnerButton = new ItemStack(
                Material.getMaterial(LoadConfig
                        .guiString("land_info.content.land_owner.type")));
        ItemStack landMembersButton = new ItemStack(
                Material.getMaterial(LoadConfig
                        .guiString("land_info.content.land_members.type")));
        ItemStack landCloseButton = new ItemStack(
                Material.getMaterial(LoadConfig.generalString("gui.close_button.type")));

        ItemMeta landInformationButtonMeta = landInformationButton.getItemMeta();
        landInformationButtonMeta.setDisplayName(ColoredString.translate(
                LoadConfig.guiString("land_info.content.land_information.displayname")
                        .replace("%land_name%",
                                LandsManager.getLandDetailById(
                                        new Integer(land_id),
                                        "land_name"))));

        ArrayList<String> landInformationButtonLore = new ArrayList<>();
        List<String> loreConfigData = LoadConfig
                .guiStringList("land_info.content.land_information.lore");

        DateFormat dateFormatter = new SimpleDateFormat(LoadConfig.generalString("date.format"));

        double land_balance = Double
                .parseDouble(LandsManager.getLandDetailById(new Integer(land_id), "balance"));

        for (String lore : loreConfigData) {
            landInformationButtonLore.add(ColoredString.translate(lore
                    .replace("%land_id%", land_id)
                    .replace("%chunks%",
                            "" + ChunksManager.getChunksCountOfLand(
                                    new Integer(land_id)))
                    .replace("%balance%",
                            String.format("%.2f", land_balance))
                    .replace("%created_at%",
                            dateFormatter.format(
                                    new Date(new Long(LandsManager
                                            .getLandDetailById(
                                                    new Integer(land_id),
                                                    "created_at")))))));
        }

        landInformationButtonMeta.setLore(landInformationButtonLore);
        landInformationButton.setItemMeta(landInformationButtonMeta);

        ItemMeta landOwnerButtonMeta = landOwnerButton.getItemMeta();
        landOwnerButtonMeta.setDisplayName(ColoredString.translate(
                LoadConfig.guiString("land_info.content.land_owner.displayname").replace(
                        "%land_owner%",
                        LandsManager.getLandDetailById(new Integer(land_id),
                                "owner_name"))));
        landOwnerButton.setItemMeta(landOwnerButtonMeta);

        List<List<String>> membersdata = LandMembersManager.listAllMembersData(new Integer(land_id));

        ItemMeta landMembersButtonMeta = landMembersButton.getItemMeta();
        landMembersButtonMeta.setDisplayName(ColoredString.translate(LoadConfig
                .guiString("land_info.content.land_members.displayname")
                .replace("%members_count%", "" + LandMembersManager
                        .getMembersCountOfLand(new Integer(land_id)))));

        ArrayList<String> landMembersButtonLore = new ArrayList<>();

        for (List<String> memberdata : membersdata) {
            landMembersButtonLore
                    .add(ColoredString
                            .translate(LoadConfig.guiString(
                                    "land_info.content.land_members.lore_style")
                                    .replace("%role_name%",
                                            memberdata.get(1))
                                    .replace("%member_name%",
                                            memberdata.get(0))));
        }

        landMembersButtonMeta.setLore(landMembersButtonLore);
        landMembersButton.setItemMeta(landMembersButtonMeta);

        ItemMeta landCloseButtonMeta = landCloseButton.getItemMeta();
        landCloseButtonMeta.setDisplayName(ColoredString
                .translate(LoadConfig.generalString("gui.close_button.displayname")));
        landCloseButton.setItemMeta(landCloseButtonMeta);

        inventory.setItem(11, landInformationButton);
        inventory.setItem(13, landOwnerButton);
        inventory.setItem(15, landMembersButton);
        if (LoadConfig.generalBoolean("gui.close_button.enabled") == true) {
            inventory.setItem(26, landCloseButton);
        }

        player.openInventory(inventory);
    }
}
