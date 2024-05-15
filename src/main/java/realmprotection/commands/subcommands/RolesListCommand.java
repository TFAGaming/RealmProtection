package realmprotection.commands.subcommands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import realmprotection.managers.LandsManager;
import realmprotection.managers.RolesManager;
import realmprotection.utils.ChatColorTranslator;
import realmprotection.utils.CustomHeadTexture;
import realmprotection.utils.Language;
import realmprotection.utils.PaginationGUI;

public class RolesListCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (!LandsManager.hasLand(player.getUniqueId().toString())) {
                player.sendMessage(Language.getCommand("roles.land_not_found"));
                return true;
            }

            String land_id = LandsManager.getLandDetail(player.getUniqueId().toString(), "id");

            List<List<Object>> roles = RolesManager.listAllRolesData(new Integer(land_id));

            roles.sort(Comparator.comparingInt((List<Object> list) -> (int) list.get(1)));

            int roles_count = roles.size();

            int total_pages = (int) Math.ceil((float) roles_count / 9.0);

            if (total_pages <= 0) {
                total_pages = 1;
            }

            PaginationGUI pagegui = new PaginationGUI(player, 3, ChatColorTranslator.translate((String) getFromLanguage("title")),
                    total_pages);

            List<List<ItemStack>> pages = new ArrayList<>();

            for (int index = 0; index < total_pages; index++) {
                List<ItemStack> page = new ArrayList<>();

                int startIndex = index * 9;
                int endIndex = Math.min(startIndex + 9, roles_count);

                for (int i = startIndex; i < endIndex; i++) {
                    List<Object> role = roles.get(i);
                    String role_name = (String) role.get(0);
                    int role_id = (int) role.get(1);

                    String roleButtonType = getItem();
                    ItemStack roleButton = roleButtonType.startsWith("TEXTURE-")
                            ? CustomHeadTexture.get(Arrays.asList(roleButtonType.split("-")).get(1))
                            : new ItemStack(Material.getMaterial(roleButtonType));

                    ItemMeta roleButtonMeta = roleButton.getItemMeta();
                    roleButtonMeta.setDisplayName(
                            ChatColorTranslator.translate(((String) getFromLanguage("content.__ITEM_DISPLAYNAME__"))
                                    .replace("%role_name%", role_name).replace("%role_id%", "" + role_id)));
                    roleButton.setItemMeta(roleButtonMeta);

                    page.add(roleButton);
                }

                pages.add(page);
            }

            for (int i = 0; i < pages.size(); i++) {
                pagegui.addPage(i, pages.get(i));
            }

            pagegui.openInventory(pagegui);

            return true;
        } else {
            return false;
        }
    }

    private static String getItem() {
        String role_texture = (String) getFromLanguage("content.__ITEM_TYPE__.__DEFAULT__");

        return role_texture;
    }

    private static Object getFromLanguage(String path) {
        return Language.get("gui.commands.roles_list." + path);
    }
}