package realmprotection.events;

import java.util.Comparator;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;

import realmprotection.gui.RoleFlagsGUI;
import realmprotection.managers.ChunksManager;
import realmprotection.managers.LandsManager;
import realmprotection.managers.RolesManager;
import realmprotection.utils.ChatColorTranslator;
import realmprotection.utils.Language;
import realmprotection.utils.PaginationGUI;

public class PaginationGUIListener implements Listener {
    @EventHandler
    public void onClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();

        if (PaginationGUI.inventory_cache.containsKey(player.getUniqueId().toString())) {
            event.setCancelled(true);

            if (event.getView().getTitle()
                    .startsWith(ChatColorTranslator.translate((String) Language.get("gui.commands.land_claimlist.title")))) {
                Inventory inventory = PaginationGUI.inventory_cache.get(player.getUniqueId().toString());
                int inventory_size = inventory.getSize() / 9;

                int first_index_last_line = 9 * (inventory_size - 1);
                int last_index_last_line = (9 * inventory_size) - 1;

                PaginationGUI pagegui = PaginationGUI.pagegui_cache.get(player.getUniqueId().toString());

                if (event.getSlot() == first_index_last_line) {
                    pagegui.previousPage();
                } else if (event.getSlot() == last_index_last_line) {
                    pagegui.nextPage();
                } else {
                    int clicked_chunk = event.getSlot();

                    if (clicked_chunk > (9 * 3) - 1) {
                        return;
                    }

                    String land_id = LandsManager.getLandDetail(player.getUniqueId().toString(), "id");

                    List<List<Object>> chunks = ChunksManager.listChunksFromLandId(new Integer(land_id));

                    chunks.sort(Comparator.comparingLong((List<Object> list) -> (long) list.get(4)));

                    int pageIndex = pagegui.getPage();
                    int slotIndex = event.getSlot();

                    if (pageIndex >= 0 && slotIndex >= 0) {
                        int itemsPerPage = 9;
                        int chunkIndex = pageIndex * itemsPerPage + slotIndex;

                        if (chunkIndex < chunks.size()) {
                            List<Object> chunk = chunks.get(chunkIndex);
                            int chunk_x = (Integer) chunk.get(0);
                            int chunk_z = (Integer) chunk.get(1);
                            String chunk_world = (String) chunk.get(2);

                            player.closeInventory();

                            ChunksManager.teleportPlayerToChunk(player, chunk_x, chunk_z, Bukkit.getWorld(chunk_world));
                        }
                    }
                }
            } else if (event.getView().getTitle()
                    .startsWith(ChatColorTranslator.translate((String) Language.get("gui.commands.roles_list.title")))) {
                Inventory inventory = PaginationGUI.inventory_cache.get(player.getUniqueId().toString());
                int inventory_size = inventory.getSize() / 9;

                int first_index_last_line = 9 * (inventory_size - 1);
                int last_index_last_line = (9 * inventory_size) - 1;

                PaginationGUI pagegui = PaginationGUI.pagegui_cache.get(player.getUniqueId().toString());

                if (event.getSlot() == first_index_last_line) {
                    pagegui.previousPage();
                } else if (event.getSlot() == last_index_last_line) {
                    pagegui.nextPage();
                } else {
                    int clicked_chunk = event.getSlot();

                    if (clicked_chunk > (9 * 3) - 1) {
                        return;
                    }

                    String land_id = LandsManager.getLandDetail(player.getUniqueId().toString(), "id");

                    List<List<Object>> roles = RolesManager.listAllRolesData(new Integer(land_id));

                    roles.sort(Comparator.comparingInt((List<Object> list) -> (int) list.get(1)));

                    int pageIndex = pagegui.getPage();
                    int slotIndex = event.getSlot();

                    if (pageIndex >= 0 && slotIndex >= 0) {
                        int itemsPerPage = 9;
                        int roleIndex = pageIndex * itemsPerPage + slotIndex;

                        if (roleIndex < roles.size()) {
                            List<Object> chunk = roles.get(roleIndex);
                            String role_name = (String) chunk.get(0);

                            player.closeInventory();

                            RoleFlagsGUI.create(player, role_name, 1);
                        }
                    }
                }
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
