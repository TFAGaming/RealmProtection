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

public class LandMembersManager {
    private static final Map<String, List<Object>> land_id_and_member_name_cache = new HashMap<>();

    public static void cacheUpdateAll() {
        String sql = "SELECT * FROM land_members";

        try {
            land_id_and_member_name_cache.clear();

            Connection connection = RealmProtection.database.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);

            ResultSet result = statement.executeQuery();

            while (result.next()) {
                int member_id = result.getInt("id");
                String land_id = result.getString("land_id");
                String member_name = result.getString("member_name");
                String role_name = result.getString("role");

                List<Object> data_land_member_cache = Lists.newArrayList(member_id, land_id, member_name, role_name);

                System.out.println("CACHE UPDATED: " + data_land_member_cache);

                land_id_and_member_name_cache.put(land_id + "," + member_name, data_land_member_cache);
            }

            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void invitePlayerToLand(Integer land_id, String player_name, String role) {
        String sql = "INSERT INTO land_members (land_id, member_name, role) VALUES (?, ?, ?)";

        try {
            Connection connection = RealmProtection.database.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);

            statement.setInt(1, land_id);
            statement.setString(2, player_name);
            statement.setString(3, role);

            statement.executeUpdate();
            statement.close();

            cacheUpdateAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void removePlayerFromLand(Integer land_id, String player_name) {
        String sql = "DELETE FROM land_members WHERE land_id = ? AND member_name = ?";

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

    public static String getRoleNameFromPlayername(Integer land_id, String player_name) {
        if (land_id_and_member_name_cache.containsKey(land_id + "," + player_name)) {
            List<Object> data = land_id_and_member_name_cache.get(land_id + "," + player_name);

            return (String) data.get(3);
        }

        String sql = "SELECT * FROM land_members WHERE land_id = ? AND member_name = ?";

        try {
            Connection connection = RealmProtection.database.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);

            statement.setInt(1, land_id);
            statement.setString(2, player_name);

            ResultSet rs = statement.executeQuery();

            if (rs.next()) {
                String role_name = rs.getString("role");
                return role_name;
            }

            statement.close();

            cacheUpdateAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return "Visitor";
    }

    public static boolean isPlayerInTheLand(Integer land_id, String player_name) {
        return land_id_and_member_name_cache.containsKey(land_id + "," + player_name);

        /*
        String sql = "SELECT COUNT (*) AS count FROM land_members WHERE land_id = ? AND member_name = ?";
        boolean exists = false;

        try {
            Connection connection = RealmProtection.database.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);

            statement.setInt(1, land_id);
            statement.setString(2, player_name);

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
        */
    }

    public static boolean hasPlayerThePermissionToDo(Integer land_id, String player_name, String permission_name) {
        boolean isTrusted = isPlayerInTheLand(land_id, player_name);

        System.out.println("IS TRUSTED? " + isTrusted);

        if (isTrusted) {
            String role_name = getRoleNameFromPlayername(land_id, player_name);

            System.out.println("ROLE NAME: " + role_name);

            if (role_name == "Visitor") {
                boolean value = RolesManager.getPermissionValue(land_id, "Visitor", permission_name);
                return value;
            } else {
                boolean value = RolesManager.getPermissionValue(land_id, role_name, permission_name);
                return value;
            }
        } else {
            boolean value = RolesManager.getPermissionValue(land_id, "Visitor", permission_name);
            return value;
        }
    }

    public static List<List<String>> listAllMembersData(Integer land_id) {
        String sql = "SELECT * FROM land_members WHERE land_id = ?";

        List<List<String>> data = new ArrayList<>();

        try {
            Connection connection = RealmProtection.database.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);

            statement.setInt(1, land_id);

            ResultSet result = statement.executeQuery();

            while (result.next()) {
                String member_name = result.getString("member_name");
                String role_name = result.getString("role");

                data.add(Lists.newArrayList(member_name, role_name));
            }
            
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return data;
    }

    public static int getMembersCountOfLand(Integer land_id) {
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
}
