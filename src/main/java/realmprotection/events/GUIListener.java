package realmprotection.events;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import realmprotection.gui.RoleFlagsGUI;
import realmprotection.managers.LandStorageManager;
import realmprotection.managers.LandsManager;
import realmprotection.managers.RolesManager;
import realmprotection.utils.ChatColorTranslator;
import realmprotection.utils.CustomHeadTexture;
import realmprotection.utils.Language;

public class GUIListener implements Listener {
    @EventHandler
    public void onClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();

        if (event.getView().getTitle()
                .startsWith(ChatColorTranslator.translate((String) Language.get("gui.commands.land_info.title")))) {
            event.setCancelled(true);

            ItemStack item = event.getCurrentItem();

            if (item == null) {
                return;
            }

            if (item.getType()
                    .equals(Material.getMaterial((String) Language.get("general.guis.close_button.item.type")))) {
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

            if (item.getType()
                    .equals(Material.getMaterial((String) Language.get("general.guis.close_button.item.type")))) {
                player.closeInventory();
                return;
            }

            String role_name = RoleFlagsGUI.cache.get(player.getUniqueId().toString());

            if (role_name == null) {
                return;
            }

            int index = event.getSlot();
            List<String> list_permissions = RolesManager.listAllPermissions();

            if (index + 1 > list_permissions.size()) {
                return;
            }

            String permission = list_permissions.get(index);
            String land_id = LandsManager.getLandDetail(player.getUniqueId().toString(), "id");

            if (land_id == null) {
                return;
            }

            boolean value = !RolesManager.getPermissionValue(new Integer(land_id), role_name, permission);

            RolesManager.updatePermissionValue(new Integer(land_id), role_name, permission, value);

            ItemStack flagButton = getItemFromPermissionNameRoleFlags("permissions_" + permission);

			ItemMeta flagButtonMeta = flagButton.getItemMeta();
			flagButtonMeta.setDisplayName(ChatColorTranslator
					.translate(((String) getFromLanguageRoleFlags("content.__FLAGS_REPEAT_STYLE__.displayname"))
							.replace("%flag%", "" + permission).replace("%displayname%",
									(String) Language.get("flags.role_flags.displayname." + permission))));

			ArrayList<String> flagButtonLore = new ArrayList<>();
			@SuppressWarnings("unchecked")
			List<String> roleflagslore = (List<String>) getFromLanguageRoleFlags("content.__FLAGS_REPEAT_STYLE__.lore");

			for (String lore : roleflagslore) {
				if ((boolean) value == true) {
					flagButtonLore.add(ChatColorTranslator
							.translate(lore
									.replace("%description%",
											"" + (String) Language.get("flags.role_flags.description." + permission))
									.replace("%value%", (String) Language.get("general.flags.enabled"))));
				} else {
					flagButtonLore.add(ChatColorTranslator
							.translate(lore
									.replace("%description%",
											"" + (String) Language.get("flags.role_flags.description." + permission))
									.replace("%value%", (String) Language.get("general.flags.disabled"))));
				}
			}

			flagButtonMeta.setLore(flagButtonLore);
			flagButton.setItemMeta(flagButtonMeta);

            event.getInventory().setItem(index, flagButton);
        } else if (event.getView().getTitle()
                .startsWith(ChatColorTranslator.translate((String) Language.get("gui.commands.nature_flags.title")))) {
            event.setCancelled(true);

            ItemStack item = event.getCurrentItem();

            if (item == null) {
                return;
            }

            if (item.getType()
                    .equals(Material.getMaterial((String) Language.get("general.guis.close_button.item.type")))) {
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

            ItemStack flagButton = getItemFromFlagNatureFlags("nature_" + flag);

            ItemMeta flagButtonMeta = flagButton.getItemMeta();
            flagButtonMeta.setDisplayName(ChatColorTranslator
                    .translate(((String) getFromLanguageNatureFlags("content.__FLAGS_REPEAT_STYLE__.displayname"))
                            .replace("%flag%", "" + flag).replace("%displayname%",
                                    (String) Language.get("flags.nature_flags.displayname." + flag))));

            ArrayList<String> flagButtonLore = new ArrayList<>();
            @SuppressWarnings("unchecked")
            List<String> natureflagslore = (List<String>) getFromLanguageNatureFlags(
                    "content.__FLAGS_REPEAT_STYLE__.lore");

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

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();

        if (event.getView().getTitle()
                .equalsIgnoreCase(
                        ChatColorTranslator.translate((String) Language.get("gui.commands.land_storage.title")))) {
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
        } else if (event.getView().getTitle()
                .startsWith(ChatColorTranslator.translate((String) Language.get("gui.commands.role_flags.title")))) {
            if (RoleFlagsGUI.cache.containsKey(player.getUniqueId().toString())) {
                RoleFlagsGUI.cache.remove(player.getUniqueId().toString());
            }
        }
    }

    private static ItemStack getItemFromFlagNatureFlags(String permission) {
		String[] splitted = permission.split("_");
		List<String> splittedlist = new ArrayList<>();

		for (String split : splitted) {
			splittedlist.add(split);
		}

		String itemType = (String) getFromLanguageNatureFlags("__FLAGS_REPEAT_STYLE_ITEMS_CONFIG__." + splittedlist.get(1));
		ItemStack item = itemType.startsWith("TEXTURE-") ? CustomHeadTexture.get(Arrays.asList(itemType.split("-")).get(1)) : new ItemStack(Material.getMaterial(itemType) == null ? Material.STRUCTURE_VOID : Material.getMaterial(itemType));

		return item;
	}

    private static Object getFromLanguageNatureFlags(String path) {
        return Language.get("gui.commands.nature_flags." + path);
    }

    private static ItemStack getItemFromPermissionNameRoleFlags(String permission) {
		String[] splitted = permission.split("_");
		List<String> splittedlist = new ArrayList<>();

		for (String split : splitted) {
			splittedlist.add(split);
		}

		String itemType = (String) getFromLanguageRoleFlags("__FLAGS_REPEAT_STYLE_ITEMS_CONFIG__." + splittedlist.get(1));
		ItemStack item = itemType.startsWith("TEXTURE-") ? CustomHeadTexture.get(Arrays.asList(itemType.split("-")).get(1)) : new ItemStack(Material.getMaterial(itemType) == null ? Material.STRUCTURE_VOID : Material.getMaterial(itemType));

		return item;
	}

    private static Object getFromLanguageRoleFlags(String path) {
        return Language.get("gui.commands.role_flags." + path);
    }
}
