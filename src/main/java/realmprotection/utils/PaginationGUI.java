package realmprotection.utils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class PaginationGUI {
    public static Map<String, Inventory> inventory_cache = new HashMap<>();
    public static Map<String, PaginationGUI> pagegui_cache = new HashMap<>();

    private Player player;
    private Map<Integer, List<ItemStack>> data = new HashMap<>();
    private Inventory inventory;
    private int inventory_lines;
    private int total_pages;

    private int current_index = 0;

    public PaginationGUI(Player player, int lines, String title, int total_pages) {
        this.player = player;
        this.inventory = Bukkit.createInventory(player, 9 * lines, title);
        this.inventory_lines = lines;
        this.total_pages = total_pages;
    }

    public void addPage(int page_index, List<ItemStack> items) {
        data.put(page_index, items);
    }

    public Map<Integer, List<ItemStack>> getPages() {
        return data;
    }

    public void setPage(int page_index) {
        current_index = page_index;
    }

    public int getPage() {
        return current_index;
    }

    public void nextPage() {
        int new_index = current_index + 1;

        if (new_index > (total_pages - 1)) {
            //new_index = 0;
            return;
        }

        updatePage(new_index, true);

        current_index = new_index;
    }

    public void previousPage() {
        int new_index = current_index - 1;

        if (new_index < 0) {
            // new_index = total_pages - 1;
            return;
        }

        updatePage(new_index, true);

        current_index = new_index;
    }

    public void openInventory(PaginationGUI pagegui) {
        updatePage(0, false);

        player.openInventory(inventory);

        inventory_cache.put(player.getUniqueId().toString(), inventory);
        pagegui_cache.put(player.getUniqueId().toString(), pagegui);
    }

    private void updatePage(int page_index, boolean clear_inventory) {
        if (clear_inventory) {
            inventory.clear();
        }

        List<ItemStack> items = data.get(page_index);

        for (int i = 0; i < items.size(); i++) {
            inventory.setItem(i, items.get(i));
        }

        updateMenuButtons(page_index);
    }

    private void updateMenuButtons(int page_index) {
        int first_index_last_line = 9 * (inventory_lines - 1);
        int last_index_last_line = (9 * inventory_lines) - 1;

        // Back button
        String arrow_backType = (String) Language.get("gui.paginator.previous_button.type");
		ItemStack arrow_back = arrow_backType.startsWith("TEXTURE-")
				? CustomHeadTexture.get(Arrays.asList(arrow_backType.split("-")).get(1))
				: new ItemStack(Material.getMaterial(arrow_backType));

        ItemMeta arrow_back_meta = arrow_back.getItemMeta();
		arrow_back_meta.setDisplayName(ChatColorTranslator.translate((String) Language.get("gui.paginator.previous_button.displayname")));
		arrow_back.setItemMeta(arrow_back_meta);

        // Page info
        String page_infoType = (String) Language.get("gui.paginator.page_info.type");
		ItemStack page_info = page_infoType.startsWith("TEXTURE-")
				? CustomHeadTexture.get(Arrays.asList(page_infoType.split("-")).get(1))
				: new ItemStack(Material.getMaterial(page_infoType));

        ItemMeta page_info_meta = page_info.getItemMeta();
		page_info_meta.setDisplayName(ChatColorTranslator.translate(((String) Language.get("gui.paginator.page_info.displayname")).replace("%page%", "" + (page_index + 1)).replace("%total_pages%", "" + (total_pages))));
		page_info.setItemMeta(page_info_meta);

        // Next button
        String arrow_nextType = (String) Language.get("gui.paginator.next_button.type");
		ItemStack arrow_next = arrow_nextType.startsWith("TEXTURE-")
				? CustomHeadTexture.get(Arrays.asList(arrow_nextType.split("-")).get(1))
				: new ItemStack(Material.getMaterial(arrow_nextType));

        ItemMeta arrow_next_meta = arrow_next.getItemMeta();
		arrow_next_meta.setDisplayName(ChatColorTranslator.translate((String) Language.get("gui.paginator.next_button.displayname")));
		arrow_next.setItemMeta(arrow_next_meta);

        inventory.setItem(last_index_last_line, arrow_next);
        inventory.setItem(first_index_last_line + 4, page_info);
        inventory.setItem(first_index_last_line, arrow_back);
    }
}
