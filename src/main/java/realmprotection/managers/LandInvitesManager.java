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
import org.bukkit.entity.Player;

import com.google.common.collect.Lists;

import realmprotection.RealmProtection;
import realmprotection.utils.LuckPermsAPI;

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
                String inviter_uuid = result.getString("inviter_uuid");
                String player_uuid = result.getString("player_uuid");
                int role_id = result.getInt("role_id");

                List<Object> data_land_member_cache = Lists.newArrayList(invite_id, land_id, inviter_uuid, player_uuid, role_id);

                cache.put(createCacheKey(land_id, player_uuid), data_land_member_cache);
            }

            statement.close();
        } catch (SQLException error) {
            error.printStackTrace();

            cacheUpdateAll();
        }
    }

    public static void invitePlayerToLand(int land_id, String inviter_uuid, String player_uuid, int role_id) {
        String sql = "INSERT INTO land_invites (land_id, inviter_uuid, player_uuid, role_id) VALUES (?, ?, ?, ?)";

        try {
            Connection connection = RealmProtection.database.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);

            statement.setInt(1, land_id);
            statement.setString(2, inviter_uuid);
            statement.setString(3, player_uuid);
            statement.setInt(4, role_id);

            statement.executeUpdate();
            statement.close();

            cacheUpdateAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void removeInviteFromPlayer(int land_id, String player_uuid) {
        String sql = "DELETE FROM land_invites WHERE land_id = ? AND player_uuid COLLATE NOCASE = ?";

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

    public static void deleteAllInvitesFromLand(int land_id) {
        String sql = "DELETE FROM land_invites WHERE land_id = ?";

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

    public static String getInviteDetail(int land_id, String player_uuid, String variable) {
        if (cache.containsKey(createCacheKey(land_id, player_uuid))) {
            List<Object> data = cache.get(createCacheKey(land_id, player_uuid));

            switch (variable) {
                case "id":
                    return "" + data.get(0);
                case "land_id":
                    return "" + data.get(1);
                case "inviter_uuid":
                    return "" + data.get(2);
                case "player_uuid":
                    return "" + data.get(3);
                case "role_id":
                    return "" + data.get(4);
                default:
                    return null;
            }
        }

        return null;
    }

    public static boolean isPlayerInvited(int land_id, String player_uuid) {
        return cache.containsKey(createCacheKey(land_id, player_uuid));
    }

    public static List<List<String>> listAllInvitesFromLandId(int land_id) {
        List<List<String>> invites = new ArrayList<>();

        for (Map.Entry<String, List<Object>> entry : cache.entrySet()) {
            List<Object> data = entry.getValue();

            if (new Integer((String) data.get(1)) == land_id) {
                invites.add(Lists.newArrayList(Bukkit.getOfflinePlayer(UUID.fromString((String) data.get(2))).getName(), (String) data.get(3))); 
            }
        }

        return invites;
    }

    public static boolean hasPlayerReachedMaxLandTrusts(Player player) {
        RealmProtection plugin = RealmProtection.getPlugin(RealmProtection.class);

        if (!LuckPermsAPI.isReady()) {
            return true;
        }

        String playergroup = LuckPermsAPI.getPlayerGroup(player);
        int groupplayerlandslimit = plugin.getConfig().getInt("ratelimits.player_lands." + playergroup);

        if (groupplayerlandslimit <= 0) {
            groupplayerlandslimit = plugin.getConfig().getInt("ratelimits.player_lands.__DEFAULT__");
        }

        int playerlandscount = LandMembersManager.getPlayerLandsCount(player);

        if (playerlandscount >= groupplayerlandslimit) {
            return true;
        } else {
            return false;
        }
    }


    public static List<List<String>> listAllInvitesForPlayer(String player_uuid) {
        List<List<String>> invites = new ArrayList<>();

        for (Map.Entry<String, List<Object>> entry : cache.entrySet()) {
            List<Object> data = entry.getValue();

            if (((String) data.get(3)).equals(player_uuid)) {
                String land_name = LandsManager.getLandDetailById(new Integer((String) data.get(1)), "land_name");

                invites.add(Lists.newArrayList(Bukkit.getOfflinePlayer(UUID.fromString((String) data.get(2))).getName(), (String) data.get(1), land_name)); 
            }
        }

        return invites;
    }

    private static String createCacheKey(Object land_id, String player_uuid) {
        return land_id + "," + player_uuid;
    }
}
