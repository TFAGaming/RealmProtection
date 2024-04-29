package realmprotection;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Logger;

import org.bukkit.Chunk;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import realmprotection.commands.LandsCommand;
import realmprotection.database.Database;
import realmprotection.events.ChunksProtection;
import realmprotection.events.GUIListener;
import realmprotection.managers.ChunksManager;
import realmprotection.managers.LandsManager;
import realmprotection.managers.LanguageLoader;
import realmprotection.utils.ChatColorTranslator;
import realmprotection.utils.Language;
import realmprotection.utils.LuckPermsAPI;
import realmprotection.utils.VaultAPIEconomy;

public class RealmProtection extends JavaPlugin implements Listener {
    public static final Logger logger = Logger.getLogger("RealmProtection");

    private static Set<UUID> cooldownPlayers = new HashSet<>();

    public static Database database;
    public static LanguageLoader language;

    @Override
    public void onEnable() {
        logger.info("[RealmProtection] The plugin has been enabled.");

        saveDefaultConfig();

        try {
            LanguageLoader languageLoader = new LanguageLoader(this);
            
            RealmProtection.language = languageLoader;
        } catch (IOException error) {
            logger.severe("[RealmProtection] Failed to load language file.");

            error.printStackTrace();
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
        }

        if (!VaultAPIEconomy.setupVault()) {
            logger.severe("[RealmProtection] Unable to load Vault API, plugin now is disabled.");

            getServer().getPluginManager().disablePlugin(this);
        } else {
            logger.info("[RealmProtection] Vault API is loaded.");
        }

        if (!LuckPermsAPI.setupLuckperms()) {
            logger.severe("[RealmProtection] Unable to load LuckPerms API, plugin is now disabled.");

            getServer().getPluginManager().disablePlugin(this);
        } else {
            logger.info("[RealmProtection] LuckPerms API is loaded.");
        }

        getServer().getPluginManager().registerEvents(new ChunksProtection(), this);
        getServer().getPluginManager().registerEvents(new GUIListener(), this);

        getCommand("lands").setExecutor(new LandsCommand());
        getCommand("land").setExecutor(new LandsCommand());
    }

    @Override
    public void onDisable() {
        logger.info("[RealmProtection] The plugin has been disabled.");

        try {
            RealmProtection.database.closeConnection();

            logger.info("[RealmProtection] Successfully closed the database.");
        } catch (SQLException error) {
            logger.severe("[RealmProtection] Failed to close the database.");

            error.printStackTrace();
        }
    }

    public static void _sendMessageWithTimeout(Player player, String permission, Chunk claimed_chunk) {
        UUID playerId = player.getUniqueId();
        if (!cooldownPlayers.contains(playerId)) {
            String message = _getConfigurationStringMessage(permission, player, claimed_chunk);

            player.sendMessage(ChatColorTranslator.translate(message));

            cooldownPlayers.add(playerId);

            player.getServer().getScheduler().runTaskLater(RealmProtection.getPlugin(RealmProtection.class),
                    () -> cooldownPlayers.remove(playerId), 60L);
        }
    }

    private static String _getConfigurationStringMessage(String permission, Player player, Chunk claimed_chunk) {
        String final_string = "";

        String land_id = ChunksManager.getChunkDetail(claimed_chunk, "land_id");
        String land_name = LandsManager.getLandDetailById(new Integer(land_id), "land_name");

        String message = (String) Language.get("permissions.lands." + permission);

        if ((Boolean) Language.get("permissions.config.prefix.enabled")) {
            final_string += (String) Language.get("permissions.config.prefix.value");
        }

        final_string += message;

        if ((Boolean) Language.get("permissions.config.suffix.enabled")) {
            final_string += (String) Language.get("permissions.config.suffix.value");
        }

        return final_string.replace("%land_name%", land_name).replace("%flag%", permission);
    }
}
