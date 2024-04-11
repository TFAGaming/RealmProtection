package realmprotection.utils;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.user.User;
import realmprotection.RealmProtection;

public class LuckPermsAPI {
    public static LuckPerms luckpermsapi = null;

    public static boolean setupLuckperms() {
        RealmProtection plugin = RealmProtection.getPlugin(RealmProtection.class);

        if (plugin.getServer().getPluginManager().getPlugin("LuckPerms") == null) {
            return false;
        }

        RegisteredServiceProvider<LuckPerms> registerer = Bukkit.getServicesManager().getRegistration(LuckPerms.class);

        if (registerer == null) {
            return false;
        }

        luckpermsapi = registerer.getProvider();

        return luckpermsapi != null;
    }

    public static LuckPerms getLuckperms() {
        return luckpermsapi;
    }

    public static boolean isReady() {
        return luckpermsapi != null ? true : false;
    }

    public static User getUser(Player player) {
        return getLuckperms().getPlayerAdapter(Player.class).getUser(player);
    }

    public static String getPlayerGroup(Player player) {
        User user = getUser(player);

        return user.getPrimaryGroup();
    }
}
