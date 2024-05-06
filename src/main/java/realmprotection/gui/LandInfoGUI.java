package realmprotection.gui;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import realmprotection.managers.ChunksManager;
import realmprotection.managers.LandMembersManager;
import realmprotection.managers.LandsManager;
import realmprotection.utils.ChatColorTranslator;
import realmprotection.utils.Language;

public class LandInfoGUI {
	public static void create(Player player, String land_id) {
		Inventory inventory = Bukkit.createInventory(player, 9 * 3,
				ChatColorTranslator.translate((String) getFromLanguage("title")));

		ItemStack landInformationButton = new ItemStack(
				Material.getMaterial((String) getFromLanguage(("content.land_information.type"))));
		ItemStack landOwnerButton = new ItemStack(
				Material.getMaterial((String) getFromLanguage("content.land_owner.type")));
		ItemStack landMembersButton = new ItemStack(
				Material.getMaterial((String) getFromLanguage("content.land_members.type")));
		ItemStack landCloseButton = new ItemStack(
				Material.getMaterial((String) Language.get("general.guis.close_button.item.type")));

		ItemMeta landInformationButtonMeta = landInformationButton.getItemMeta();
		landInformationButtonMeta.setDisplayName(ChatColorTranslator.translate(
				((String) getFromLanguage("content.land_information.displayname"))
						.replace("%land_name%",
								LandsManager.getLandDetailById(
										new Integer(land_id),
										"land_name"))));

		ArrayList<String> landInformationButtonLore = new ArrayList<>();
		@SuppressWarnings("unchecked")
		List<String> loreConfigData = (List<String>) getFromLanguage("content.land_information.lore");

		DateFormat dateFormatter = new SimpleDateFormat((String) Language.get("general.date_format"));

		double land_balance = Double
				.parseDouble(LandsManager.getLandDetailById(new Integer(land_id), "balance"));

		for (String lore : loreConfigData) {
			landInformationButtonLore.add(ChatColorTranslator.translate(lore
					.replace("%land_id%", land_id)
					.replace("%chunks%",
							"" + ChunksManager.getChunksCountOfLand(
									new Integer(land_id)))
					.replace("%balance%",
							String.format("%.2f", land_balance))
					.replace("%created_at%",
							dateFormatter.format(
									new Date(new Long(LandsManager
											.getLandDetailById(
													new Integer(land_id),
													"created_at")))))));
		}

		landInformationButtonMeta.setLore(landInformationButtonLore);
		landInformationButton.setItemMeta(landInformationButtonMeta);

		ItemMeta landOwnerButtonMeta = landOwnerButton.getItemMeta();
		landOwnerButtonMeta.setDisplayName(ChatColorTranslator
				.translate(((String) getFromLanguage("content.land_owner.displayname")).replace(
						"%land_owner%",
						Bukkit.getOfflinePlayer(UUID.fromString(LandsManager.getLandDetailById(new Integer(land_id),
								"owner_uuid"))).getName())));
		landOwnerButton.setItemMeta(landOwnerButtonMeta);

		List<List<String>> membersdata = LandMembersManager.listAllMembersData(new Integer(land_id));

		ItemMeta landMembersButtonMeta = landMembersButton.getItemMeta();
		landMembersButtonMeta.setDisplayName(
				ChatColorTranslator.translate(((String) getFromLanguage("content.land_members.displayname"))
						.replace("%members_count%", "" + LandMembersManager
								.getMembersCountOfLand(new Integer(land_id)))));

		ArrayList<String> landMembersButtonLore = new ArrayList<>();

		for (List<String> memberdata : membersdata) {
			landMembersButtonLore
					.add(ChatColorTranslator
							.translate(((String) getFromLanguage("content.land_members.__LORE_REPEAT_STYLE__"))
									.replace("%role_name%",
											memberdata.get(1))
									.replace("%member_name%",
											memberdata.get(0))));
		}

		landMembersButtonMeta.setLore(landMembersButtonLore);
		landMembersButton.setItemMeta(landMembersButtonMeta);

		ItemMeta landCloseButtonMeta = landCloseButton.getItemMeta();
		landCloseButtonMeta.setDisplayName(ChatColorTranslator
				.translate((String) Language.get("general.guis.close_button.item.displayname")));
		landCloseButton.setItemMeta(landCloseButtonMeta);

		inventory.setItem(11, landInformationButton);
		inventory.setItem(13, landOwnerButton);
		inventory.setItem(15, landMembersButton);
		if ((Boolean) Language.get("general.guis.close_button.enabled")) {
			inventory.setItem(26, landCloseButton);
		}

		player.openInventory(inventory);
	}

	private static Object getFromLanguage(String path) {
		return Language.get("gui.commands.land_info." + path);
	}
}
