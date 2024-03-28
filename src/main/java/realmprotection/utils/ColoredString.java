package realmprotection.utils;

import org.bukkit.ChatColor;

public class ColoredString {
    public static String translate(String string) {
        return ChatColor.translateAlternateColorCodes('&', string);
    }
}
