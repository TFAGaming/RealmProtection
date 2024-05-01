package realmprotection.database;

import java.sql.*;

import realmprotection.RealmProtection;
import realmprotection.managers.ChunksManager;
import realmprotection.managers.LandBansManager;
import realmprotection.managers.LandInvitesManager;
import realmprotection.managers.LandMembersManager;
import realmprotection.managers.LandsManager;
import realmprotection.managers.RolesManager;

public class Database {
    public static RealmProtection plugin = RealmProtection.getPlugin(RealmProtection.class);

    private final String DATABASE_PATH;
    private Connection connection;

    public Database(String database_path) {
        this.DATABASE_PATH = database_path;
    }
    
    public Connection getConnection() throws SQLException {
        if (connection != null) {
            return connection;
        }

        if (plugin.getConfig().getString("database.provider").equalsIgnoreCase("sqlite")) {
            Connection connection = DriverManager.getConnection("jdbc:sqlite:" + this.DATABASE_PATH);
            this.connection = connection;

            RealmProtection.logger.info("[RealmProtection] Successfully connected to the database! (SQLite)");

            return connection;
        } else {
            throw new Error("Invalid database provider in config.yml.");
        }
    }

    public void closeConnection() throws SQLException {
        if (this.connection != null && !connection.isClosed()) {
            this.connection.close();
        }
    }

    public void initialize() throws SQLException {
        statementExecute("CREATE TABLE IF NOT EXISTS lands (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "land_name TEXT NOT NULL, " +
                "owner_name TEXT NOT NULL, " +
                "location_x REAL NOT NULL, " +
                "location_y REAL NOT NULL, " +
                "location_z REAL NOT NULL, " +
                "location_world TEXT NOT NULL, " +
                "location_yaw REAL NOT NULL, " +
                "created_at BIGINT NOT NULL, " +
                "balance REAL NOT NULL, " +
                "nature_hostilemobsspawn BOOLEAN NOT NULL, " +
                "nature_passivemobsspawn BOOLEAN NOT NULL, " +
                "nature_leavesdecay BOOLEAN NOT NULL, " +
                "nature_firespread BOOLEAN NOT NULL, " +
                "nature_liquidflow BOOLEAN NOT NULL, " +
                "nature_tntblockdamage BOOLEAN NOT NULL, " +
                "nature_respawnanchorblockdamage BOOLEAN NOT NULL, " +
                "nature_pistonsfromwilderness BOOLEAN NOT NULL, " +
                "nature_dispensersfromwilderness BOOLEAN NOT NULL, " +
                "nature_plantgrowth BOOLEAN NOT NULL)");

        statementExecute("CREATE TABLE IF NOT EXISTS claimed_chunks (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "chunk_x INTEGER NOT NULL," +
                "chunk_z INTEGER NOT NULL," +
                "chunk_world TEXT NOT NULL, " +
                "land_id INTEGER NOT NULL," +
                "created_at BIGINT NOT NULL)");

        statementExecute("CREATE TABLE IF NOT EXISTS land_roles (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "land_id INTEGER NOT NULL," +
                "role_name TEXT NOT NULL," +
                "permissions_breakblocks BOOLEAN NOT NULL," +
                "permissions_placeblocks BOOLEAN NOT NULL," +
                "permissions_containers BOOLEAN NOT NULL," +
                "permissions_redstone BOOLEAN NOT NULL," +
                "permissions_doors BOOLEAN NOT NULL," +
                "permissions_trapdoors BOOLEAN NOT NULL," +
                "permissions_editsigns BOOLEAN NOT NULL," +
                "permissions_emptybuckets BOOLEAN NOT NULL," +
                "permissions_fillbuckets BOOLEAN NOT NULL," +
                "permissions_harvestcrops BOOLEAN NOT NULL," +
                "permissions_frostwalker BOOLEAN NOT NULL," +
                "permissions_shearentities BOOLEAN NOT NULL," +
                "permissions_itemframes BOOLEAN NOT NULL," +
                "permissions_generalinteractions BOOLEAN NOT NULL," +
                "permissions_fencegates BOOLEAN NOT NULL," +
                "permissions_buttons BOOLEAN NOT NULL," +
                "permissions_levers BOOLEAN NOT NULL," +
                "permissions_pressureplates BOOLEAN NOT NULL," +
                "permissions_bells BOOLEAN NOT NULL," +
                "permissions_tripwires BOOLEAN NOT NULL," +
                "permissions_armorstands BOOLEAN NOT NULL," +
                "permissions_dyemobs BOOLEAN NOT NULL," +
                "permissions_renamemobs BOOLEAN NOT NULL," +
                "permissions_leashmobs BOOLEAN NOT NULL," +
                "permissions_tradewithvillagers BOOLEAN NOT NULL," +
                "permissions_teleporttospawn BOOLEAN NOT NULL," +
                "permissions_throwenderpearls BOOLEAN NOT NULL," +
                "permissions_throwpotions BOOLEAN NOT NULL," +
                "permissions_damagehostilemobs BOOLEAN NOT NULL," +
                "permissions_damagepassivemobs BOOLEAN NOT NULL," +
                "permissions_pvp BOOLEAN NOT NULL," +
                "permissions_usecauldron BOOLEAN NOT NULL," +
                "permissions_pickupitems BOOLEAN NOT NULL," +
                "permissions_useanvil BOOLEAN NOT NULL, " +
                "permissions_createfire BOOLEAN NOT NULL, " +
                "permissions_usevehicles BOOLEAN NOT NULL)");

        statementExecute("CREATE TABLE IF NOT EXISTS land_members (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "land_id INTEGER NOT NULL," +
                "member_name TEXT NOT NULL," +
                "role_id INTEGER NOT NULL)");

        statementExecute("CREATE TABLE IF NOT EXISTS land_bans (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "land_id INTEGER NOT NULL," +
                "player_name INTEGER NOT NULL," +
                "reason TEXT NOT NULL)");

        statementExecute("CREATE TABLE IF NOT EXISTS land_invites (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "land_id INTEGER NOT NULL," +
                "inviter_name TEXT NOT NULL," +
                "player_name TEXT NOT NULL," +
                "role_id INTEGER NOT NULL)");

        ChunksManager.cacheUpdateAll();
        LandBansManager.cacheUpdateAll();
        LandMembersManager.cacheUpdateAll();
        LandInvitesManager.cacheUpdateAll();
        LandsManager.cacheUpdateAll();
        RolesManager.cacheUpdateAll();
    }

    public void statementExecute(String sql) throws SQLException {
        Statement statement = getConnection().createStatement();

        statement.execute(sql);
        statement.close();
    }
}
