package realmprotection.managers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;

import realmprotection.RealmProtection;

public class LandBansManager {
    private static final Map<String, List<Object>> cache = new HashMap<>();

    public static void cacheUpdateAll() {
        String sql = "SELECT * FROM land_bans";

        try {
            cache.clear();

            Connection connection = RealmProtection.database.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);

            ResultSet result = statement.executeQuery();

            while (result.next()) {
                int ban_id = result.getInt("id");
                String land_id = result.getString("land_id");
                String player_name = result.getString("player_name");
                String reason = result.getString("reason");

                List<Object> data_land_member_cache = Lists.newArrayList(ban_id, land_id, player_name, reason);

                cache.put(createCacheKey(land_id, player_name), data_land_member_cache);
            }

            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void banPlayerFromLand(int land_id, String player_name, String reason) {
        String sql = "INSERT INTO land_bans (land_id, player_name, reason) VALUES (?, ?, ?)";

        try {
            Connection connection = RealmProtection.database.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);

            statement.setInt(1, land_id);
            statement.setString(2, player_name);
            statement.setString(3, reason);

            statement.executeUpdate();
            statement.close();

            cacheUpdateAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void unbanPlayerFromLand(int land_id, String player_name) {
        String sql = "DELETE FROM land_bans WHERE land_id = ? AND player_name COLLATE NOCASE = ?";

        try {
            Connection connection = RealmProtection.database.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);

            statement.setInt(1, land_id);
            statement.setString(2, player_name);

            statement.executeUpdate();
            statement.close();

            cacheUpdateAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static String getBanReason(int land_id, String player_name) {
        if (cache.containsKey(createCacheKey(land_id, player_name))) {
            List<Object> data = cache.get(createCacheKey(land_id, player_name));

            return (String) data.get(3);
        }

        String sql = "SELECT * FROM land_bans WHERE land_id = ? AND player_name COLLATE NOCASE = ?";

        try {
            Connection connection = RealmProtection.database.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);

            statement.setInt(1, land_id);
            statement.setString(2, player_name);

            ResultSet rs = statement.executeQuery();

            if (rs.next()) {
                String reason = rs.getString("reason");
                return reason;
            }

            statement.close();

            cacheUpdateAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return "No reason was provided";
    }

    public static boolean isPlayerBannedFromLand(int land_id, String player_name) {
        return cache.containsKey(createCacheKey(land_id, player_name));
    }

    public static List<List<String>> listAllBannedPlayersData(int land_id) {
        String sql = "SELECT * FROM land_bans WHERE land_id = ?";

        List<List<String>> data = new ArrayList<>();

        try {
            Connection connection = RealmProtection.database.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);

            statement.setInt(1, land_id);

            ResultSet result = statement.executeQuery();

            while (result.next()) {
                String player = result.getString("player_name");
                String reason = result.getString("reason");

                data.add(Lists.newArrayList(player, reason));
            }
            
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return data;
    }

    public static void deleteAllBannedPlayersFromLand(int land_id) {
        String sql = "DELETE FROM land_bans WHERE land_id = ?";

        try {
            Connection connection = RealmProtection.database.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);

            statement.setInt(1, land_id);

            statement.executeUpdate();
            statement.close();

            cacheUpdateAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static String createCacheKey(Object land_id, String player_name) {
        return land_id + "," + player_name;
    }
}
