package realmprotection.managers;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import realmprotection.RealmProtection;
import realmprotection.utils.DelayedTeleportation;
import realmprotection.utils.LuckPermsAPI;

public class ChunksManager {
    private static final Map<String, List<Object>> cache = new HashMap<>();

    public static void cacheUpdateAll() {
        String sql = "SELECT * FROM claimed_chunks";

        try {
            cache.clear();

            Connection connection = RealmProtection.database.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);

            ResultSet result = statement.executeQuery();

            while (result.next()) {
                int chunk_id = result.getInt("id");
                int chunk_x = result.getInt("chunk_x");
                int chunk_z = result.getInt("chunk_z");
                String chunk_world = result.getString("chunk_world");
                int land_id = result.getInt("land_id");
                long created_at = result.getLong("created_at");

                List<Object> data = new ArrayList<>();

                data.add(chunk_id);
                data.add(chunk_x);
                data.add(chunk_z);
                data.add(chunk_world);
                data.add(land_id);
                data.add(created_at);

                cache.put(createCacheKey(chunk_x, chunk_z, chunk_world), data);
            }

            statement.close();
        } catch (SQLException error) {
            error.printStackTrace();

            cacheUpdateAll();
        }
    }

    public static void claimNewChunk(Chunk chunk, int land_id) {
        String sql = "INSERT INTO claimed_chunks (chunk_x, chunk_z, chunk_world, land_id, created_at) VALUES (?, ?, ?, ?, ?)";

        try {
            Connection connection = RealmProtection.database.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);

            long timestamp = System.currentTimeMillis();

            statement.setInt(1, chunk.getX());
            statement.setInt(2, chunk.getZ());
            statement.setString(3, chunk.getWorld().getName());

            statement.setInt(4, new Integer(land_id));
            statement.setLong(5, timestamp);

            statement.executeUpdate();
            statement.close();

            cacheUpdateAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void removeClaimedChunk(Chunk chunk) {
        String sql = "DELETE FROM claimed_chunks WHERE chunk_x = ? AND chunk_z = ? AND chunk_world = ?";

        try {
            Connection connection = RealmProtection.database.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);

            statement.setInt(1, chunk.getX());
            statement.setInt(2, chunk.getZ());
            statement.setString(3, chunk.getWorld().getName());

            statement.executeUpdate();
            statement.close();

            cacheUpdateAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void deleteAllChunksFromLand(int land_id) {
        String sql = "DELETE FROM claimed_chunks WHERE land_id = ?";

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

    public static boolean hasEnoughChunksToClaim(Player player) {
        RealmProtection plugin = RealmProtection.getPlugin(RealmProtection.class);

        if (!LuckPermsAPI.isReady()) {
            return true;
        }

        String playergroup = LuckPermsAPI.getPlayerGroup(player);
        int groupchunkslimit = plugin.getConfig().getInt("ratelimits.chunks." + playergroup);

        if (groupchunkslimit <= 0) {
            groupchunkslimit = plugin.getConfig().getInt("ratelimits.chunks.__DEFAULT__");
        }

        String land_id = LandsManager.getLandDetail(player.getUniqueId().toString(), "id");
        int landchunkscount = ChunksManager.getChunksCountOfLand(new Integer(land_id));

        if (landchunkscount >= groupchunkslimit) {
            return false;
        } else {
            return true;
        }
    }

    public static boolean isChunkClaimed(Chunk chunk) {
        return cache.containsKey(createCacheKey(chunk.getX(), chunk.getZ(), chunk.getWorld().getName()));
    }

    public static String getChunkDetail(Chunk chunk, String variable) {
        String cacheKey = createCacheKey(chunk.getX(), chunk.getZ(), chunk.getWorld().getName());

        if (cache.containsKey(cacheKey)) {
            List<Object> data = cache.get(cacheKey);

            switch (variable) {
                case "id":
                    return "" + data.get(0);
                case "chunk_x":
                    return "" + data.get(1);
                case "chunk_z":
                    return "" + data.get(2);
                case "chunk_world":
                    return "" + data.get(3);
                case "land_id":
                    return "" + data.get(4);
                case "created_at":
                    return "" + data.get(5);
                default:
                    return null;
            }
        }

        return null;
    }

    public static int getChunksCountOfLand(int land_id) {
        int count = 0;

        for (Map.Entry<String, List<Object>> entry : cache.entrySet()) {
            List<Object> data = entry.getValue();

            if ((int) data.get(4) == land_id) {
                count++;
            }
        }

        return count;
    }

    public static String getOwnerUUIDByChunk(Chunk chunk) {
        String land_id = getChunkDetail(chunk, "land_id");

        if (land_id != null) {
            return LandsManager.getLandDetailById(new Integer(land_id), "owner_uuid");
        } else {
            return null;
        }
    }

    public static List<List<Object>> listChunksFromLandId(int land_id) {
        List<List<Object>> chunks = new ArrayList<>();

        for (Map.Entry<String, List<Object>> entry : cache.entrySet()) {
            List<Object> data = entry.getValue();

            if ((int) data.get(4) == land_id) {
                chunks.add(data.subList(1, 6));
            }
        }

        return chunks;
    }

    public static void teleportPlayerToChunk(Player player, int chunkX, int chunkZ, World world) {
        Location location = new Location(world, chunkX * 16 + 8, 64, chunkZ * 16 + 8);

        location.setY(location.getWorld().getHighestBlockYAt(location) + 1);

        DelayedTeleportation.teleportPlayerWithDelay(player, location, null);
    }

    public static void findUnclaimedChunkPositionAndTeleportPlayer(Player player, int land_id) {
        Chunk chunk = player.getLocation().getChunk();
        World world = player.getWorld();
        int chunkX = chunk.getX();
        int chunkZ = chunk.getZ();
        String chunkWorldName = chunk.getWorld().getName();

        if (!player.getLocation().getWorld().getName().equals(chunkWorldName))
            return;

        Chunk north = world.getChunkAt(chunkX, chunkZ - 1);
        Chunk south = world.getChunkAt(chunkX, chunkZ + 1);
        Chunk west = world.getChunkAt(chunkX - 1, chunkZ);
        Chunk east = world.getChunkAt(chunkX + 1, chunkZ);

        if (!ChunksManager.isChunkClaimed(north)) {
            Location location = new Location(world, north.getX() * 16 + 8, 64, north.getZ() * 16 + 8);

            location.setY(location.getWorld().getHighestBlockYAt(location) + 1);

            player.teleport(location);
        } else if (!ChunksManager.isChunkClaimed(south)) {
            Location location = new Location(world, north.getX() * 16 + 8, 64, north.getZ() * 16 + 8);

            location.setY(location.getWorld().getHighestBlockYAt(location) + 1);

            player.teleport(location);
        } else if (!ChunksManager.isChunkClaimed(west)) {
            Location location = new Location(world, north.getX() * 16 + 8, 64, north.getZ() * 16 + 8);

            location.setY(location.getWorld().getHighestBlockYAt(location) + 1);

            player.teleport(location);
        } else if (!ChunksManager.isChunkClaimed(east)) {
            Location location = new Location(world, north.getX() * 16 + 8, 64, north.getZ() * 16 + 8);

            location.setY(location.getWorld().getHighestBlockYAt(location) + 1);

            player.teleport(location);
        }
    }

    public static boolean isThereNeighborChunkClaimed(Player player) {
        Chunk chunk = player.getLocation().getChunk();
        World world = player.getWorld();
        int chunkX = chunk.getX();
        int chunkZ = chunk.getZ();
        String chunkWorldName = chunk.getWorld().getName();

        if (!player.getLocation().getWorld().getName().equals(chunkWorldName))
            return false;

        Chunk north = world.getChunkAt(chunkX, chunkZ - 1);
        Chunk south = world.getChunkAt(chunkX, chunkZ + 1);
        Chunk west = world.getChunkAt(chunkX - 1, chunkZ);
        Chunk east = world.getChunkAt(chunkX + 1, chunkZ);

        if (ChunksManager.isChunkClaimed(north)) {
            String temp_land_id = ChunksManager.getChunkDetail(north, "land_id");
            String temp_land_owner_uuid = LandsManager.getLandDetailById(new Integer(temp_land_id), "owner_uuid");

            if (!temp_land_owner_uuid.equals(player.getUniqueId().toString()))
                return true;
        }

        if (ChunksManager.isChunkClaimed(south)) {
            String temp_land_id = ChunksManager.getChunkDetail(south, "land_id");
            String temp_land_owner_uuid = LandsManager.getLandDetailById(new Integer(temp_land_id), "owner_uuid");

            if (!temp_land_owner_uuid.equals(player.getUniqueId().toString()))
                return true;
        }

        if (ChunksManager.isChunkClaimed(west)) {
            String temp_land_id = ChunksManager.getChunkDetail(west, "land_id");
            String temp_land_owner_uuid = LandsManager.getLandDetailById(new Integer(temp_land_id), "owner_uuid");

            if (!temp_land_owner_uuid.equalsIgnoreCase(player.getUniqueId().toString()))
                return true;
        }

        if (ChunksManager.isChunkClaimed(east)) {
            String temp_land_id = ChunksManager.getChunkDetail(east, "land_id");
            String temp_land_owner_uuid = LandsManager.getLandDetailById(new Integer(temp_land_id), "owner_uuid");

            if (!temp_land_owner_uuid.equalsIgnoreCase(player.getUniqueId().toString()))
                return true;
        }

        return false;
    }

    private static String createCacheKey(int chunk_x, int chunk_z, String chunk_world) {
        return chunk_x + "," + chunk_z + "," + chunk_world;
    }
}