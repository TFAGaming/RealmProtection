package realmprotection.managers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.Lists;

import realmprotection.RealmProtection;

public class RolesManager {
    public static void createNewRole(Integer land_id, String role_name, Boolean is_member) {
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
                "permissions_damagehostilemobs, " +
                "permissions_damagepassivemobs, " +
                "permissions_pvp, " +
                "permissions_usecauldron, " +
                "permissions_pickupitems, " +
                "permissions_useanvil, " +
                "permissions_createfire)" +
                "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

        try {
            Connection connection = RealmProtection.database.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);

            if (is_member) {
                statement.setInt(1, land_id);
                statement.setString(2, role_name);
                statement.setBoolean(3, true); // break blocks
                statement.setBoolean(4, true); // Place blocks
                statement.setBoolean(5, true); // Containers
                statement.setBoolean(6, true); // Redstone
                statement.setBoolean(7, true); // Doors
                statement.setBoolean(8, true); // Trapdoors
                statement.setBoolean(9, false); // Edit signs
                statement.setBoolean(10, true); // Empty buckets
                statement.setBoolean(11, true); // Fill buckets
                statement.setBoolean(12, true); // Harvest crops
                statement.setBoolean(13, true); // Frost walker
                statement.setBoolean(14, true); // Shear entities
                statement.setBoolean(15, true); // Item frames
                statement.setBoolean(16, true); // Fences gates
                statement.setBoolean(17, true); // Buttons
                statement.setBoolean(18, true); // Levers
                statement.setBoolean(19, true); // Pressure plates
                statement.setBoolean(20, true); // Bells
                statement.setBoolean(21, true); // Tripwires
                statement.setBoolean(22, true); // Armor stands
                statement.setBoolean(23, true); // Teleport to spawn
                statement.setBoolean(24, true); // Damage hostile mobs
                statement.setBoolean(25, false); // Damage passive mobs
                statement.setBoolean(26, false); // PvP
                statement.setBoolean(27, true); // Use cauldron
                statement.setBoolean(28, true); // Pickup items
                statement.setBoolean(29, true); // Use anvil
                statement.setBoolean(30, false); // Ignite (create fire)
            } else {
                statement.setInt(1, land_id);
                statement.setString(2, role_name);
                statement.setBoolean(3, false); // break blocks
                statement.setBoolean(4, false); // Place blocks
                statement.setBoolean(5, false); // Containers
                statement.setBoolean(6, false); // Redstone
                statement.setBoolean(7, false); // Doors
                statement.setBoolean(8, false); // Trapdoors
                statement.setBoolean(9, false); // Edit signs
                statement.setBoolean(10, false); // Empty buckets
                statement.setBoolean(11, false); // Fill buckets
                statement.setBoolean(12, false); // Harvest crops
                statement.setBoolean(13, false); // Frost walker
                statement.setBoolean(14, false); // Shear entities
                statement.setBoolean(15, false); // Item frames
                statement.setBoolean(16, false); // Fences gates
                statement.setBoolean(17, false); // Buttons
                statement.setBoolean(18, false); // Levers
                statement.setBoolean(19, false); // Pressure plates
                statement.setBoolean(20, false); // Bells
                statement.setBoolean(21, false); // Tripwires
                statement.setBoolean(22, false); // Armor stands
                statement.setBoolean(23, false); // Teleport to spawn
                statement.setBoolean(24, false); // Damage hostile mobs
                statement.setBoolean(25, false); // Damage passive mobs
                statement.setBoolean(26, false); // PvP
                statement.setBoolean(27, false); // Use cauldron
                statement.setBoolean(28, true); // Pickup items
                statement.setBoolean(29, false); // Use anvil
                statement.setBoolean(30, false); // Ignite (create fire)
            }

            statement.executeUpdate();
            statement.close();
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
                allflags.add(Lists.newArrayList("damagehostilemobs", result.getBoolean("permissions_damagehostilemobs"))); 
                allflags.add(Lists.newArrayList("damagepassivemobs", result.getBoolean("permissions_damagepassivemobs"))); 
                allflags.add(Lists.newArrayList("pvp", result.getBoolean("permissions_pvp"))); 
                allflags.add(Lists.newArrayList("usecauldron", result.getBoolean("permissions_usecauldron"))); 
                allflags.add(Lists.newArrayList("pickupitems", result.getBoolean("permissions_pickupitems"))); 
                allflags.add(Lists.newArrayList("useanvil", result.getBoolean("permissions_useanvil"))); 
                allflags.add(Lists.newArrayList("createfire", result.getBoolean("permissions_createfire"))); 
            }

            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return allflags;
    }

    public static String getRoleDetail(Integer land_id, String role_name, String variable) {
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
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }
}
