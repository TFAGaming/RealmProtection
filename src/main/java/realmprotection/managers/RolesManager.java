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

public class RolesManager {
    private static final Map<String, List<Object>> role_id_cache = new HashMap<>();
    private static final Map<String, List<Object>> land_id_and_role_name_cache = new HashMap<>();
    private static final Map<String, List<Boolean>> role_id_flags_cache = new HashMap<>();
    private static final Map<String, List<Boolean>> land_id_and_role_name_flags_cache = new HashMap<>();

    public static void cacheUpdateAll() {
        String sql = "SELECT * FROM land_roles";

        try {
            role_id_cache.clear();
            land_id_and_role_name_cache.clear();
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
                boolean permissions_fencegates = result.getBoolean("permissions_fencegates");
                boolean permissions_buttons = result.getBoolean("permissions_buttons");
                boolean permissions_levers = result.getBoolean("permissions_levers");
                boolean permissions_pressureplates = result.getBoolean("permissions_pressureplates");
                boolean permissions_bells = result.getBoolean("permissions_bells");
                boolean permissions_tripwires = result.getBoolean("permissions_tripwires");
                boolean permissions_armorstands = result.getBoolean("permissions_armorstands");
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
                List<Boolean> data_role_id_flags_cache = Lists.newArrayList(
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
                        permissions_fencegates,
                        permissions_buttons,
                        permissions_levers,
                        permissions_pressureplates,
                        permissions_bells,
                        permissions_tripwires,
                        permissions_armorstands,
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
                        permissions_usevehicles);

                role_id_cache.put("" + role_id, data_role_cache);
                land_id_and_role_name_cache.put(land_id + "," + role_name, data_role_cache);
                role_id_flags_cache.put("" + role_id, data_role_id_flags_cache);
                land_id_and_role_name_flags_cache.put(land_id + "," + role_name, data_role_id_flags_cache);
            }

            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void createNewRole(Integer land_id, String role_name) {
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
                "permissions_fencegates, " +
                "permissions_buttons, " +
                "permissions_levers, " +
                "permissions_pressureplates, " +
                "permissions_bells, " +
                "permissions_tripwires, " +
                "permissions_armorstands, " +
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
                "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

        try {
            Connection connection = RealmProtection.database.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);

            List<Boolean> datapermissions = LoadConfig.landRolesDefaultBooleanList("permissions." + role_name);

            statement.setInt(1, land_id);
            statement.setString(2, role_name);

            if (datapermissions.size() > 0) {
                for (int i = 3; i < 34; i++) {
                    statement.setBoolean(i, datapermissions.get(i - 3));
                }
            } else {
                datapermissions = LoadConfig.landRolesDefaultBooleanList("permissions.__default__");

                for (int i = 3; i < 34; i++) {
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

    public static void deleteRole(Integer land_id, String role_name) {
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

    public static void deleteAllRolesFromLand(Integer land_id) {
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

    public static Integer countRolesFromLand(Integer land_id) {
        String sql = "SELECT COUNT (*) AS count FROM land_roles WHERE land_id = ?";
        Integer counted = 0;

        try {
            Connection connection = RealmProtection.database.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);

            statement.setInt(1, new Integer(land_id));

            ResultSet rs = statement.executeQuery();

            if (rs.next()) {
                Integer count = rs.getInt("count");
                counted = count;
            }

            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return counted;
    }

    public static String getRoleDetail(Integer land_id, String role_name, String variable) {
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

        String sql = "SELECT * FROM land_roles WHERE land_id = ? AND role_name = ?";

        try {
            Connection connection = RealmProtection.database.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);

            statement.setInt(1, land_id);
            statement.setString(2, role_name);

            ResultSet result = statement.executeQuery();

            if (result.next()) {
                String string = result.getString(variable);

                return string;
            }

            statement.close();

            cacheUpdateAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static boolean hasRole(Integer land_id, String role_name) {
        String sql = "SELECT COUNT (*) AS count FROM land_roles WHERE land_id = ? AND role_name = ?";
        boolean exists = false;

        try {
            Connection connection = RealmProtection.database.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);

            statement.setInt(1, land_id);
            statement.setString(2, role_name);

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

    public static boolean getPermissionValue(Integer land_id, String role_name, String permission_name) {
        if (land_id_and_role_name_flags_cache.containsKey(land_id + "," + role_name)) {
            List<Boolean> data = land_id_and_role_name_flags_cache.get(land_id + "," + role_name);

            switch ("permissions_" + permission_name) {
                case "permissions_breakblocks":
                    return data.get(0);
                case "permissions_placeblocks":
                    return data.get(1);
                case "permissions_containers":
                    return data.get(2);
                case "permissions_redstone":
                    return data.get(3);
                case "permissions_doors":
                    return data.get(4);
                case "permissions_trapdoors":
                    return data.get(5);
                case "permissions_editsigns":
                    return data.get(6);
                case "permissions_emptybuckets":
                    return data.get(7);
                case "permissions_fillbuckets":
                    return data.get(8);
                case "permissions_harvestcrops":
                    return data.get(9);
                case "permissions_frostwalker":
                    return data.get(10);
                case "permissions_shearentities":
                    return data.get(11);
                case "permissions_itemframes":
                    return data.get(12);
                case "permissions_fencegates":
                    return data.get(13);
                case "permissions_buttons":
                    return data.get(14);
                case "permissions_levers":
                    return data.get(15);
                case "permissions_pressureplates":
                    return data.get(16);
                case "permissions_bells":
                    return data.get(17);
                case "permissions_tripwires":
                    return data.get(18);
                case "permissions_armorstands":
                    return data.get(19);
                case "permissions_teleporttospawn":
                    return data.get(20);
                case "permissions_throwenderpearls":
                    return data.get(21);
                case "permissions_throwpotions":
                    return data.get(22);
                case "permissions_damagehostilemobs":
                    return data.get(23);
                case "permissions_damagepassivemobs":
                    return data.get(24);
                case "permissions_pvp":
                    return data.get(25);
                case "permissions_usecauldron":
                    return data.get(26);
                case "permissions_pickupitems":
                    return data.get(27);
                case "permissions_useanvil":
                    return data.get(28);
                case "permissions_createfire":
                    return data.get(29);
                case "permissions_usevehicles":
                    return data.get(30);
                default:
                    return false;
            }
        }

        String sql = "SELECT * FROM land_roles WHERE land_id = ? AND role_name = ?";

        try {
            Connection connection = RealmProtection.database.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);

            statement.setInt(1, land_id);
            statement.setString(2, role_name);

            ResultSet result = statement.executeQuery();

            if (result.next()) {
                Boolean value = result.getBoolean("permissions_" + permission_name);

                return value;
            }

            statement.close();

            cacheUpdateAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public static void updatePermissionValue(Integer land_id, String role_name, String permission_name,
            Boolean value) {
        String sql = "UPDATE land_roles SET permissions_" + permission_name + "=" + value
                + " WHERE land_id = ? AND role_name = ?";

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

    public static void updateRoleName(Integer land_id, Integer role_id, String new_role_name) {
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

    public static List<String> listAllRolesNames(Integer land_id) {
        String sql = "SELECT * FROM land_roles WHERE land_id = ?";

        List<String> rolenames = new ArrayList<>();

        try {
            Connection connection = RealmProtection.database.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);

            statement.setInt(1, land_id);

            ResultSet result = statement.executeQuery();

            while (result.next()) {
                String role_name = result.getString("role_name");

                rolenames.add(role_name);
            }

            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return rolenames;
    }

    public static List<List<Object>> listEnabledAndDisabledFlagsForRole(Integer land_id, String role_name) {
        String sql = "SELECT * FROM land_roles WHERE land_id = ? AND role_name = ?";

        List<List<Object>> allflags = new ArrayList<>();

        try {
            Connection connection = RealmProtection.database.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);

            statement.setInt(1, land_id);
            statement.setString(2, role_name);

            ResultSet result = statement.executeQuery();

            while (result.next()) {
                allflags.add(Lists.newArrayList("breakblocks", result.getBoolean("permissions_breakblocks")));
                allflags.add(Lists.newArrayList("placeblocks", result.getBoolean("permissions_placeblocks")));
                allflags.add(Lists.newArrayList("containers", result.getBoolean("permissions_containers")));
                allflags.add(Lists.newArrayList("redstone", result.getBoolean("permissions_redstone")));
                allflags.add(Lists.newArrayList("doors", result.getBoolean("permissions_doors")));
                allflags.add(Lists.newArrayList("trapdoors", result.getBoolean("permissions_trapdoors")));
                allflags.add(Lists.newArrayList("editsigns", result.getBoolean("permissions_editsigns")));
                allflags.add(Lists.newArrayList("emptybuckets", result.getBoolean("permissions_emptybuckets")));
                allflags.add(Lists.newArrayList("fillbuckets", result.getBoolean("permissions_fillbuckets")));
                allflags.add(Lists.newArrayList("harvestcrops", result.getBoolean("permissions_harvestcrops")));
                allflags.add(Lists.newArrayList("frostwalker", result.getBoolean("permissions_frostwalker")));
                allflags.add(Lists.newArrayList("shearentities", result.getBoolean("permissions_shearentities")));
                allflags.add(Lists.newArrayList("itemframes", result.getBoolean("permissions_itemframes")));
                allflags.add(Lists.newArrayList("fencegates", result.getBoolean("permissions_fencegates")));
                allflags.add(Lists.newArrayList("buttons", result.getBoolean("permissions_buttons")));
                allflags.add(Lists.newArrayList("levers", result.getBoolean("permissions_levers")));
                allflags.add(Lists.newArrayList("pressureplates", result.getBoolean("permissions_pressureplates")));
                allflags.add(Lists.newArrayList("bells", result.getBoolean("permissions_bells")));
                allflags.add(Lists.newArrayList("tripwires", result.getBoolean("permissions_tripwires")));
                allflags.add(Lists.newArrayList("armorstands", result.getBoolean("permissions_armorstands")));
                allflags.add(Lists.newArrayList("teleporttospawn", result.getBoolean("permissions_teleporttospawn")));
                allflags.add(Lists.newArrayList("throwenderpearls", result.getBoolean("permissions_throwenderpearls")));
                allflags.add(Lists.newArrayList("throwpotions", result.getBoolean("permissions_throwpotions")));
                allflags.add(
                        Lists.newArrayList("damagehostilemobs", result.getBoolean("permissions_damagehostilemobs")));
                allflags.add(
                        Lists.newArrayList("damagepassivemobs", result.getBoolean("permissions_damagepassivemobs")));
                allflags.add(Lists.newArrayList("pvp", result.getBoolean("permissions_pvp")));
                allflags.add(Lists.newArrayList("usecauldron", result.getBoolean("permissions_usecauldron")));
                allflags.add(Lists.newArrayList("pickupitems", result.getBoolean("permissions_pickupitems")));
                allflags.add(Lists.newArrayList("useanvil", result.getBoolean("permissions_useanvil")));
                allflags.add(Lists.newArrayList("createfire", result.getBoolean("permissions_createfire")));
                allflags.add(Lists.newArrayList("usevehicles", result.getBoolean("permissions_usevehicles")));
            }

            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return allflags;
    }
}
