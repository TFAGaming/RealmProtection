package realmprotection.managers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.entity.Player;

import com.google.common.collect.Lists;

import realmprotection.RealmProtection;

public class LandsManager {
    private static final Map<String, List<Object>> land_id_cache = new HashMap<>();
    private static final Map<String, List<Object>> land_name_cache = new HashMap<>();
    private static final Map<String, List<Object>> land_owner_uuid_cache = new HashMap<>();
    private static final Map<String, List<Boolean>> land_id_nature_flags_cache = new HashMap<>();

    public static void cacheUpdateAll() {
        String sql = "SELECT * FROM lands";

        try {
            land_id_cache.clear();
            land_name_cache.clear();
            land_owner_uuid_cache.clear();
            land_id_nature_flags_cache.clear();

            Connection connection = RealmProtection.database.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);

            ResultSet result = statement.executeQuery();

            while (result.next()) {
                int land_id = result.getInt("id");
                String land_name = result.getString("land_name");
                String owner_uuid = result.getString("owner_uuid");
                double location_x = result.getDouble("location_x");
                double location_y = result.getDouble("location_y");
                double location_z = result.getDouble("location_z");
                String location_world = result.getString("location_world");
                float location_yaw = result.getFloat("location_yaw");
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
                boolean nature_ravagergrief = result.getBoolean("nature_ravagergrief");
                boolean nature_endermangrief = result.getBoolean("nature_endermangrief");

                List<Object> data_land_cache = Lists.newArrayList(land_id, land_name, owner_uuid,
                        location_x, location_y, location_z, location_world, created_at, balance, location_yaw);
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
                        nature_plantgrowth,
                        nature_ravagergrief,
                        nature_endermangrief);

                land_id_cache.put("" + land_id, data_land_cache);
                land_name_cache.put(land_name, data_land_cache);
                land_owner_uuid_cache.put(owner_uuid, data_land_cache);
                land_id_nature_flags_cache.put("" + land_id, data_land_id_nature_flags_cache);
            }

