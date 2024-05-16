package realmprotection.gui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import realmprotection.managers.ChunksManager;
import realmprotection.managers.LandsManager;
import realmprotection.utils.ChatColorTranslator;
import realmprotection.utils.CustomHeadTexture;
import realmprotection.utils.Language;
import realmprotection.utils.PaginationGUI;

public class ClaimListPaginationGUI {
    public static void create(Player player) {
        String land_id = LandsManager.getLandDetail(player.getUniqueId().toString(), "id");

        List<List<Object>> chunks = ChunksManager.listChunksFromLandId(new Integer(land_id));

        chunks.sort(Comparator.comparingLong((List<Object> list) -> (long) list.get(4)));

        int chunks_count = ChunksManager.getChunksCountOfLand(new Integer(land_id));

        int total_pages = (int) Math.ceil((float) chunks_count / 9.0);

        if (total_pages <= 0) {
            total_pages = 1;
        }

        PaginationGUI pagegui = new PaginationGUI(player, 3,
                ChatColorTranslator.translate((String) getFromLanguage("title")),
                total_pages);

        List<List<ItemStack>> pages = new ArrayList<>();

        for (int index = 0; index < total_pages; index++) {
            List<ItemStack> page = new ArrayList<>();

            int startIndex = index * 9;
            int endIndex = Math.min(startIndex + 9, chunks.size());

            for (int i = startIndex; i < endIndex; i++) {
                List<Object> chunk = chunks.get(i);
                int chunk_x = (Integer) chunk.get(0);
                int chunk_z = (Integer) chunk.get(1);
                String chunk_world = (String) chunk.get(2);

                String chunkButtonType = getItemBasedWorldName(chunk_world);
                ItemStack chunkButton = chunkButtonType.startsWith("TEXTURE-")
                        ? CustomHeadTexture.get(Arrays.asList(chunkButtonType.split("-")).get(1))
                        : new ItemStack(Material.getMaterial(chunkButtonType));

                ItemMeta chunkButtonMeta = chunkButton.getItemMeta();
                chunkButtonMeta.setDisplayName(
                        ChatColorTranslator.translate(((String) getFromLanguage("content.__ITEM_DISPLAYNAME__"))
                                .replace("%chunk_x%", "" + chunk_x).replace("%chunk_z%", "" + chunk_z)
                                .replace("%chunk_world%", "" + chunk_world)));
                chunkButton.setItemMeta(chunkButtonMeta);

                page.add(chunkButton);
            }

            pages.add(page);
        }

        for (int i = 0; i < pages.size(); i++) {
            pagegui.addPage(i, pages.get(i));
        }

        pagegui.openInventory(pagegui);
    }

    private static String getItemBasedWorldName(String world_name) {
        String grass_block_texture = (String) getFromLanguage("content.__ITEM_TYPE__.__DEFAULT__");
        String netherrack_texture = (String) getFromLanguage("content.__ITEM_TYPE__.__NETHER__");
        String endstone_texture = (String) getFromLanguage("content.__ITEM_TYPE__.__END__");

        if (world_name.contains("nether")) {
            return netherrack_texture;
        } else if (world_name.contains("end")) {
            return endstone_texture;
        } else {
            return grass_block_texture;
        }
    }

    private static Object getFromLanguage(String path) {
        return Language.get("gui.commands.land_claimlist." + path);
    }
}
