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
                    /*
                    int slot = event.getSlot();

                    List<String> flags = new ArrayList<>();

                    flags.add("breakblocks");
                    flags.add("placeblocks");
                    flags.add("containers");
                    flags.add("redstone");
                    flags.add("doors");
                    flags.add("trapdoors");
                    flags.add("editsigns");
                    flags.add("emptybuckets");
                    flags.add("fillbuckets");
                    flags.add("harvestcrops");
                    flags.add("frostwalker");
                    flags.add("shearentities");
                    flags.add("itemframes");
                    flags.add("fencegates");
                    flags.add("buttons");
                    flags.add("levers");
                    flags.add("pressureplates");
                    flags.add("bells");
                    flags.add("tripwires");
                    flags.add("armorstands");
                    flags.add("teleporttospawn");
                    flags.add("throwenderpearls");
                    flags.add("throwpotions");
                    flags.add("damagehostilemobs");
                    flags.add("damagepassivemobs");
                    flags.add("pvp");
                    flags.add("usecauldron");
                    flags.add("pickupitems");
                    flags.add("useanvil");
                    flags.add("createfire");
                    flags.add("usevehicles");

                    String flag = flags.get(slot);

                    String land_id = LandsManager.getLandDetail(event.getWhoClicked().getName(), "id");

                    RolesManager.updatePermissionValue(new Integer(land_id), args[2], args[3], value);
                    */
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

    }
}
