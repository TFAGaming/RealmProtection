package xtremelands.database;

import java.sql.*;

import xtremelands.Plugin;
import xtremelands.managers.ChunksManager;

public class Database {
    private final String DATABASE_PATH;

    public Database(String database_path) {
        this.DATABASE_PATH = database_path;
    }

    private Connection connection;

    public Connection getConnection() throws SQLException {
        if (connection != null) {
            return connection;
        }

        Connection connection = DriverManager.getConnection("jdbc:sqlite:" + this.DATABASE_PATH);
        this.connection = connection;

        Plugin.logger.info(Plugin.ANSI_COLOR_GREEN + "Connected to the database!" + Plugin.ANSI_COLOR_RESET);

        return connection;
    }

    public void closeConnection() throws SQLException {
        if (this.connection != null && !connection.isClosed()) {
            this.connection.close();
        }
    }

    public void init() throws SQLException {
        Statement statement1 = getConnection().createStatement();

        statement1.execute("CREATE TABLE IF NOT EXISTS lands (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "land_name TEXT NOT NULL, " +
                "owner_name TEXT NOT NULL, " +
                "location_x REAL NOT NULL, " +
                "location_y REAL NOT NULL, " +
                "location_z REAL NOT NULL, " +
                "location_world TEXT NOT NULL, " +
                "created_at BIGINT NOT NULL, " + 
                "nature_hostilemobsspawn BOOLEAN NOT NULL, " + 
                "nature_passivemobsspawn BOOLEAN NOT NULL, " + 
                "nature_leavesdecay BOOLEAN NOT NULL, " + 
                "nature_firespread BOOLEAN NOT NULL, " + 
                "nature_liquidflow BOOLEAN NOT NULL, " + 
                "nature_tntblockdamage BOOLEAN NOT NULL, " + 
                "nature_respawnanchorblockdamage BOOLEAN NOT NULL, " + 
                "nature_pistonsfromwilderness BOOLEAN NOT NULL, " + 
                "nature_plantgrowth BOOLEAN NOT NULL)"
                );
        statement1.close();

        Statement statement2 = getConnection().createStatement();

        statement2.execute("CREATE TABLE IF NOT EXISTS claimed_chunks (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "chunk_x INTEGER NOT NULL," +
                "chunk_z INTEGER NOT NULL," +
                "chunk_world TEXT NOT NULL, " +
                "land_id INTEGER NOT NULL," +
                "created_at BIGINT NOT NULL)");
        statement2.close();

        Statement statement3 = getConnection().createStatement();

        statement3.execute("CREATE TABLE IF NOT EXISTS land_roles (" +
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
                "permissions_fencegates BOOLEAN NOT NULL," +
                "permissions_buttons BOOLEAN NOT NULL," +
                "permissions_levers BOOLEAN NOT NULL," +
                "permissions_pressureplates BOOLEAN NOT NULL," +
                "permissions_bells BOOLEAN NOT NULL," +
                "permissions_tripwires BOOLEAN NOT NULL," +
                "permissions_armorstands BOOLEAN NOT NULL," +
                "permissions_teleporttospawn BOOLEAN NOT NULL," +
                "permissions_damagehostilemobs BOOLEAN NOT NULL," +
                "permissions_damagepassivemobs BOOLEAN NOT NULL," +
                "permissions_pvp BOOLEAN NOT NULL," +
                "permissions_usecauldron BOOLEAN NOT NULL," +
                "permissions_pickupitems BOOLEAN NOT NULL," +
                "permissions_useanvil BOOLEAN NOT NULL, " +
                "permissions_createfire BOOLEAN NOT NULL)");
        statement3.close();

        Statement statement4 = getConnection().createStatement();

        statement4.execute("CREATE TABLE IF NOT EXISTS land_members (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "land_id INTEGER NOT NULL," +
                "member_name TEXT NOT NULL," +
                "role TEXT NOT NULL)");
        statement4.close();

        ChunksManager.cacheSetAllClaimedChunks();
    }
}
