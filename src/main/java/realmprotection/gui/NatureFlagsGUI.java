package realmprotection.gui;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import realmprotection.managers.LandsManager;
import realmprotection.utils.ChatColorTranslator;
import realmprotection.utils.Language;

public class NatureFlagsGUI {
	public static void create(Player player) {
		String land_id = LandsManager.getLandDetail(player.getName(), "id");
		String land_name = LandsManager.getLandDetailById(new Integer(land_id), "land_name");

		Inventory inventory = Bukkit.createInventory(player, 9 * 4,
				ChatColorTranslator.translate(
						ChatColorTranslator
								.translate((String) getFromLanguage("title"))));

		List<List<Object>> allflags = LandsManager.listEnabledAndDisabledFlagsForLand(new Integer(land_id));

		for (List<Object> flag : allflags) {
			ItemStack flagButton = new ItemStack(
					getMaterialItemFromFlagName("nature_" + flag.get(0)));

			ItemMeta flagButtonMeta = flagButton.getItemMeta();
			flagButtonMeta.setDisplayName(ChatColorTranslator
					.translate(((String) getFromLanguage("content.__FLAGS_REPEAT_STYLE__.displayname"))
							.replace("%flag%", "" + flag.get(0)).replace("%displayname%",
									(String) Language.get("flags.nature_flags.displayname." + flag.get(0)))));

			ArrayList<String> flagButtonLore = new ArrayList<>();
			@SuppressWarnings("unchecked")
			List<String> natureflagslore = (List<String>) getFromLanguage("content.__FLAGS_REPEAT_STYLE__.lore");

			for (String lore : natureflagslore) {
				if ((boolean) flag.get(1) == true) {
					flagButtonLore.add(ChatColorTranslator
							.translate(lore
									.replace("%description%",
											"" + (String) Language.get("flags.nature_flags.description." + flag.get(0)))
									.replace("%value%", (String) Language.get("general.flags.enabled"))));
				} else {
					flagButtonLore.add(ChatColorTranslator
							.translate(lore
									.replace("%description%",
											"" + (String) Language.get("flags.nature_flags.description." + flag.get(0)))
									.replace("%value%", (String) Language.get("general.flags.disabled"))));
				}
			}

			flagButtonMeta.setLore(flagButtonLore);
			flagButton.setItemMeta(flagButtonMeta);

			inventory.addItem(flagButton);
		}

		ItemStack landNameButton = new ItemStack(
				Material.getMaterial((String) getFromLanguage("content.land_name.type")));
		ItemMeta landNameButtonMeta = landNameButton.getItemMeta();
		landNameButtonMeta.setDisplayName(ChatColorTranslator
				.translate(((String) getFromLanguage("content.land_name.displayname"))
						.replace("%land_name%", land_name)));
		landNameButton.setItemMeta(landNameButtonMeta);

		ItemStack closeButton = new ItemStack(
				Material.getMaterial((String) Language.get("general.guis.close_button.item.type")));
		ItemMeta closeButtonMeta = closeButton.getItemMeta();
		closeButtonMeta.setDisplayName(
				ChatColorTranslator.translate((String) Language.get("general.guis.close_button.item.displayname")));
		closeButton.setItemMeta(closeButtonMeta);

		inventory.setItem(27, landNameButton);
		if ((Boolean) Language.get("general.guis.close_button.enabled")) {
			inventory.setItem(35, closeButton);
		}

		player.openInventory(inventory);
	}

	private static Material getMaterialItemFromFlagName(String flag) {
		String[] splitted = flag.split("_");
		List<String> splittedlist = new ArrayList<>();

		for (String split : splitted) {
			splittedlist.add(split);
		}

		Material material = Material.getMaterial((String) getFromLanguage("__FLAGS_REPEAT_STYLE_ITEMS_CONFIG__." + splittedlist.get(1)));

		if (material != null) {
			return material;
		} else {
			return Material.STRUCTURE_VOID;
		}
	}

	private static Object getFromLanguage(String path) {
		return Language.get("gui.commands.nature_flags." + path);
	}
}
