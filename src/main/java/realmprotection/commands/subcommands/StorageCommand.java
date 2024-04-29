package realmprotection.commands.subcommands;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import realmprotection.managers.LandStorageManager;
import realmprotection.managers.LandsManager;
import realmprotection.utils.ChatColorTranslator;
import realmprotection.utils.Language;

public class StorageCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (!LandsManager.hasLand(player.getName())) {
                player.sendMessage(Language.getCommand("storage.land_not_found"));
                return true;
            }

            List<List<Object>> storedItems = LandStorageManager.getItems(player);

            Inventory inventory = Bukkit.createInventory(player, 9 * 6,
                    ChatColorTranslator.translate((String) Language.get("gui.commands.land_storage.title")));

            for (List<Object> itemEntry : storedItems) {
                ItemStack itemStack = (ItemStack) itemEntry.get(0);
                int slotIndex = (int) itemEntry.get(1);
                if (slotIndex >= 0 && slotIndex < inventory.getSize()) {
                    inventory.setItem(slotIndex, itemStack);
                }
            }

            player.openInventory(inventory);

            return true;
        } else {
            return false;
        }
    }
}
