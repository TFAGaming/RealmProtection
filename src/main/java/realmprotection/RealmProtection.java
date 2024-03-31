package realmprotection;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Logger;

import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import net.milkbowl.vault.economy.Economy;
import realmprotection.commands.LandsCommand;
import realmprotection.database.Database;
import realmprotection.events.ChunksProtection;
import realmprotection.events.GUIListener;
import realmprotection.managers.ChunksManager;
import realmprotection.managers.LandsManager;
import realmprotection.utils.ColoredString;

public class RealmProtection extends JavaPlugin implements Listener {
    public static final Logger logger = Logger.getLogger("RealmProtector");
    public static final String ANSI_COLOR_RESET = "\u001B[0m";
    public static final String ANSI_COLOR_GREEN = "\u001B[32m";
    public static final String ANSI_COLOR_RED = "\u001B[31m";

    public static Economy vaultapi_economy = null;

    private static Set<UUID> cooldownPlayers = new HashSet<>();

    public static Database database;

    @Override
    public void onEnable() {
        logger.info(ANSI_COLOR_GREEN + "RealmProtector: The plugin has been enabled." + ANSI_COLOR_RESET);

        saveDefaultConfig();

        try {
            if (!getDataFolder().exists()) {
                getDataFolder().mkdirs();
            }

            RealmProtection.database = new Database(
                    getDataFolder().getAbsolutePath() + getConfig().getString("database.path"));

            RealmProtection.database.initialize();
        } catch (SQLException error) {
            error.printStackTrace();
            logger.severe(ANSI_COLOR_RED + "RealmProtector: Failed to connect to the database." + ANSI_COLOR_RESET);
        }

        if (getConfig().getBoolean("lands.plugins.vaultapi_economy") == true) {
            if (!setupEconomy()) {
                logger.severe(ANSI_COLOR_RED
                        + "RealmProtector: Unable to load the economy API from Vault."
                        + ANSI_COLOR_RESET);
            } else {
                logger.info(ANSI_COLOR_GREEN
                        + "RealmProtector: Vault API economy has been enabled!"
                        + ANSI_COLOR_RESET);
            }
        }

        getServer().getPluginManager().registerEvents(new ChunksProtection(), this);
        getServer().getPluginManager().registerEvents(new GUIListener(), this);

        getCommand("lands").setExecutor(new LandsCommand());
        getCommand("land").setExecutor(new LandsCommand());
    }

    @Override
    public void onDisable() {
        logger.info(ANSI_COLOR_GREEN + "RealmProtector: The plugin has been disabled." + ANSI_COLOR_RESET);

        try {
            RealmProtection.database.closeConnection();

            logger.info(ANSI_COLOR_GREEN + "RealmProtector: Successfully closed the database." + ANSI_COLOR_RESET);
        } catch (SQLException error) {
            error.printStackTrace();
            logger.severe(ANSI_COLOR_RED + "RealmProtector: Failed to close the database." + ANSI_COLOR_RESET);
        }
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }

        RegisteredServiceProvider<Economy> registerer = getServer().getServicesManager().getRegistration(Economy.class);

        if (registerer == null) {
            return false;
        }

        vaultapi_economy = registerer.getProvider();

        return vaultapi_economy != null;
    }

    public static Economy getEconomy() {
        return vaultapi_economy;
    }

    public static void _sendMessageWithTimeout(Player player, String permission) {
        UUID playerId = player.getUniqueId();
        if (!cooldownPlayers.contains(playerId)) {
            String message = _getConfigurationStringMessage(permission, player);

            player.sendMessage(ColoredString.translate(message));

            cooldownPlayers.add(playerId);

            player.getServer().getScheduler().runTaskLater(RealmProtection.getPlugin(RealmProtection.class),
                    () -> cooldownPlayers.remove(playerId), 60L);
        }
    }

    private static String _getConfigurationStringMessage(String permission, Player player) {
        RealmProtection plugin = RealmProtection.getPlugin(RealmProtection.class);

        String final_string = "";

        String land_id = ChunksManager.getChunkDetail(player.getLocation().getChunk(), "land_id");
        String land_name = LandsManager.getLandDetailById(new Integer(land_id), "land_name");

        String message = plugin.getConfig().getString("messages.permissions.messages." + permission);

        if (plugin.getConfig().getBoolean("messages.permissions.prefix.toggle")) {
            final_string += plugin.getConfig().getString("messages.permissions.prefix.string");
        }

        final_string += message;

        if (plugin.getConfig().getBoolean("messages.permissions.suffix.toggle")) {
            final_string += plugin.getConfig().getString("messages.permissions.suffix.string");
        }

        return final_string.replace("%land_name%", land_name).replace("%flag%", permission);
    }
}
