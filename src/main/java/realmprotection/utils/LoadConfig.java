package realmprotection.utils;

import java.util.List;

import realmprotection.RealmProtection;

public class LoadConfig {
    public static String commandString(String path) {
        RealmProtection plugin = RealmProtection.getPlugin(RealmProtection.class);

        String final_string = "";
        String message = plugin.getConfig().getString("messages.commands." + path);

        if (plugin.getConfig().getBoolean("messages.prefix.toggle")) {
            final_string += plugin.getConfig().getString("messages.prefix.string");
        }

        final_string += message;

        return ColoredString.translate(final_string);
    }

    public static String commandStringWithoutPrefix(String path) {
        RealmProtection plugin = RealmProtection.getPlugin(RealmProtection.class);

        return plugin.getConfig().getString("messages.commands." + path);
    }

    public static List<String> commandStringList(String path) {
        RealmProtection plugin = RealmProtection.getPlugin(RealmProtection.class);

        return plugin.getConfig().getStringList("messages.commands." + path);
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

    public static List<String> generalStringList(String path) {
        RealmProtection plugin = RealmProtection.getPlugin(RealmProtection.class);

        return plugin.getConfig().getStringList("messages.general." + path);
    }

    public static List<Boolean> landRolesDefaultBooleanList(String path) {
        RealmProtection plugin = RealmProtection.getPlugin(RealmProtection.class);

        return plugin.getConfig().getBooleanList("land_roles.default." + path);
    }

    public static String landRolesDefaultString(String path) {
        RealmProtection plugin = RealmProtection.getPlugin(RealmProtection.class);

        return plugin.getConfig().getString("land_roles.default." + path);
    }

    public static List<String> landRolesDefaultStringList(String path) {
        RealmProtection plugin = RealmProtection.getPlugin(RealmProtection.class);

        return plugin.getConfig().getStringList("land_roles.default." + path);
    }

    public static List<String> landsStringList(String path) {
        RealmProtection plugin = RealmProtection.getPlugin(RealmProtection.class);

        return plugin.getConfig().getStringList("lands." + path);
    }

    public static boolean isVaultPluginLoaded() {
        return RealmProtection.vaultapi_economy != null ? true : false;
    }
}
