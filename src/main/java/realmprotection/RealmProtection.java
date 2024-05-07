package realmprotection;

import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Logger;

import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import realmprotection.commands.LandsCommand;
import realmprotection.database.Database;
import realmprotection.events.ChunksProtection;
import realmprotection.events.GUIListener;
import realmprotection.managers.LanguageLoader;
import realmprotection.utils.LuckPermsAPI;
import realmprotection.utils.VaultAPIEconomy;

public class RealmProtection extends JavaPlugin implements Listener {
    public static final Logger logger = Logger.getLogger("RealmProtection");

    public static Database database;
    public static LanguageLoader language;

    @Override
    public void onEnable() {
        logger.info("[RealmProtection] The plugin has been enabled, getting everything ready...");

        saveDefaultConfig();

        try {
            LanguageLoader languageLoader = new LanguageLoader(this);

            RealmProtection.language = languageLoader;
        } catch (IOException error) {
            logger.severe("[RealmProtection] Failed to load language file.");

            error.printStackTrace();

            disablePlugin();

            return;
        }

        try {
            if (!getDataFolder().exists()) {
                getDataFolder().mkdirs();
            }

            RealmProtection.database = new Database(
                    getDataFolder().getAbsolutePath() + getConfig().getString("database.path"));

            RealmProtection.database.initialize();
        } catch (SQLException error) {
            logger.severe("[RealmProtection] Failed to connect to the database.");

            error.printStackTrace();

            disablePlugin();

            return;
        }

        if (!VaultAPIEconomy.setupVault()) {
            logger.severe("[RealmProtection] Unable to load Vault API.");

            disablePlugin();

            return;
        } else {
            logger.info("[RealmProtection] Vault API has been successfully loaded.");
        }

        if (!LuckPermsAPI.setupLuckperms()) {
            logger.severe("[RealmProtection] Unable to load LuckPerms API.");

            disablePlugin();

            return;
        } else {
            logger.info("[RealmProtection] LuckPerms API has been successfully loaded.");
        }

        getServer().getPluginManager().registerEvents(new ChunksProtection(), this);
        getServer().getPluginManager().registerEvents(new GUIListener(), this);

        getCommand("lands").setExecutor(new LandsCommand());
        getCommand("land").setExecutor(new LandsCommand());

        logger.info("[RealmProtection] The plugin is now fully functional!");
    }

    @Override
    public void onDisable() {
        logger.info("[RealmProtection] The plugin has been disabled.");

        try {
            RealmProtection.database.closeConnection();

            logger.info("[RealmProtection] Successfully closed the connection to database.");
        } catch (SQLException error) {
            logger.severe("[RealmProtection] Failed to close the connection to database.");

            error.printStackTrace();
        }
    }

    private void disablePlugin() {
        getServer().getPluginManager().disablePlugin(this);
    }
}