            statement.close();
        } catch (SQLException error) {
            error.printStackTrace();

            cacheUpdateAll();
        }
    }

    public static void createNewLand(String land_name, String player_uuid, String location_world, double location_x,
            double location_y, double location_z) {
        String sql = "INSERT INTO lands ( " +
                "land_name, " +
                "owner_uuid, " +
                "created_at, " +
                "location_world, " +
                "location_x, " +
                "location_y, " +
                "location_z, " +
                "location_yaw, " +
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
                "nature_plantgrowth, " +
                "nature_ravagergrief, " +
                "nature_endermangrief" +
                ") VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

        try {
            Connection connection = RealmProtection.database.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);

            long timestamp = System.currentTimeMillis();

            statement.setString(1, land_name);
            statement.setString(2, player_uuid);
            statement.setLong(3, timestamp);
            statement.setString(4, location_world);
            statement.setDouble(5, location_x);
            statement.setDouble(6, location_y);
            statement.setDouble(7, location_z);
            statement.setDouble(8, 0);
            statement.setDouble(9, 0);
            statement.setBoolean(10, true); // Hostile mob spawn
            statement.setBoolean(11, true); // Passive mob spawn
            statement.setBoolean(12, true); // Leaves decay
            statement.setBoolean(13, false); // Fire spread
            statement.setBoolean(14, false); // Liquid flow
            statement.setBoolean(15, false); // TnT block damage
            statement.setBoolean(16, false); // Anchor block damage
            statement.setBoolean(17, false); // Pistons damage
            statement.setBoolean(18, false); // Dispensers damage
            statement.setBoolean(19, true); // Plant growth
            statement.setBoolean(20, false); // Ravager grief
            statement.setBoolean(21, false); // Enderman grief

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

    public static boolean hasLand(String player_uuid) {
        String sql = "SELECT COUNT (*) AS count FROM lands WHERE owner_uuid = ?";
        boolean bool = false;

        try {
            Connection connection = RealmProtection.database.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);

            statement.setString(1, player_uuid);

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

    public static String getLandDetail(String player_uuid, String variable) {
        if (land_owner_uuid_cache.containsKey(player_uuid)) {
            List<Object> data = land_owner_uuid_cache.get(player_uuid);

            switch (variable) {
                case "id":
                    return "" + data.get(0);
                case "land_name":
                    return "" + data.get(1);
                case "owner_uuid":
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
                case "location_yaw":
                    return "" + data.get(9);
                default:
                    return null;
            }
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
                case "owner_uuid":
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
                case "location_yaw":
                    return "" + data.get(9);
                default:
                    return null;
            }
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
                case "owner_uuid":
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
                case "location_yaw":
                    return "" + data.get(9);
                default:
                    return null;
            }
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
                case "ravagergrief":
                    return data.get(10);
                case "endermangrief":
                    return data.get(11);
                default:
                    return false;
            }
        }

        return false;
    }

    public static int getTotalLandsCount() {
        return land_id_cache.size();
    }

    public static List<List<Object>> getTopLands(int limit) {
        List<List<Object>> topLands = new ArrayList<>();

        for (Map.Entry<String, List<Object>> entry : land_id_cache.entrySet()) {
            List<Object> data = entry.getValue();

            int land_id = (int) data.get(0);
            String landName = (String) data.get(1);
            String owner_uuid = (String) data.get(2);
            int total_chunks = ChunksManager.getChunksCountOfLand(land_id);
            double balance = (double) data.get(8);

            List<Object> landInfo = new ArrayList<>();
            landInfo.add(land_id);
            landInfo.add(landName);
            landInfo.add(owner_uuid);
            landInfo.add(total_chunks);
            landInfo.add(balance);

            topLands.add(landInfo);
        }

        topLands.sort(Comparator.comparingDouble((List<Object> list) -> (double) list.get(4)).reversed());

        if (topLands.size() > limit) {
            topLands = topLands.subList(0, limit);
        }

        return topLands;
    }

    public static int getPlayerLandRank(Player player) {
        List<List<Object>> topLands = getTopLands(getTotalLandsCount());
        int index = 0;

        for (int i = 0; i < topLands.size(); i++) {
            if (((String) topLands.get(i).get(2)).equals(player.getUniqueId().toString())) {
                index = i;

                break;
            }
        }

        return index;
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
            String location_world, float player_yaw) {
        String sql = "UPDATE lands SET location_x=" + location_x + ", location_y=" + location_y + ", location_z="
                + location_z + ", location_world = '" + location_world + "', location_yaw=" + player_yaw
                + " WHERE id = ?";

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

    @SuppressWarnings("rawtypes")
    public static List<String> listAllLandNames() {
        List<String> landnames = new ArrayList<>();

        if (!land_name_cache.isEmpty()) {
            Set keys = land_name_cache.keySet();
            Iterator iterator = keys.iterator();

            while (iterator.hasNext()) {
                landnames.add((String) iterator.next());
            }

            return landnames;
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
            allflags.add(Lists.newArrayList("ravagergrief", data.get(10)));
            allflags.add(Lists.newArrayList("endermangrief", data.get(11)));

            return allflags;
        }

        return allflags;
    }

    public static List<String> listAllFlags() {
        List<String> arraylist = new ArrayList<>();

        arraylist.add("hostilemobsspawn");
        arraylist.add("passivemobsspawn");
        arraylist.add("leavesdecay");
        arraylist.add("firespread");
        arraylist.add("liquidflow");
        arraylist.add("tntblockdamage");
        arraylist.add("respawnanchorblockdamage");
        arraylist.add("pistonsfromwilderness");
        arraylist.add("dispensersfromwilderness");
        arraylist.add("plantgrowth");
        arraylist.add("ravagergrief");
        arraylist.add("endermangrief");

        return arraylist;
    }
}
