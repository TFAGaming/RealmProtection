package xtremelands.utils;

import xtremelands.Plugin;

public class LoadConfigString {
    public static String load(String path) {
        Plugin plugin = Plugin.getPlugin(Plugin.class);

        String final_string = "";
        String message = plugin.getConfig().getString("messages.commands." + path);

        if (plugin.getConfig().getBoolean("messages.prefix.toggle")) {
            final_string += plugin.getConfig().getString("messages.prefix.string");
        }

        final_string += message;

        return ColoredString.translate(final_string);
    }
}
