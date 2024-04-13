package realmprotection.events;

import java.util.ArrayList;
import java.util.Arrays;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;

import realmprotection.managers.LandStorageManager;
import realmprotection.utils.ColoredString;
import realmprotection.utils.LoadConfig;

public class GUIListener implements Listener {
    @EventHandler
    public void onClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();

        if (event.getView().getTitle().startsWith(ColoredString.translate(LoadConfig.guiString("land_info.title")))) {
            event.setCancelled(true);

            ItemStack item = event.getCurrentItem();

            if (item == null)
                return;

            switch (item.getType()) {
                case OAK_SIGN:
                    break;
                case PLAYER_HEAD:
                    break;
                case EMERALD:
                    event.setCancelled(true);
                    break;
                case BARRIER:
                    player.closeInventory();
                    break;
                default:
                    break;
            }
        } else if (event.getView().getTitle()
                .startsWith(ColoredString.translate(LoadConfig.guiString("role_flags.title")))) {
            event.setCancelled(true);

            ItemStack item = event.getCurrentItem();

            if (item == null)
                return;

            switch (item.getType()) {
                case BARRIER:
                    player.closeInventory();
                    break;
                case PAPER:
                    break;
                default:
                    break;
            }
        } else if (event.getView().getTitle()
                .startsWith(ColoredString.translate(LoadConfig.guiString("nature_flags.title")))) {
            event.setCancelled(true);

            ItemStack item = event.getCurrentItem();

            if (item == null)
                return;

            switch (item.getType()) {
                case BARRIER:
                    player.closeInventory();
                    break;
                case PAPER:
                    break;
                default:
                    break;
            }
        }
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();

        if (event.getView().getTitle().equalsIgnoreCase(ColoredString.translate(LoadConfig.guiString("land_storage.title")))) {

            ArrayList<ItemStack> pruneditems = new ArrayList<>();

            Arrays.stream(event.getInventory().getContents())
                    .filter(itemstack -> {
                        if (itemstack == null) {
                            return false;
                        }
                        return true;
                    })
                    .forEach(itemstack -> pruneditems.add(itemstack));

            LandStorageManager.storeItems(pruneditems, player);
        }

    }
}
