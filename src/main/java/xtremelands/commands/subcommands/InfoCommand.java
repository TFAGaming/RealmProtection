package xtremelands.commands.subcommands;

import java.util.ArrayList;
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

import xtremelands.managers.ChunksManager;
import xtremelands.managers.LandMembersManager;
import xtremelands.managers.LandsManager;
import xtremelands.utils.ColoredString;
import xtremelands.utils.LoadConfigString;

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
            ItemStack landInformationButton = new ItemStack(Material.OAK_SIGN);
            ItemStack landOwnerButton = new ItemStack(Material.PLAYER_HEAD);
            ItemStack landMembersButton = new ItemStack(Material.EMERALD);
            ItemStack landCloseButton = new ItemStack(Material.BARRIER);

            // Edit items
            ItemMeta landInformationButtonMeta = landInformationButton.getItemMeta();
            landInformationButtonMeta.setDisplayName(ColoredString.translate("&6Land: &r%land_name%".replace("%land_name%", LandsManager.getLandDetailById(new Integer(land_id), "land_name"))));
            ArrayList<String> landInformationButtonLore = new ArrayList<>();
            landInformationButtonLore.add(ColoredString.translate("&bID: &f%land_id%".replace("%chunk_x%", ChunksManager.getChunkDetail(chunk, "chunk_x")).replace("%chunk_z%", ChunksManager.getChunkDetail(chunk, "chunk_z")).replace("%land_id%", land_id).replace("%chunk_id%", ChunksManager.getChunkDetail(chunk, "id"))));
            landInformationButtonLore.add(ColoredString.translate("&bChunk X: &f%chunk_x%".replace("%chunk_x%", ChunksManager.getChunkDetail(chunk, "chunk_x")).replace("%chunk_z%", ChunksManager.getChunkDetail(chunk, "chunk_z")).replace("%land_id%", land_id).replace("%chunk_id%", ChunksManager.getChunkDetail(chunk, "id"))));
            landInformationButtonLore.add(ColoredString.translate("&bChunk Z: &f%chunk_z%".replace("%chunk_x%", ChunksManager.getChunkDetail(chunk, "chunk_x")).replace("%chunk_z%", ChunksManager.getChunkDetail(chunk, "chunk_z")).replace("%land_id%", land_id).replace("%chunk_id%", ChunksManager.getChunkDetail(chunk, "id"))));
            landInformationButtonLore.add(ColoredString.translate("&bChunk ID: &f%chunk_id%".replace("%chunk_x%", ChunksManager.getChunkDetail(chunk, "chunk_x")).replace("%chunk_z%", ChunksManager.getChunkDetail(chunk, "chunk_z")).replace("%land_id%", land_id).replace("%chunk_id%", ChunksManager.getChunkDetail(chunk, "id"))));
            landInformationButtonMeta.setLore(landInformationButtonLore);
            landInformationButton.setItemMeta(landInformationButtonMeta);

            ItemMeta landOwnerButtonMeta = landOwnerButton.getItemMeta();
            landOwnerButtonMeta.setDisplayName(ColoredString.translate("&6Owner: &c%owner_name%".replace("%owner_name%", LandsManager.getLandDetailById(new Integer(land_id), "owner_name"))));
            landOwnerButton.setItemMeta(landOwnerButtonMeta);

            List<List<String>> membersdata = LandMembersManager.listAllMembersData(new Integer(land_id));

            ItemMeta landMembersButtonMeta = landMembersButton.getItemMeta();
            landMembersButtonMeta.setDisplayName(ColoredString.translate("&6Members"));
            ArrayList<String> landMembersButtonLore = new ArrayList<>();
            for (List<String> memberdata : membersdata) {
                landMembersButtonLore.add(ColoredString.translate("&r&f" + memberdata.get(1) + " &f" + memberdata.get(0)));
            }
            landMembersButtonMeta.setLore(landMembersButtonLore);
            landMembersButton.setItemMeta(landMembersButtonMeta);

            ItemMeta landCloseButtonMeta = landCloseButton.getItemMeta();
            landCloseButtonMeta.setDisplayName(ColoredString.translate("&cClose"));
            landCloseButton.setItemMeta(landCloseButtonMeta);

            // Create new inventory
            Inventory inventory = Bukkit.createInventory(player, 9 * 3, ColoredString.translate("&9Land Information"));

            inventory.setItem(11, landInformationButton);
            inventory.setItem(13, landOwnerButton);
            inventory.setItem(15, landMembersButton);
            inventory.setItem(26, landCloseButton);

            player.openInventory(inventory);
            
            return true;
        } else {
            return false;
        }
    }
}
