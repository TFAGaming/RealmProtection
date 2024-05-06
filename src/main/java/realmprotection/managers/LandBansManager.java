package realmprotection.managers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;

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
                String player_uuid = result.getString("player_uuid");
                String reason = result.getString("reason");

                List<Object> data_land_member_cache = Lists.newArrayList(ban_id, land_id, player_uuid, reason);

                cache.put(createCacheKey(land_id, player_uuid), data_land_member_cache);
            }

            statement.close();
        } catch (SQLException error) {
            error.printStackTrace();

            cacheUpdateAll();
        }
    }

    public static void banPlayerFromLand(int land_id, String player_uuid, String reason) {
        String sql = "INSERT INTO land_bans (land_id, player_uuid, reason) VALUES (?, ?, ?)";

        try {
            Connection connection = RealmProtection.database.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);

            statement.setInt(1, land_id);
            statement.setString(2, player_uuid);
            statement.setString(3, reason);

            statement.executeUpdate();
            statement.close();

            cacheUpdateAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void unbanPlayerFromLand(int land_id, String player_uuid) {
        String sql = "DELETE FROM land_bans WHERE land_id = ? AND player_uuid COLLATE NOCASE = ?";

        try {
            Connection connection = RealmProtection.database.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);

            statement.setInt(1, land_id);
            statement.setString(2, player_uuid);

            statement.executeUpdate();
            statement.close();

            cacheUpdateAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static String getBanReason(int land_id, String player_uuid) {
        if (cache.containsKey(createCacheKey(land_id, player_uuid))) {
            List<Object> data = cache.get(createCacheKey(land_id, player_uuid));

            return (String) data.get(3);
        }

        String sql = "SELECT * FROM land_bans WHERE land_id = ? AND player_uuid COLLATE NOCASE = ?";

        try {
            Connection connection = RealmProtection.database.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);

            statement.setInt(1, land_id);
            statement.setString(2, player_uuid);

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

    public static boolean isPlayerBannedFromLand(int land_id, String player_uuid) {
        return cache.containsKey(createCacheKey(land_id, player_uuid));
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
                String player_uuid = result.getString("player_uuid");
                String reason = result.getString("reason");

                data.add(Lists.newArrayList(Bukkit.getOfflinePlayer(UUID.fromString(player_uuid)).getName(), reason));
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

    private static String createCacheKey(Object land_id, String player_uuid) {
        return land_id + "," + player_uuid;
    }
}
