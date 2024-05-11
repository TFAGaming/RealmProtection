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
                String member_uuid = result.getString("member_uuid");
                String role_id = result.getString("role_id");

                List<Object> data_land_member_cache = Lists.newArrayList(member_id, land_id, member_uuid, role_id);

                cache.put(createCacheKey(land_id, member_uuid), data_land_member_cache);
            }

            statement.close();
        } catch (SQLException error) {
            error.printStackTrace();

            cacheUpdateAll();
        }
    }

    public static void invitePlayerToLand(int land_id, String player_uuid, int role_id) {
        String sql = "INSERT INTO land_members (land_id, member_uuid, role_id) VALUES (?, ?, ?)";

        try {
            Connection connection = RealmProtection.database.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);

            statement.setInt(1, land_id);
            statement.setString(2, player_uuid);
            statement.setInt(3, role_id);

            statement.executeUpdate();
            statement.close();

            cacheUpdateAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void removePlayerFromLand(int land_id, String player_uuid) {
        String sql = "DELETE FROM land_members WHERE land_id = ? AND member_uuid COLLATE NOCASE = ?";

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

    public static void removeAllMembersFromLand(int land_id) {
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

    public static String getRoleNameFromPlayername(int land_id, String player_uuid) {
        if (cache.containsKey(createCacheKey(land_id, player_uuid))) {
            List<Object> data = cache.get(createCacheKey(land_id, player_uuid));

            String role_id = (String) data.get(3);
            String role_name = RolesManager.getRoleDetailById(land_id, new Integer(role_id), "role_name");

            return role_name;
        }

        RealmProtection plugin = RealmProtection.getPlugin(RealmProtection.class);

        return plugin.getConfig().getString("roles.__DEFAULT_VISITOR_ROLE__");
    }

    public static boolean isPlayerInTheLand(int land_id, String player_uuid) {
        return cache.containsKey(createCacheKey(land_id, player_uuid));
    }

    public static boolean hasFlagPermission(int land_id, String player_uuid, String permission_name) {
        boolean isTrusted = isPlayerInTheLand(land_id, player_uuid);

        RealmProtection plugin = RealmProtection.getPlugin(RealmProtection.class);

        if (isTrusted) {
            String role_name = getRoleNameFromPlayername(land_id, player_uuid);

            if (role_name == plugin.getConfig().getString("roles.__DEFAULT_VISITOR_ROLE__")) {
                boolean value = RolesManager.getPermissionValue(land_id, plugin.getConfig().getString("roles.__DEFAULT_VISITOR_ROLE__"), permission_name);
                return value;
            } else {
                boolean value = RolesManager.getPermissionValue(land_id, role_name, permission_name);
                return value;
            }
        } else {
            boolean value = RolesManager.getPermissionValue(land_id, plugin.getConfig().getString("roles.__DEFAULT_VISITOR_ROLE__"), permission_name);
            return value;
        }
    }

    public static List<List<String>> listAllMembersData(int land_id) {
        List<List<String>> members_data = new ArrayList<>();

        for (Map.Entry<String, List<Object>> entry : cache.entrySet()) {
            List<Object> data = entry.getValue();

            if (new Integer((String) data.get(1)) == land_id) {
                String role_name = RolesManager.getRoleDetailById(land_id, new Integer((String) data.get(3)), "role_name");

                members_data.add(Lists.newArrayList(Bukkit.getOfflinePlayer(UUID.fromString((String) data.get(2))).getName(), role_name)); 
            }
        }

        return members_data;
    }

    public static int getMembersCountOfLand(int land_id) {
        int count = 0;

        for (Map.Entry<String, List<Object>> entry : cache.entrySet()) {
            List<Object> data = entry.getValue();

            if (new Integer((String) data.get(1)) == land_id) {
                count++;
            }
        }

        return count;
    }

    private static String createCacheKey(Object land_id, String member_uuid) {
        return land_id + "," + member_uuid;
    }
}
