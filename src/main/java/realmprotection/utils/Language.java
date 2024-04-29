package realmprotection.utils;

import realmprotection.RealmProtection;
import realmprotection.managers.LanguageLoader;

public class Language {
    public static RealmProtection plugin = RealmProtection.getPlugin(RealmProtection.class);

    public static Object get(String path){
        return LanguageLoader.loaded.get(path);
    }

    public static String getCommand(String path) {
        String prefix = "";
        String message = (String) get("commands." + path);

        if ((Boolean) get("general.prefix.enabled") && (Boolean) get("commands.__ALLOW_PREFIX__")) {
            prefix += (String) get("general.prefix.value");
        }

        return ChatColorTranslator.translate(prefix + message);
    }
}
