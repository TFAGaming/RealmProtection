package realmprotection.managers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.entity.Player;

import com.google.common.collect.Lists;

import realmprotection.RealmProtection;
import realmprotection.utils.LuckPermsAPI;

public class RolesManager {
    private static final Map<String, List<Object>> role_id_cache = new HashMap<>();
    private static final Map<String, List<Object>> land_id_and_role_name_cache = new HashMap<>();
    private static final Map<String, List<Object>> land_id_and_role_id_cache = new HashMap<>();
    private static final Map<String, List<Object>> role_id_flags_cache = new HashMap<>();
    private static final Map<String, List<Object>> land_id_and_role_name_flags_cache = new HashMap<>();

    public static void cacheUpdateAll() {
        String sql = "SELECT * FROM land_roles";

        try {
            role_id_cache.clear();
            land_id_and_role_name_cache.clear();
            land_id_and_role_id_cache.clear();
            role_id_flags_cache.clear();
            land_id_and_role_name_flags_cache.clear();

            Connection connection = RealmProtection.database.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);

            ResultSet result = statement.executeQuery();

            while (result.next()) {
                int role_id = result.getInt("id");
                String land_id = result.getString("land_id");
                String role_name = result.getString("role_name");

                boolean permissions_breakblocks = result.getBoolean("permissions_breakblocks");
                boolean permissions_placeblocks = result.getBoolean("permissions_placeblocks");
                boolean permissions_containers = result.getBoolean("permissions_containers");
                boolean permissions_redstone = result.getBoolean("permissions_redstone");
                boolean permissions_doors = result.getBoolean("permissions_doors");
                boolean permissions_trapdoors = result.getBoolean("permissions_trapdoors");
                boolean permissions_editsigns = result.getBoolean("permissions_editsigns");
                boolean permissions_emptybuckets = result.getBoolean("permissions_emptybuckets");
                boolean permissions_fillbuckets = result.getBoolean("permissions_fillbuckets");
                boolean permissions_harvestcrops = result.getBoolean("permissions_harvestcrops");
                boolean permissions_frostwalker = result.getBoolean("permissions_frostwalker");
                boolean permissions_shearentities = result.getBoolean("permissions_shearentities");
                boolean permissions_itemframes = result.getBoolean("permissions_itemframes");
                boolean permissions_generalinteractions = result.getBoolean("permissions_generalinteractions");
                boolean permissions_fencegates = result.getBoolean("permissions_fencegates");
                boolean permissions_buttons = result.getBoolean("permissions_buttons");
                boolean permissions_levers = result.getBoolean("permissions_levers");
                boolean permissions_pressureplates = result.getBoolean("permissions_pressureplates");
                boolean permissions_bells = result.getBoolean("permissions_bells");
                boolean permissions_tripwires = result.getBoolean("permissions_tripwires");
                boolean permissions_armorstands = result.getBoolean("permissions_armorstands");
                boolean permissions_dyemobs = result.getBoolean("permissions_dyemobs");
                boolean permissions_renamemobs = result.getBoolean("permissions_renamemobs");
                boolean permissions_leashmobs = result.getBoolean("permissions_leashmobs");
                boolean permissions_tradewithvillagers = result.getBoolean("permissions_tradewithvillagers");
                boolean permissions_teleporttospawn = result.getBoolean("permissions_teleporttospawn");
                boolean permissions_throwenderpearls = result.getBoolean("permissions_throwenderpearls");
                boolean permissions_throwpotions = result.getBoolean("permissions_throwpotions");
                boolean permissions_damagehostilemobs = result.getBoolean("permissions_damagehostilemobs");
                boolean permissions_damagepassivemobs = result.getBoolean("permissions_damagepassivemobs");
                boolean permissions_pvp = result.getBoolean("permissions_pvp");
                boolean permissions_usecauldron = result.getBoolean("permissions_usecauldron");
                boolean permissions_pickupitems = result.getBoolean("permissions_pickupitems");
                boolean permissions_useanvil = result.getBoolean("permissions_useanvil");
                boolean permissions_createfire = result.getBoolean("permissions_createfire");
                boolean permissions_usevehicles = result.getBoolean("permissions_usevehicles");

                List<Object> data_role_cache = Lists.newArrayList(role_id, land_id, role_name);
                List<Object> data_role_id_flags_cache = Lists.newArrayList(
                        permissions_breakblocks,
                        permissions_placeblocks,
                        permissions_containers,
                        permissions_redstone,
                        permissions_doors,
                        permissions_trapdoors,
                        permissions_editsigns,
                        permissions_emptybuckets,
                        permissions_fillbuckets,
                        permissions_harvestcrops,
                        permissions_frostwalker,
                        permissions_shearentities,
                        permissions_itemframes,
                        permissions_generalinteractions,
                        permissions_fencegates,
                        permissions_buttons,
                        permissions_levers,
                        permissions_pressureplates,
                        permissions_bells,
                        permissions_tripwires,
                        permissions_armorstands,
                        permissions_dyemobs,
                        permissions_renamemobs,
                        permissions_leashmobs,
                        permissions_tradewithvillagers,
                        permissions_teleporttospawn,
                        permissions_throwenderpearls,
                        permissions_throwpotions,
                        permissions_damagehostilemobs,
                        permissions_damagepassivemobs,
                        permissions_pvp,
                        permissions_usecauldron,
                        permissions_pickupitems,
                        permissions_useanvil,
                        permissions_createfire,
                        permissions_usevehicles,
                        land_id,
                        role_name);

                role_id_cache.put("" + role_id, data_role_cache);
                land_id_and_role_name_cache.put(land_id + "," + role_name, data_role_cache);
                land_id_and_role_id_cache.put(land_id + "," + role_id, data_role_cache);
                role_id_flags_cache.put("" + role_id, data_role_id_flags_cache);
                land_id_and_role_name_flags_cache.put(land_id + "," + role_name, data_role_id_flags_cache);
            }

            statement.close();
        } catch (SQLException error) {
            error.printStackTrace();

            cacheUpdateAll();
        }
    }

    public static void createNewRole(int land_id, String role_name) {
        String sql = "INSERT INTO land_roles (" +
                "land_id, " +
                "role_name, " +
                "permissions_breakblocks, " +
                "permissions_placeblocks, " +
                "permissions_containers, " +
                "permissions_redstone, " +
                "permissions_doors, " +
                "permissions_trapdoors, " +
                "permissions_editsigns, " +
                "permissions_emptybuckets," +
                "permissions_fillbuckets," +
                "permissions_harvestcrops, " +
                "permissions_frostwalker, " +
                "permissions_shearentities, " +
                "permissions_itemframes, " +
                "permissions_generalinteractions, " +
                "permissions_fencegates, " +
                "permissions_buttons, " +
                "permissions_levers, " +
                "permissions_pressureplates, " +
                "permissions_bells, " +
                "permissions_tripwires, " +
                "permissions_armorstands, " +
                "permissions_dyemobs, " +
                "permissions_renamemobs, " +
                "permissions_leashmobs, " +
                "permissions_tradewithvillagers, " +
                "permissions_teleporttospawn, " +
                "permissions_throwenderpearls, " +
                "permissions_throwpotions, " +
                "permissions_damagehostilemobs, " +
                "permissions_damagepassivemobs, " +
                "permissions_pvp, " +
                "permissions_usecauldron, " +
                "permissions_pickupitems, " +
                "permissions_useanvil, " +
                "permissions_createfire, " +
                "permissions_usevehicles) " +
                "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

        try {
            Connection connection = RealmProtection.database.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);

            RealmProtection plugin = RealmProtection.getPlugin(RealmProtection.class);

            List<Boolean> datapermissions = plugin.getConfig().getBooleanList("roles.permissions." + role_name);

            statement.setInt(1, land_id);
            statement.setString(2, role_name);

            if (datapermissions.size() > 0) {
                for (int i = 3; i < 39; i++) {
                    statement.setBoolean(i, datapermissions.get(i - 3));
                }
            } else {
                datapermissions = plugin.getConfig().getBooleanList("roles.permissions.__DEFAULT__");

                for (int i = 3; i < 39; i++) {
                    statement.setBoolean(i, datapermissions.get(i - 3));
                }
            }

            statement.executeUpdate();
            statement.close();

            cacheUpdateAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void deleteRole(int land_id, String role_name) {
        String sql = "DELETE FROM land_roles WHERE land_id = ? AND role_name = ?";

        try {
            Connection connection = RealmProtection.database.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);

            statement.setInt(1, land_id);
            statement.setString(2, role_name);

            statement.executeUpdate();
            statement.close();

            cacheUpdateAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void deleteAllRolesFromLand(int land_id) {
        String sql = "DELETE FROM land_roles WHERE land_id = ?";

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

    public static int countRolesFromLand(int land_id) {
        int count = 0;

        for (Map.Entry<String, List<Object>> entry : role_id_cache.entrySet()) {
            List<Object> data = entry.getValue();

            if (new Integer((String) data.get(1)) == land_id) {
                count++;
            }
        }

        return count;
    }

    public static String getRoleDetail(int land_id, String role_name, String variable) {
        if (land_id_and_role_name_cache.containsKey(land_id + "," + role_name)) {
            List<Object> data = land_id_and_role_name_cache.get(land_id + "," + role_name);

            switch (variable) {
                case "id":
                    return "" + data.get(0);
                case "land_id":
                    return "" + data.get(1);
                case "role_name":
                    return "" + data.get(2);
                default:
                    return null;
            }
        }

        return null;
    }

    public static String getRoleDetailById(int land_id, int role_id, String variable) {
        if (land_id_and_role_id_cache.containsKey(land_id + "," + role_id)) {
            List<Object> data = land_id_and_role_id_cache.get(land_id + "," + role_id);

            switch (variable) {
                case "id":
                    return "" + data.get(0);
                case "land_id":
                    return "" + data.get(1);
                case "role_name":
                    return "" + data.get(2);
                default:
                    return null;
            }
        }

        return null;
    }

    public static boolean hasRole(int land_id, String role_name) {
        boolean value = false;

        for (Map.Entry<String, List<Object>> entry : role_id_cache.entrySet()) {
            List<Object> data = entry.getValue();

            if (new Integer((String) data.get(1)) == land_id && ((String) data.get(2)).equals(role_name)) {
                value = true;
                break;
            }
        }

        return value;
    }

    public static boolean hasReachedMaximumRolesCountForLand(Player player) {
        if (!LuckPermsAPI.isReady()) {
            return true;
        }

        RealmProtection plugin = RealmProtection.getPlugin(RealmProtection.class);

        String playergroup = LuckPermsAPI.getPlayerGroup(player);
        int grouproleslimit = plugin.getConfig().getInt("ratelimits.roles." + playergroup);

        if (grouproleslimit <= 0) {
            grouproleslimit = plugin.getConfig().getInt("ratelimits.roles.__DEFAULT__");
        }

        String land_id = LandsManager.getLandDetail(player.getUniqueId().toString(), "id");
        int landrolescount = listAllRolesNames(new Integer(land_id)).size();

        if (landrolescount >= grouproleslimit) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean getPermissionValue(int land_id, String role_name, String permission_name) {
        if (land_id_and_role_name_flags_cache.containsKey(land_id + "," + role_name)) {
            List<Object> data = land_id_and_role_name_flags_cache.get(land_id + "," + role_name);

            switch ("permissions_" + permission_name) {
                case "permissions_breakblocks":
                    return (Boolean) data.get(0);
                case "permissions_placeblocks":
                    return (Boolean) data.get(1);
                case "permissions_containers":
                    return (Boolean) data.get(2);
                case "permissions_redstone":
                    return (Boolean) data.get(3);
                case "permissions_doors":
                    return (Boolean) data.get(4);
                case "permissions_trapdoors":
                    return (Boolean) data.get(5);
                case "permissions_editsigns":
                    return (Boolean) data.get(6);
                case "permissions_emptybuckets":
                    return (Boolean) data.get(7);
                case "permissions_fillbuckets":
                    return (Boolean) data.get(8);
                case "permissions_harvestcrops":
                    return (Boolean) data.get(9);
                case "permissions_frostwalker":
                    return (Boolean) data.get(10);
                case "permissions_shearentities":
                    return (Boolean) data.get(11);
                case "permissions_itemframes":
                    return (Boolean) data.get(12);
                case "permissions_generalinteractions":
                    return (Boolean) data.get(13);
                case "permissions_fencegates":
                    return (Boolean) data.get(14);
                case "permissions_buttons":
                    return (Boolean) data.get(15);
                case "permissions_levers":
                    return (Boolean) data.get(16);
                case "permissions_pressureplates":
                    return (Boolean) data.get(17);
                case "permissions_bells":
                    return (Boolean) data.get(18);
                case "permissions_tripwires":
                    return (Boolean) data.get(19);
                case "permissions_armorstands":
                    return (Boolean) data.get(20);
                case "permissions_dyemobs":
                    return (Boolean) data.get(21);
                case "permissions_renamemobs":
                    return (Boolean) data.get(22);
                case "permissions_leashmobs":
                    return (Boolean) data.get(23);
                case "permissions_tradewithvillagers":
                    return (Boolean) data.get(24);
                case "permissions_teleporttospawn":
                    return (Boolean) data.get(25);
                case "permissions_throwenderpearls":
                    return (Boolean) data.get(26);
                case "permissions_throwpotions":
                    return (Boolean) data.get(27);
                case "permissions_damagehostilemobs":
                    return (Boolean) data.get(28);
                case "permissions_damagepassivemobs":
                    return (Boolean) data.get(29);
                case "permissions_pvp":
                    return (Boolean) data.get(30);
                case "permissions_usecauldron":
                    return (Boolean) data.get(31);
                case "permissions_pickupitems":
                    return (Boolean) data.get(32);
                case "permissions_useanvil":
                    return (Boolean) data.get(33);
                case "permissions_createfire":
                    return (Boolean) data.get(34);
                case "permissions_usevehicles":
                    return (Boolean) data.get(35);
                default:
                    return false;
            }
        }

        return false;
    }

    public static void updatePermissionValue(int land_id, String role_name, String permission_name, boolean value) {
        String sql = "UPDATE land_roles SET permissions_" + permission_name + "=" + value
                + " WHERE land_id = ? AND role_name COLLATE NOCASE = ?";

        try {
            Connection connection = RealmProtection.database.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);

            statement.setInt(1, land_id);
            statement.setString(2, role_name);

            statement.execute();

            statement.close();

            cacheUpdateAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return;
    }

    public static void updateRoleName(int land_id, int role_id, String new_role_name) {
        String sql = "UPDATE land_roles SET role_name='" + new_role_name + "' WHERE land_id = ? AND id = ?";

        try {
            Connection connection = RealmProtection.database.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);

            statement.setInt(1, land_id);
            statement.setInt(2, role_id);

            statement.execute();

            statement.close();

            cacheUpdateAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return;
    }

    public static List<String> listAllRolesNames(int land_id) {
        List<String> role_names = new ArrayList<>();

        for (Map.Entry<String, List<Object>> entry : role_id_cache.entrySet()) {
            List<Object> data = entry.getValue();

            if (new Integer((String) data.get(1)) == land_id) {
                role_names.add((String) data.get(2));
            }
        }

        return role_names;
    }

    public static List<List<Object>> listEnabledAndDisabledFlagsForRole(int land_id, String role_name) {
        List<List<Object>> allflags = new ArrayList<>();
        List<String> all_permissions = listAllPermissions();

        for (Map.Entry<String, List<Object>> entry : role_id_flags_cache.entrySet()) {
            List<Object> data = entry.getValue();

            if (new Integer((String) data.get(data.size() - 2)) == land_id && ((String) data.get(data.size() - 1)).equals(role_name)) {
                for (int index = 0; index < data.size() - 2; index++) {
                    allflags.add(Lists.newArrayList(
                            all_permissions.get(index), (Boolean) data.get(index)));
                }

                break;
            }
        }

        return allflags;
    }

    public static List<String> listAllPermissions() {
        List<String> arraylist = new ArrayList<>();

        arraylist.add("breakblocks");
        arraylist.add("placeblocks");
        arraylist.add("containers");
        arraylist.add("redstone");
        arraylist.add("doors");
        arraylist.add("trapdoors");
        arraylist.add("editsigns");
        arraylist.add("emptybuckets");
        arraylist.add("fillbuckets");
        arraylist.add("harvestcrops");
        arraylist.add("frostwalker");
        arraylist.add("shearentities");
        arraylist.add("itemframes");
        arraylist.add("generalinteractions");
        arraylist.add("fencegates");
        arraylist.add("buttons");
        arraylist.add("levers");
        arraylist.add("pressureplates");
        arraylist.add("bells");
        arraylist.add("tripwires");
        arraylist.add("armorstands");
        arraylist.add("dyemobs");
        arraylist.add("renamemobs");
        arraylist.add("leashmobs");
        arraylist.add("tradewithvillagers");
        arraylist.add("teleporttospawn");
        arraylist.add("throwenderpearls");
        arraylist.add("throwpotions");
        arraylist.add("damagehostilemobs");
        arraylist.add("damagepassivemobs");
        arraylist.add("pvp");
        arraylist.add("usecauldron");
        arraylist.add("pickupitems");
        arraylist.add("useanvil");
        arraylist.add("createfire");
        arraylist.add("usevehicles");

        return arraylist;
    }
}
