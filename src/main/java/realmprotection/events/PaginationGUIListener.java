package realmprotection.events;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;

import realmprotection.utils.PaginationGUI;

public class PaginationGUIListener implements Listener {
    @EventHandler
    public void onClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();

        if (PaginationGUI.inventory_cache.containsKey(player.getUniqueId().toString())) {
            event.setCancelled(true);

            Inventory inventory = PaginationGUI.inventory_cache.get(player.getUniqueId().toString());
            int inventory_size = inventory.getSize() / 9;

            int first_index_last_line = 9 * (inventory_size - 1);
            int last_index_last_line = (9 * inventory_size) - 1;

            if (event.getSlot() == first_index_last_line) {
                PaginationGUI pagegui = PaginationGUI.pagegui_cache.get(player.getUniqueId().toString());

                pagegui.previousPage();
            } else if (event.getSlot() == last_index_last_line) {
                PaginationGUI pagegui = PaginationGUI.pagegui_cache.get(player.getUniqueId().toString());

                pagegui.nextPage();
            }
        }
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();

        if (PaginationGUI.inventory_cache.containsKey(player.getUniqueId().toString())) {
            PaginationGUI.inventory_cache.remove(player.getUniqueId().toString());
            PaginationGUI.pagegui_cache.remove(player.getUniqueId().toString());
        }
    }
}
