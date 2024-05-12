package realmprotection.gui;

import java.util.ArrayList;
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

import realmprotection.managers.LandsManager;
import realmprotection.managers.RolesManager;
import realmprotection.utils.ChatColorTranslator;
import realmprotection.utils.CustomHeadTexture;
import realmprotection.utils.Language;

public class RoleFlagsGUI {
	public static Map<String, String> cache = new HashMap<>();

	public static void create(Player player, String role_name) {
		String land_id = LandsManager.getLandDetail(player.getUniqueId().toString(), "id");

		Inventory inventory = Bukkit.createInventory(player, 9 * 6,
				ChatColorTranslator.translate((String) getFromLanguage("title")));

		List<List<Object>> allflags = RolesManager.listEnabledAndDisabledFlagsForRole(new Integer(land_id),
				role_name);

		for (List<Object> flag : allflags) {
			ItemStack flagButton = getItemFromPermissionName("permissions_" + flag.get(0));

			ItemMeta flagButtonMeta = flagButton.getItemMeta();
			flagButtonMeta.setDisplayName(ChatColorTranslator
					.translate(((String) getFromLanguage("content.__FLAGS_REPEAT_STYLE__.displayname"))
							.replace("%flag%", "" + flag.get(0)).replace("%displayname%",
									(String) Language.get("flags.role_flags.displayname." + flag.get(0)))));

			ArrayList<String> flagButtonLore = new ArrayList<>();
			@SuppressWarnings("unchecked")
			List<String> roleflagslore = (List<String>) getFromLanguage("content.__FLAGS_REPEAT_STYLE__.lore");

			for (String lore : roleflagslore) {
				if ((boolean) flag.get(1) == true) {
					flagButtonLore.add(ChatColorTranslator
							.translate(lore
									.replace("%description%",
											"" + (String) Language.get("flags.role_flags.description." + flag.get(0)))
									.replace("%value%", (String) Language.get("general.flags.enabled"))));
				} else {
					flagButtonLore.add(ChatColorTranslator
							.translate(lore
									.replace("%description%",
											"" + (String) Language.get("flags.role_flags.description." + flag.get(0)))
									.replace("%value%", (String) Language.get("general.flags.disabled"))));
				}
			}

			flagButtonMeta.setLore(flagButtonLore);
			flagButton.setItemMeta(flagButtonMeta);

			inventory.addItem(flagButton);
		}

		ItemStack roleNameButton = new ItemStack(
				Material.getMaterial((String) getFromLanguage("content.role_name.type")));
		ItemMeta roleNameButtonMeta = roleNameButton.getItemMeta();
		roleNameButtonMeta.setDisplayName(ChatColorTranslator.translate(
				((String) getFromLanguage("content.role_name.displayname")).replace("%role%", role_name)));
		roleNameButton.setItemMeta(roleNameButtonMeta);

		String closeButtonType = (String) Language.get("general.guis.close_button.item.type");
		ItemStack closeButton = closeButtonType.startsWith("TEXTURE-")
				? CustomHeadTexture.get(Arrays.asList(closeButtonType.split("-")).get(1))
				: new ItemStack(Material.getMaterial(closeButtonType));
				
		ItemMeta closeButtonMeta = closeButton.getItemMeta();
		closeButtonMeta.setDisplayName(
				ChatColorTranslator.translate((String) Language.get("general.guis.close_button.item.displayname")));
		closeButton.setItemMeta(closeButtonMeta);

		inventory.setItem(45, roleNameButton);
		if ((Boolean) Language.get("general.guis.close_button.enabled")) {
			inventory.setItem(53, closeButton);
		}

		player.openInventory(inventory);
		
		cache.put(player.getUniqueId().toString(), role_name);
	}

	private static ItemStack getItemFromPermissionName(String permission) {
		String[] splitted = permission.split("_");
		List<String> splittedlist = new ArrayList<>();

		for (String split : splitted) {
			splittedlist.add(split);
		}

		String itemType = (String) getFromLanguage("__FLAGS_REPEAT_STYLE_ITEMS_CONFIG__." + splittedlist.get(1));
		ItemStack item = itemType.startsWith("TEXTURE-") ? CustomHeadTexture.get(Arrays.asList(itemType.split("-")).get(1)) : new ItemStack(Material.getMaterial(itemType) == null ? Material.STRUCTURE_VOID : Material.getMaterial(itemType));

		return item;
	}

	private static Object getFromLanguage(String path) {
		return Language.get("gui.commands.role_flags." + path);
	}
}
