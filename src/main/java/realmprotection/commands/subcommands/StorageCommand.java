package realmprotection.commands.subcommands;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import realmprotection.managers.LandStorageManager;
import realmprotection.managers.LandsManager;
import realmprotection.utils.ColoredString;
import realmprotection.utils.LoadConfig;

public class StorageCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (!LandsManager.hasLand(player.getName())) {
                player.sendMessage(LoadConfig.commandString("storage.land_not_found"));
                return true;
            }

            ArrayList<ItemStack> storageitems = LandStorageManager.getItems(player);

            Inventory inventory = Bukkit.createInventory(player, 9 * 6, ColoredString.translate(LoadConfig.guiString("land_storage.title")));

            storageitems.stream().forEach(itemstack -> inventory.addItem(itemstack));

            player.openInventory(inventory);

            return true;
        } else {
            return false;
        }
    }
}
