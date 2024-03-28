package realmprotection.utils;

import java.util.List;

import realmprotection.RealmProtection;

public class LoadConfigString {
    public static String load(String path) {
        RealmProtection plugin = RealmProtection.getPlugin(RealmProtection.class);

        String final_string = "";
        String message = plugin.getConfig().getString("messages.commands." + path);

        if (plugin.getConfig().getBoolean("messages.prefix.toggle")) {
            final_string += plugin.getConfig().getString("messages.prefix.string");
        }

        final_string += message;

        return ColoredString.translate(final_string);
    }

    public static String guiString(String path) {
        RealmProtection plugin = RealmProtection.getPlugin(RealmProtection.class);

        return plugin.getConfig().getString("messages.gui." + path);
    }

    public static List<String> guiStringList(String path) {
        RealmProtection plugin = RealmProtection.getPlugin(RealmProtection.class);

        return plugin.getConfig().getStringList("messages.gui." + path);
    }

    public static String generalString(String path) {
        RealmProtection plugin = RealmProtection.getPlugin(RealmProtection.class);

        return plugin.getConfig().getString("messages.general." + path);
    }

    public static boolean generalBoolean(String path) {
        RealmProtection plugin = RealmProtection.getPlugin(RealmProtection.class);

        return plugin.getConfig().getBoolean("messages.general." + path);
    }
}
