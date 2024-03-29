package realmprotection.commands.subcommands;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import realmprotection.managers.ChunksManager;
import realmprotection.managers.LandMembersManager;
import realmprotection.managers.LandsManager;
import realmprotection.utils.ColoredString;
import realmprotection.utils.LoadConfigString;

public class InfoCommand implements CommandExecutor {
        @Override
        public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
                if (sender instanceof Player) {
                        Player player = (Player) sender;
                        Chunk chunk = player.getLocation().getChunk();

                        boolean isclaimed = ChunksManager.isChunkClaimed(chunk);

                        if (!isclaimed) {
                                player.sendMessage(LoadConfigString.load("info.chunk_wilderness"));
                                return true;
                        }

                        String land_id = ChunksManager.getChunkDetail(chunk, "land_id");

                        // Creating new items
                        ItemStack landInformationButton = new ItemStack(
                                        Material.getMaterial(LoadConfigString
                                                        .guiString("land_info.content.land_information.type")));
                        ItemStack landOwnerButton = new ItemStack(
                                        Material.getMaterial(LoadConfigString
                                                        .guiString("land_info.content.land_owner.type")));
                        ItemStack landMembersButton = new ItemStack(
                                        Material.getMaterial(LoadConfigString
                                                        .guiString("land_info.content.land_members.type")));
                        ItemStack landCloseButton = new ItemStack(
                                        Material.getMaterial(LoadConfigString.generalString("gui.close_button.type")));

                        // Edit items
                        ItemMeta landInformationButtonMeta = landInformationButton.getItemMeta();
                        landInformationButtonMeta.setDisplayName(ColoredString.translate(
                                        LoadConfigString.guiString("land_info.content.land_information.displayname")
                                                        .replace("%land_name%",
                                                                        LandsManager.getLandDetailById(
                                                                                        new Integer(land_id),
                                                                                        "land_name"))));

                        ArrayList<String> landInformationButtonLore = new ArrayList<>();
                        List<String> loreConfigData = LoadConfigString
                                        .guiStringList("land_info.content.land_information.lore");

                        DateFormat dateFormatter = new SimpleDateFormat(LoadConfigString.generalString("date.format"));

                        double land_balance = Double.parseDouble(LandsManager.getLandDetailById(new Integer(land_id), "balance"));

                        System.out.println(land_balance);

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
                                        LoadConfigString.guiString("land_info.content.land_owner.displayname").replace(
                                                        "%land_owner%",
                                                        LandsManager.getLandDetailById(new Integer(land_id),
                                                                        "owner_name"))));
                        landOwnerButton.setItemMeta(landOwnerButtonMeta);

                        List<List<String>> membersdata = LandMembersManager.listAllMembersData(new Integer(land_id));

                        ItemMeta landMembersButtonMeta = landMembersButton.getItemMeta();
                        landMembersButtonMeta.setDisplayName(ColoredString.translate(LoadConfigString
                                        .guiString("land_info.content.land_members.displayname")
                                        .replace("%members_count%", "" + LandMembersManager
                                                        .getMembersCountOfLand(new Integer(land_id)))));

                        ArrayList<String> landMembersButtonLore = new ArrayList<>();

                        for (List<String> memberdata : membersdata) {
                                landMembersButtonLore
                                                .add(ColoredString
                                                                .translate(LoadConfigString.guiString(
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
                                        .translate(LoadConfigString.generalString("gui.close_button.displayname")));
                        landCloseButton.setItemMeta(landCloseButtonMeta);

                        // Create new inventory
                        Inventory inventory = Bukkit.createInventory(player, 9 * 3,
                                        ColoredString.translate(LoadConfigString.guiString("land_info.title")));

                        inventory.setItem(11, landInformationButton);
                        inventory.setItem(13, landOwnerButton);
                        inventory.setItem(15, landMembersButton);
                        if (LoadConfigString.generalBoolean("gui.close_button.enabled") == true) {
                                inventory.setItem(26, landCloseButton);
                        }

                        player.openInventory(inventory);

                        return true;
                } else {
                        return false;
                }
        }
}
