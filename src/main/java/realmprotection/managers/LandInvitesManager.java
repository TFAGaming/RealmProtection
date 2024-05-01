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

public class LandInvitesManager {
    private static final Map<String, List<Object>> cache = new HashMap<>();

    public static void cacheUpdateAll() {
        String sql = "SELECT * FROM land_invites";

        try {
            cache.clear();

            Connection connection = RealmProtection.database.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);

            ResultSet result = statement.executeQuery();

            while (result.next()) {
                int invite_id = result.getInt("id");
                String land_id = result.getString("land_id");
                String inviter_name = result.getString("inviter_name");
                String player_name = result.getString("player_name");
                int role_id = result.getInt("role_id");

                List<Object> data_land_member_cache = Lists.newArrayList(invite_id, land_id, inviter_name, player_name, role_id);

                cache.put(createCacheKey(land_id, player_name), data_land_member_cache);
            }

            statement.close();
        } catch (SQLException error) {
            error.printStackTrace();

            cacheUpdateAll();
        }
    }

    public static void invitePlayerToLand(int land_id, String inviter_name, String player_name, int role_id) {
        String sql = "INSERT INTO land_invites (land_id, inviter_name, player_name, role_id) VALUES (?, ?, ?, ?)";

        try {
            Connection connection = RealmProtection.database.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);

            statement.setInt(1, land_id);
            statement.setString(2, inviter_name);
            statement.setString(3, player_name);
            statement.setInt(4, role_id);

            statement.executeUpdate();
            statement.close();

            cacheUpdateAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void removeInviteFromPlayer(int land_id, String player_name) {
        String sql = "DELETE FROM land_invites WHERE land_id = ? AND player_name COLLATE NOCASE = ?";

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

    public static String getInviteDetail(int land_id, String player_name, String variable) {
        if (cache.containsKey(createCacheKey(land_id, player_name))) {
            List<Object> data = cache.get(createCacheKey(land_id, player_name));

            switch (variable) {
                case "id":
                    return "" + data.get(0);
                case "land_id":
                    return "" + data.get(1);
                case "inviter_name":
                    return "" + data.get(2);
                case "player_name":
                    return "" + data.get(3);
                case "role_id":
                    return "" + data.get(4);
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

    public static boolean isPlayerInvited(int land_id, String player_name) {
        return cache.containsKey(createCacheKey(land_id, player_name));
    }

    public static List<List<String>> listAllInvitesFromLandId(int land_id) {
        String sql = "SELECT * FROM land_invites WHERE land_id = ?";

        List<List<String>> data = new ArrayList<>();

        try {
            Connection connection = RealmProtection.database.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);

            statement.setInt(1, land_id);

            ResultSet result = statement.executeQuery();

            while (result.next()) {
                String inviter_name = result.getString("inviter_name");
                String player_name = result.getString("player_name");

                data.add(Lists.newArrayList(inviter_name, player_name));
            }
            
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return data;
    }

    public static List<List<String>> listAllInvitesForPlayer(String player_name) {
        String sql = "SELECT * FROM land_invites WHERE player_name = ?";

        List<List<String>> data = new ArrayList<>();

        try {
            Connection connection = RealmProtection.database.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);

            statement.setString(1, player_name);

            ResultSet result = statement.executeQuery();

            while (result.next()) {
                String inviter_name = result.getString("inviter_name");
                String land_id = result.getString("land_id");

                String land_name = LandsManager.getLandDetailById(new Integer(land_id), "land_name");

                data.add(Lists.newArrayList(inviter_name, land_id, land_name));
            }
            
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return data;
    }

    private static String createCacheKey(Object land_id, String player_name) {
        return land_id + "," + player_name;
    }
}
