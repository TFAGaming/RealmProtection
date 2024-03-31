package realmprotection.events;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;

import realmprotection.utils.ColoredString;
import realmprotection.utils.LoadConfig;

public class GUIListener implements Listener {
    @EventHandler
    public void onClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();

        if (event.getView().getTitle().startsWith(ColoredString.translate(LoadConfig.guiString("land_info.title")))) {
            event.setCancelled(true);

            ItemStack item = event.getCurrentItem();

            if (item == null) return;

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
        } else if (event.getView().getTitle().startsWith(ColoredString.translate(LoadConfig.guiString("role_flags.title")))) {
            event.setCancelled(true);

            ItemStack item = event.getCurrentItem();

            if (item == null) return;

            switch (item.getType()) {
                case BARRIER:
                    player.closeInventory();
                    break;
                case PAPER:
                    break;
                default:
                    player.sendMessage(ColoredString.translate(
                            "&cSorry, but you can only edit flags values using the commands for this moment."));
            }
        } else if (event.getView().getTitle().startsWith(ColoredString.translate(LoadConfig.guiString("nature_flags.title")))) {
            event.setCancelled(true);

            ItemStack item = event.getCurrentItem();

            if (item == null) return;

            switch (item.getType()) {
                case BARRIER:
                    player.closeInventory();
                    break;
                case PAPER:
                    break;
                default:
                    player.sendMessage(ColoredString.translate(
                            "&cSorry, but you can only edit flags values using the commands for this moment."));
            }
        }
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {

    }
}
