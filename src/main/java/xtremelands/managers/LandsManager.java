package xtremelands.managers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.Lists;

import xtremelands.Plugin;

public class LandsManager {
    public static void createNewLand(String land_name, String player_name, String location_world, double location_x,
            double location_y, double location_z) {
        String sql = "INSERT INTO lands ( " +
                "land_name, " +
                "owner_name, " +
                "created_at, " +
                "location_world, " +
                "location_x, " +
                "location_y, " +
                "location_z, " +
                "nature_hostilemobsspawn, " +
                "nature_passivemobsspawn, " +
                "nature_leavesdecay, " +
                "nature_firespread, " +
                "nature_liquidflow, " +
                "nature_tntblockdamage, " +
                "nature_respawnanchorblockdamage, " +
                "nature_pistonsfromwilderness, " +
                "nature_plantgrowth " +
                ") VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

        try {
            Connection connection = Plugin.database.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);

            long timestamp = System.currentTimeMillis();

            statement.setString(1, land_name);
            statement.setString(2, player_name);
            statement.setLong(3, timestamp);
            statement.setString(4, location_world);
            statement.setDouble(5, location_x);
            statement.setDouble(6, location_y);
            statement.setDouble(7, location_z);
            statement.setBoolean(8, true); // Hostile mob spawn
            statement.setBoolean(9, true); // Passive mob spawn
            statement.setBoolean(10, true); // Leaves decay
            statement.setBoolean(11, false); // Fire spread
            statement.setBoolean(12, false); // Liquid flow
            statement.setBoolean(13, false); // TnT block damage
            statement.setBoolean(14, false); // Anchor block damage
            statement.setBoolean(15, false); // Pistons damage
            statement.setBoolean(16, true); // Plant growth

            statement.execute();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void deleteLand(Integer land_id) {
        String sql = "DELETE FROM lands WHERE id = ?";

        try {
            Connection connection = Plugin.database.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);

            statement.setInt(1, land_id);

            statement.execute();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static boolean hasLand(String player_name) {
        String sql = "SELECT COUNT (*) AS count FROM lands WHERE owner_name = ?";
        boolean exists = false;

        try {
            Connection connection = Plugin.database.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);

            statement.setString(1, player_name);

            ResultSet rs = statement.executeQuery();

            if (rs.next()) {
                int count = rs.getInt("count");
                exists = count > 0;
            }

            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return exists;
    }

    public static boolean landNameExist(String land_name) {
        String sql = "SELECT COUNT (*) AS count FROM lands WHERE land_name = ?";
        boolean exists = false;

        try {
            Connection connection = Plugin.database.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);

            statement.setString(1, land_name);

            ResultSet rs = statement.executeQuery();

            if (rs.next()) {
                int count = rs.getInt("count");
                exists = count > 0;
            }

            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return exists;
    }

    public static String getLandDetail(String player_name, String variable) {
        String sql = "SELECT * FROM lands WHERE owner_name = ?";

        try {
            Connection connection = Plugin.database.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);

            statement.setString(1, player_name);

            ResultSet result = statement.executeQuery();

            if (result.next()) {
                String string = result.getString(variable);

                return string;
            }

            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static String getLandDetailById(Integer land_id, String variable) {
        String sql = "SELECT * FROM lands WHERE id = ?";

        try {
            Connection connection = Plugin.database.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);

            statement.setInt(1, land_id);

            ResultSet result = statement.executeQuery();

            if (result.next()) {
                String string = result.getString(variable);

                return string;
            }

            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static String getLandDetailByLandName(String land_name, String variable) {
        String sql = "SELECT * FROM lands WHERE land_name = ?";

        try {
            Connection connection = Plugin.database.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);

            statement.setString(1, land_name);

            ResultSet result = statement.executeQuery();

            if (result.next()) {
                String string = result.getString(variable);

                return string;
            }

            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static List<String> listAllLandNames() {
        String sql = "SELECT * FROM lands";
        List<String> landnames = new ArrayList<>();

        try {
            Connection connection = Plugin.database.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);

            ResultSet result = statement.executeQuery();

            while (result.next()) {
                String string = result.getString("land_name");

                landnames.add(string);
            }

            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return landnames;
    }

    public static void updateSpawnLocation(Integer land_id, double location_x, double location_y, double location_z, String location_world) {
        String sql = "UPDATE lands SET location_x=" + location_x + ", location_y=" + location_y + ", location_z="
                + location_z + ", location_world = '" + location_world + "' WHERE id = ?";

        try {
            Connection connection = Plugin.database.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);

            statement.setInt(1, land_id);

            statement.execute();

            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return;
    }

    public static boolean getFlagValue(Integer land_id, String flag_name) {
        String sql = "SELECT * FROM lands WHERE id = ?";

        try {
            Connection connection = Plugin.database.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);

            statement.setInt(1, land_id);

            ResultSet result = statement.executeQuery();

            if (result.next()) {
                Boolean value = result.getBoolean("nature_" + flag_name);

                return value;
            }

            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public static void updateNatureFlagValue(Integer land_id, String flag, Boolean value) {
        String sql = "UPDATE lands SET nature_" + flag + "=" + value + " WHERE id = ?";

        try {
            Connection connection = Plugin.database.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);

            statement.setInt(1, land_id);

            statement.execute();

            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return;
    }

    public static List<List<Object>> listEnabledAndDisabledFlagsForLand(Integer land_id) {
        String sql = "SELECT * FROM lands WHERE id = ?";

        List<List<Object>> allflags = new ArrayList<>();

        try {
            Connection connection = Plugin.database.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);

            statement.setInt(1, land_id);

            ResultSet result = statement.executeQuery();

            while (result.next()) {
                allflags.add(Lists.newArrayList("hostilemobsspawn", result.getBoolean("nature_hostilemobsspawn")));
                allflags.add(Lists.newArrayList("passivemobsspawn", result.getBoolean("nature_passivemobsspawn"))); 
                allflags.add(Lists.newArrayList("leavesdecay", result.getBoolean("nature_leavesdecay")));
                allflags.add(Lists.newArrayList("firespread", result.getBoolean("nature_firespread"))); 
                allflags.add(Lists.newArrayList("liquidflow", result.getBoolean("nature_liquidflow"))); 
                allflags.add(Lists.newArrayList("tntblockdamage", result.getBoolean("nature_tntblockdamage"))); 
                allflags.add(Lists.newArrayList("respawnanchorblockdamage", result.getBoolean("nature_respawnanchorblockdamage"))); 
                allflags.add(Lists.newArrayList("pistonsfromwilderness", result.getBoolean("nature_pistonsfromwilderness")));
                allflags.add(Lists.newArrayList("plantgrowth", result.getBoolean("nature_plantgrowth")));
            }

            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return allflags;
    }
}
