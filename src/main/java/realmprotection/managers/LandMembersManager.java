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
import realmprotection.utils.LoadConfig;

public class LandMembersManager {
    private static final Map<String, List<Object>> cache = new HashMap<>();

    public static void cacheUpdateAll() {
        String sql = "SELECT * FROM land_members";

        try {
            cache.clear();

            Connection connection = RealmProtection.database.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);

            ResultSet result = statement.executeQuery();

            while (result.next()) {
                int member_id = result.getInt("id");
                String land_id = result.getString("land_id");
                String member_name = result.getString("member_name");
                String role_id = result.getString("role_id");

                List<Object> data_land_member_cache = Lists.newArrayList(member_id, land_id, member_name, role_id);

                cache.put(createCacheKey(land_id, member_name), data_land_member_cache);
            }

            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void invitePlayerToLand(int land_id, String player_name, int role_id) {
        String sql = "INSERT INTO land_members (land_id, member_name, role_id) VALUES (?, ?, ?)";

        try {
            Connection connection = RealmProtection.database.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);

            statement.setInt(1, land_id);
            statement.setString(2, player_name);
            statement.setInt(3, role_id);

            statement.executeUpdate();
            statement.close();

            cacheUpdateAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void removePlayerFromLand(int land_id, String player_name) {
        String sql = "DELETE FROM land_members WHERE land_id = ? AND member_name COLLATE NOCASE = ?";

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

    public static String getRoleNameFromPlayername(int land_id, String player_name) {
        if (cache.containsKey(createCacheKey(land_id, player_name))) {
            List<Object> data = cache.get(createCacheKey(land_id, player_name));

            String role_id = (String) data.get(3);
            String role_name = RolesManager.getRoleDetailById(land_id, new Integer(role_id), "role_name");

            return role_name;
        }

        String sql = "SELECT * FROM land_members WHERE land_id = ? AND member_name COLLATE NOCASE = ?";

        try {
            Connection connection = RealmProtection.database.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);

            statement.setInt(1, land_id);
            statement.setString(2, player_name);

            ResultSet rs = statement.executeQuery();

            if (rs.next()) {
                String role_id = rs.getString("role_id");
                String role_name = RolesManager.getRoleDetailById(land_id, new Integer(role_id), "role_name");

                return role_name;
            }

            statement.close();

            cacheUpdateAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return LoadConfig.landRolesDefaultString("__DEFAULT_VISITOR_ROLE__");
    }

    public static boolean isPlayerInTheLand(int land_id, String player_name) {
        return cache.containsKey(createCacheKey(land_id, player_name));
    }

    public static boolean hasPlayerThePermissionToDo(int land_id, String player_name, String permission_name) {
        boolean isTrusted = isPlayerInTheLand(land_id, player_name);

        if (isTrusted) {
            String role_id = getRoleNameFromPlayername(land_id, player_name);

            if (role_id == LoadConfig.landRolesDefaultString("__DEFAULT_VISITOR_ROLE__")) {
                boolean value = RolesManager.getPermissionValue(land_id, LoadConfig.landRolesDefaultString("__DEFAULT_VISITOR_ROLE__"), permission_name);
                return value;
            } else {
                boolean value = RolesManager.getPermissionValue(land_id, role_id, permission_name);
                return value;
            }
        } else {
            boolean value = RolesManager.getPermissionValue(land_id, LoadConfig.landRolesDefaultString("__DEFAULT_VISITOR_ROLE__"), permission_name);
            return value;
        }
    }

    public static List<List<String>> listAllMembersData(int land_id) {
        String sql = "SELECT * FROM land_members WHERE land_id = ?";

        List<List<String>> data = new ArrayList<>();

        try {
            Connection connection = RealmProtection.database.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);

            statement.setInt(1, land_id);

            ResultSet result = statement.executeQuery();

            while (result.next()) {
                String member_name = result.getString("member_name");
                String role_id = result.getString("role_id");

                String role_name = RolesManager.getRoleDetailById(land_id, new Integer(role_id), "role_name");

                data.add(Lists.newArrayList(member_name, role_name));
            }

            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return data;
    }

    public static int getMembersCountOfLand(int land_id) {
        String sql = "SELECT COUNT (*) AS count FROM land_members WHERE land_id = ?";

        try {
            Connection connection = RealmProtection.database.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);

            statement.setInt(1, land_id);
            ResultSet rs = statement.executeQuery();

            if (rs.next()) {
                int count = rs.getInt("count");
                return count;
            }

            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }

    public static void deleteAllMembersFromLand(int land_id) {
        String sql = "DELETE FROM land_members WHERE land_id = ?";

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

    private static String createCacheKey(Object land_id, String member_name) {
        return land_id + "," + member_name;
    }
}
