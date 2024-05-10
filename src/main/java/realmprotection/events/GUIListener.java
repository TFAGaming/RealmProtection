package realmprotection.events;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import realmprotection.managers.LandStorageManager;
import realmprotection.managers.LandsManager;
import realmprotection.utils.ChatColorTranslator;
import realmprotection.utils.Language;

public class GUIListener implements Listener {
    @EventHandler
    public void onClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();

        if (event.getView().getTitle().startsWith(ChatColorTranslator.translate((String) Language.get("gui.commands.land_info.title")))) {
            event.setCancelled(true);

            ItemStack item = event.getCurrentItem();

            if (item == null) {
                return;
            }

            if (item.getType().equals(Material.getMaterial((String) Language.get("general.guis.close_button.item.type")))) {
                player.closeInventory();
                return;
            }
        } else if (event.getView().getTitle()
                .startsWith(ChatColorTranslator.translate((String) Language.get("gui.commands.role_flags.title")))) {
            event.setCancelled(true);

            ItemStack item = event.getCurrentItem();

            if (item == null) {
                return;
            }

            if (item.getType().equals(Material.getMaterial((String) Language.get("general.guis.close_button.item.type")))) {
                player.closeInventory();
                return;
            }
        } else if (event.getView().getTitle()
                .startsWith(ChatColorTranslator.translate((String) Language.get("gui.commands.nature_flags.title")))) {
            event.setCancelled(true);

            ItemStack item = event.getCurrentItem();

            if (item == null) {
                return;
            }

            if (item.getType().equals(Material.getMaterial((String) Language.get("general.guis.close_button.item.type")))) {
                player.closeInventory();
                return;
            }

            int index = event.getSlot();
            List<String> list_flags = LandsManager.listAllFlags();

            if (index + 1 > list_flags.size()) {
                return;
            }

            String flag = list_flags.get(index);
            String land_id = LandsManager.getLandDetail(player.getUniqueId().toString(), "id");

            if (land_id == null) {
                return;
            }

            boolean value = !LandsManager.getFlagValue(new Integer(land_id), flag);

            LandsManager.updateNatureFlagValue(new Integer(land_id), flag, value);

            ItemStack flagButton = new ItemStack(
					_getMaterialItemFromFlagNameNatueFlags("nature_" + flag));

			ItemMeta flagButtonMeta = flagButton.getItemMeta();
			flagButtonMeta.setDisplayName(ChatColorTranslator
					.translate(((String) _getFromLanguageNatureFlags("content.__FLAGS_REPEAT_STYLE__.displayname"))
							.replace("%flag%", "" + flag).replace("%displayname%",
									(String) Language.get("flags.nature_flags.displayname." + flag))));

			ArrayList<String> flagButtonLore = new ArrayList<>();
			@SuppressWarnings("unchecked")
			List<String> natureflagslore = (List<String>) _getFromLanguageNatureFlags("content.__FLAGS_REPEAT_STYLE__.lore");

			for (String lore : natureflagslore) {
				if ((boolean) value == true) {
					flagButtonLore.add(ChatColorTranslator
							.translate(lore
									.replace("%description%",
											"" + (String) Language.get("flags.nature_flags.description." + flag))
									.replace("%value%", (String) Language.get("general.flags.enabled"))));
				} else {
					flagButtonLore.add(ChatColorTranslator
							.translate(lore
									.replace("%description%",
											"" + (String) Language.get("flags.nature_flags.description." + flag))
									.replace("%value%", (String) Language.get("general.flags.disabled"))));
				}
			}

			flagButtonMeta.setLore(flagButtonLore);
			flagButton.setItemMeta(flagButtonMeta);

            event.getInventory().setItem(index, flagButton);
        }
    }

    private static Material _getMaterialItemFromFlagNameNatueFlags(String flag) {
		String[] splitted = flag.split("_");
		List<String> splittedlist = new ArrayList<>();

		for (String split : splitted) {
			splittedlist.add(split);
		}

		Material material = Material.getMaterial((String) _getFromLanguageNatureFlags("__FLAGS_REPEAT_STYLE_ITEMS_CONFIG__." + splittedlist.get(1)));

		if (material != null) {
			return material;
		} else {
			return Material.STRUCTURE_VOID;
		}
	}

	private static Object _getFromLanguageNatureFlags(String path) {
		return Language.get("gui.commands.nature_flags." + path);
	}

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();

        if (event.getView().getTitle()
                .equalsIgnoreCase(ChatColorTranslator.translate((String) Language.get("gui.commands.land_storage.title")))) {
            List<List<Object>> prunedItems = new ArrayList<>();

            for (int slot = 0; slot < event.getInventory().getSize(); slot++) {
                ItemStack itemStack = event.getInventory().getItem(slot);

                if (itemStack != null) {
                    List<Object> itemEntry = new ArrayList<>();

                    itemEntry.add(itemStack);
                    itemEntry.add(slot);

                    prunedItems.add(itemEntry);
                }
            }

            LandStorageManager.storeItems(prunedItems, player);
        }

    }
}
