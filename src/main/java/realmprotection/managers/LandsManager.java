package realmprotection.managers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.Lists;

import realmprotection.RealmProtection;

public class LandsManager {
    private static final Map<String, List<Object>> land_id_cache = new HashMap<>();
    private static final Map<String, List<Object>> land_name_cache = new HashMap<>();
    private static final Map<String, List<Object>> land_owner_name_cache = new HashMap<>();
    private static final Map<String, List<Boolean>> land_id_nature_flags_cache = new HashMap<>();

    public static void cacheUpdateAll() {
        String sql = "SELECT * FROM lands";

        try {
            land_id_cache.clear();
            land_name_cache.clear();
            land_owner_name_cache.clear();
            land_id_nature_flags_cache.clear();

            Connection connection = RealmProtection.database.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);

            ResultSet result = statement.executeQuery();

            while (result.next()) {
                int land_id = result.getInt("id");
                String land_name = result.getString("land_name");
                String owner_name = result.getString("owner_name");
                double location_x = result.getDouble("location_x");
                double location_y = result.getDouble("location_y");
                double location_z = result.getDouble("location_z");
                String location_world = result.getString("location_world");
                long created_at = result.getLong("created_at");
                double balance = result.getDouble("balance");

                boolean nature_hostilemobsspawn = result.getBoolean("nature_hostilemobsspawn");
                boolean nature_passivemobsspawn = result.getBoolean("nature_passivemobsspawn");
                boolean nature_leavesdecay = result.getBoolean("nature_leavesdecay");
                boolean nature_firespread = result.getBoolean("nature_firespread");
                boolean nature_liquidflow = result.getBoolean("nature_liquidflow");
                boolean nature_tntblockdamage = result.getBoolean("nature_tntblockdamage");
                boolean nature_respawnanchorblockdamage = result.getBoolean("nature_respawnanchorblockdamage");
                boolean nature_pistonsfromwilderness = result.getBoolean("nature_pistonsfromwilderness");
                boolean nature_dispensersfromwilderness = result.getBoolean("nature_dispensersfromwilderness");
                boolean nature_plantgrowth = result.getBoolean("nature_plantgrowth");

                List<Object> data_land_cache = Lists.newArrayList(land_id, land_name, owner_name,
                        location_x, location_y, location_z, location_world, created_at, balance);
                List<Boolean> data_land_id_nature_flags_cache = Lists.newArrayList(
                        nature_hostilemobsspawn,
                        nature_passivemobsspawn,
                        nature_leavesdecay,
                        nature_firespread,
                        nature_liquidflow,
                        nature_tntblockdamage,
                        nature_respawnanchorblockdamage,
                        nature_pistonsfromwilderness,
                        nature_dispensersfromwilderness,
                        nature_plantgrowth);

                land_id_cache.put("" + land_id, data_land_cache);
                land_name_cache.put(land_name, data_land_cache);
                land_owner_name_cache.put(owner_name, data_land_cache);
                land_id_nature_flags_cache.put("" + land_id, data_land_id_nature_flags_cache);
            }

            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

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
                "balance, " +
                "nature_hostilemobsspawn, " +
                "nature_passivemobsspawn, " +
                "nature_leavesdecay, " +
                "nature_firespread, " +
                "nature_liquidflow, " +
                "nature_tntblockdamage, " +
                "nature_respawnanchorblockdamage, " +
                "nature_pistonsfromwilderness, " +
                "nature_dispensersfromwilderness, " +
                "nature_plantgrowth " +
                ") VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

        try {
            Connection connection = RealmProtection.database.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);

            long timestamp = System.currentTimeMillis();

            statement.setString(1, land_name);
            statement.setString(2, player_name);
            statement.setLong(3, timestamp);
            statement.setString(4, location_world);
            statement.setDouble(5, location_x);
            statement.setDouble(6, location_y);
            statement.setDouble(7, location_z);
            statement.setDouble(8, 0);
            statement.setBoolean(9, true); // Hostile mob spawn
            statement.setBoolean(10, true); // Passive mob spawn
            statement.setBoolean(11, true); // Leaves decay
            statement.setBoolean(12, false); // Fire spread
            statement.setBoolean(13, false); // Liquid flow
            statement.setBoolean(14, false); // TnT block damage
            statement.setBoolean(15, false); // Anchor block damage
            statement.setBoolean(16, false); // Pistons damage
            statement.setBoolean(17, false); // Dispensers damage
            statement.setBoolean(18, true); // Plant growth

            statement.execute();
            statement.close();

            cacheUpdateAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void deleteLand(int land_id) {
        String sql = "DELETE FROM lands WHERE id = ?";

        try {
            Connection connection = RealmProtection.database.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);

            statement.setInt(1, land_id);

            statement.execute();
            statement.close();

            cacheUpdateAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static boolean hasLand(String player_name) {
        String sql = "SELECT COUNT (*) AS count FROM lands WHERE owner_name = ?";
        boolean bool = false;

        try {
            Connection connection = RealmProtection.database.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);

            statement.setString(1, player_name);

            ResultSet rs = statement.executeQuery();

            if (rs.next()) {
                int count = rs.getInt("count");
                bool = count > 0;
            }

            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return bool;
    }

    public static boolean landNameExist(String land_name) {
        String sql = "SELECT COUNT (*) AS count FROM lands WHERE land_name COLLATE NOCASE = ?";
        boolean exists = false;

        try {
            Connection connection = RealmProtection.database.getConnection();
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
        if (land_owner_name_cache.containsKey(player_name)) {
            List<Object> data = land_owner_name_cache.get(player_name);

            switch (variable) {
                case "id":
                    return "" + data.get(0);
                case "land_name":
                    return "" + data.get(1);
                case "owner_name":
                    return "" + data.get(2);
                case "location_x":
                    return "" + data.get(3);
                case "location_y":
                    return "" + data.get(4);
                case "location_z":
                    return "" + data.get(5);
                case "location_world":
                    return "" + data.get(6);
                case "created_at":
                    return "" + data.get(7);
                case "balance":
                    return "" + data.get(8);
                default:
                    return null;
            }
        }

        String sql = "SELECT * FROM lands WHERE owner_name = ?";

        try {
            Connection connection = RealmProtection.database.getConnection();
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

    public static String getLandDetailById(int land_id, String variable) {
        if (land_id_cache.containsKey("" + land_id)) {
            List<Object> data = land_id_cache.get("" + land_id);

            switch (variable) {
                case "id":
                    return "" + data.get(0);
                case "land_name":
                    return "" + data.get(1);
                case "owner_name":
                    return "" + data.get(2);
                case "location_x":
                    return "" + data.get(3);
                case "location_y":
                    return "" + data.get(4);
                case "location_z":
                    return "" + data.get(5);
                case "location_world":
                    return "" + data.get(6);
                case "created_at":
                    return "" + data.get(7);
                case "balance":
                    return "" + data.get(8);
                default:
                    return null;
            }
        }

        String sql = "SELECT * FROM lands WHERE id = ?";

        try {
            Connection connection = RealmProtection.database.getConnection();
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
        if (land_name_cache.containsKey(land_name)) {
            List<Object> data = land_name_cache.get(land_name);

            switch (variable) {
                case "id":
                    return "" + data.get(0);
                case "land_name":
                    return "" + data.get(1);
                case "owner_name":
                    return "" + data.get(2);
                case "location_x":
                    return "" + data.get(3);
                case "location_y":
                    return "" + data.get(4);
                case "location_z":
                    return "" + data.get(5);
                case "location_world":
                    return "" + data.get(6);
                case "created_at":
                    return "" + data.get(7);
                case "balance":
                    return "" + data.get(8);
                default:
                    return null;
            }
        }

        String sql = "SELECT * FROM lands WHERE land_name COLLATE NOCASE = ?";

        try {
            Connection connection = RealmProtection.database.getConnection();
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

    public static boolean getFlagValue(int land_id, String flag_name) {
        if (land_id_nature_flags_cache.containsKey("" + land_id)) {
            List<Boolean> data = land_id_nature_flags_cache.get("" + land_id);

            switch (flag_name) {
                case "hostilemobsspawn":
                    return data.get(0);
                case "passivemobsspawn":
                    return data.get(1);
                case "leavesdecay":
                    return data.get(2);
                case "firespread":
                    return data.get(3);
                case "liquidflow":
                    return data.get(4);
                case "tntblockdamage":
                    return data.get(5);
                case "respawnanchorblockdamage":
                    return data.get(6);
                case "pistonsfromwilderness":
                    return data.get(7);
                case "dispensersfromwilderness":
                    return data.get(8);
                case "plantgrowth":
                    return data.get(9);
                default:
                    return false;
            }
        }

        String sql = "SELECT * FROM lands WHERE id = ?";

        try {
            Connection connection = RealmProtection.database.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);

            statement.setInt(1, land_id);

            ResultSet result = statement.executeQuery();

            if (result.next()) {
                boolean value = result.getBoolean("nature_" + flag_name);
                return value;
            }

            statement.close();

            cacheUpdateAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public static void updateLandName(int land_id, String new_land_name) {
        String sql = "UPDATE lands SET land_name='" + new_land_name + "' WHERE id = ?";

        try {
            Connection connection = RealmProtection.database.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);

            statement.setInt(1, land_id);

            statement.execute();

            statement.close();

            cacheUpdateAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return;
    }

    public static void updateSpawnLocation(int land_id, double location_x, double location_y, double location_z,
            String location_world) {
        String sql = "UPDATE lands SET location_x=" + location_x + ", location_y=" + location_y + ", location_z="
                + location_z + ", location_world = '" + location_world + "' WHERE id = ?";

        try {
            Connection connection = RealmProtection.database.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);

            statement.setInt(1, land_id);

            statement.execute();

            statement.close();

            cacheUpdateAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return;
    }

    public static void updateBalance(int land_id, double new_balance) {
        String sql = "UPDATE lands SET balance=" + new_balance + " WHERE id = ?";

        try {
            Connection connection = RealmProtection.database.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);

            statement.setInt(1, land_id);

            statement.execute();

            statement.close();

            cacheUpdateAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return;
    }

    public static void updateNatureFlagValue(int land_id, String flag, boolean value) {
        String sql = "UPDATE lands SET nature_" + flag + "=" + value + " WHERE id = ?";

        try {
            Connection connection = RealmProtection.database.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);

            statement.setInt(1, land_id);

            statement.execute();

            statement.close();

            cacheUpdateAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return;
    }

    public static List<String> listAllLandNames() {
        List<String> landnames = new ArrayList<>();

        if (!land_name_cache.isEmpty()) {
            @SuppressWarnings("rawtypes")
            Set keys = land_name_cache.keySet();

            @SuppressWarnings("rawtypes")
            Iterator iterator = keys.iterator();

            while (iterator.hasNext()) {
                landnames.add((String) iterator.next());
            }

            return landnames;
        }

        String sql = "SELECT * FROM lands";

        try {
            Connection connection = RealmProtection.database.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);

            ResultSet result = statement.executeQuery();

            while (result.next()) {
                String string = result.getString("land_name");

                landnames.add(string);
            }

            statement.close();

            cacheUpdateAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return landnames;
    }

    public static List<List<Object>> listEnabledAndDisabledFlagsForLand(int land_id) {
        List<List<Object>> allflags = new ArrayList<>();

        if (land_id_nature_flags_cache.containsKey("" + land_id)) {
            List<Boolean> data = land_id_nature_flags_cache.get("" + land_id);

            allflags.add(Lists.newArrayList("hostilemobsspawn", data.get(0)));
            allflags.add(Lists.newArrayList("passivemobsspawn", data.get(1)));
            allflags.add(Lists.newArrayList("leavesdecay", data.get(2)));
            allflags.add(Lists.newArrayList("firespread", data.get(3)));
            allflags.add(Lists.newArrayList("liquidflow", data.get(4)));
            allflags.add(Lists.newArrayList("tntblockdamage", data.get(5)));
            allflags.add(Lists.newArrayList("respawnanchorblockdamage",
                    data.get(6)));
            allflags.add(
                    Lists.newArrayList("pistonsfromwilderness", data.get(7)));
            allflags.add(Lists.newArrayList("dispensersfromwilderness", data.get(8)));
            allflags.add(Lists.newArrayList("plantgrowth", data.get(9)));

            return allflags;
        }

        String sql = "SELECT * FROM lands WHERE id = ?";

        try {
            Connection connection = RealmProtection.database.getConnection();
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
                allflags.add(Lists.newArrayList("respawnanchorblockdamage",
                        result.getBoolean("nature_respawnanchorblockdamage")));
                allflags.add(
                        Lists.newArrayList("pistonsfromwilderness", result.getBoolean("nature_pistonsfromwilderness")));
                allflags.add(Lists.newArrayList("dispensersfromwilderness", result.getBoolean("nature_dispensersfromwilderness")));
                allflags.add(Lists.newArrayList("plantgrowth", result.getBoolean("nature_plantgrowth")));
            }

            statement.close();

            cacheUpdateAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return allflags;
    }
}
