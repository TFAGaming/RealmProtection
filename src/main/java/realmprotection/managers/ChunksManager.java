package realmprotection.managers;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Particle.DustOptions;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import com.google.common.collect.Lists;

import realmprotection.RealmProtection;
import realmprotection.utils.LoadConfig;
import realmprotection.utils.LuckPermsAPI;

public class ChunksManager {
    private static final Map<String, List<Object>> claimed_chunks_cache = new HashMap<>();

    public static void cacheUpdateAll() {
        String sql = "SELECT * FROM claimed_chunks";

        try {
            claimed_chunks_cache.clear();

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

                claimed_chunks_cache.put(createCacheKey(chunk_x, chunk_z, chunk_world), data);
            }

            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
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
        if (!LuckPermsAPI.isReady()) {
            return true;
        }

        String playergroup = LuckPermsAPI.getPlayerGroup(player);
        int groupchunkslimit = LoadConfig.landsInteger("ratelimits.chunks." + playergroup);

        if (groupchunkslimit <= 0) {
            groupchunkslimit = LoadConfig.landsInteger("ratelimits.chunks.__DEFAULT__");
        }

        String land_id = LandsManager.getLandDetail(player.getName(), "id");
        int landchunkscount = ChunksManager.getChunksCountOfLand(new Integer(land_id));

        if (landchunkscount >= groupchunkslimit) {
            return false;
        } else {
            return true;
        }
    }

    public static boolean isChunkClaimed(Chunk chunk) {
        if (claimed_chunks_cache.containsKey(createCacheKey(chunk.getX(), chunk.getZ(), chunk.getWorld().getName()))) {
            return true;
        }

        String sql = "SELECT COUNT (*) AS count FROM claimed_chunks WHERE chunk_x = ? AND chunk_z = ? AND chunk_world = ?";
        boolean claimed = false;

        try {
            Connection connection = RealmProtection.database.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);

            statement.setInt(1, chunk.getX());
            statement.setInt(2, chunk.getZ());
            statement.setString(3, chunk.getWorld().getName());

            ResultSet rs = statement.executeQuery();

            if (rs.next()) {
                int count = rs.getInt("count");
                claimed = count > 0;
            }

            statement.close();

            cacheUpdateAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return claimed;
    }

    public static boolean isLandHaveAtLeastOneChunk(int land_id) {
        String sql = "SELECT COUNT (*) AS count FROM claimed_chunks WHERE land_id = ?";
        boolean claimed = false;

        try {
            Connection connection = RealmProtection.database.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);

            statement.setInt(1, new Integer(land_id));

            ResultSet rs = statement.executeQuery();

            if (rs.next()) {
                int count = rs.getInt("count");
                claimed = count > 0;
            }

            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return claimed;
    }

    public static String getChunkDetail(Chunk chunk, String variable) {
        String cacheKey = createCacheKey(chunk.getX(), chunk.getZ(), chunk.getWorld().getName());

        if (claimed_chunks_cache.containsKey(cacheKey)) {
            List<Object> data = claimed_chunks_cache.get(cacheKey);

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

        String sql = "SELECT * FROM claimed_chunks WHERE chunk_x = ? AND chunk_z = ? AND chunk_world = ?";

        try {
            Connection connection = RealmProtection.database.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);

            statement.setInt(1, chunk.getX());
            statement.setInt(2, chunk.getZ());
            statement.setString(3, chunk.getWorld().getName());

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

    public static int getChunksCountOfLand(int land_id) {
        String sql = "SELECT COUNT (*) AS count FROM claimed_chunks WHERE land_id = ?";

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

    public static String getOwnerByChunk(Chunk chunk) {
        String land_id = getChunkDetail(chunk, "land_id");

        if (land_id != null) {
            return LandsManager.getLandDetailById(new Integer(land_id), "owner_name");
        } else {
            return null;
        }
    }

    public static List<List<Object>> listChunksFromLandId(int land_id) {
        String sql = "SELECT * FROM claimed_chunks WHERE land_id = ?";
        List<List<Object>> chunks = Lists.newArrayList();

        try {
            Connection connection = RealmProtection.database.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);

            statement.setInt(1, land_id);

            ResultSet result = statement.executeQuery();

            while (result.next()) {
                int chunk_x = new Integer(result.getString("chunk_x"));
                int chunk_z = new Integer(result.getString("chunk_z"));
                String chunk_world = result.getString("chunk_world");

                chunks.add(Lists.newArrayList(chunk_x, chunk_z, chunk_world));
            }

            statement.close();

            return chunks;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return chunks;
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
            String temp_land_owner = LandsManager.getLandDetailById(new Integer(temp_land_id), "owner_name");

            if (!temp_land_owner.equalsIgnoreCase(player.getName()))
                return true;
        }

        if (ChunksManager.isChunkClaimed(south)) {
            String temp_land_id = ChunksManager.getChunkDetail(south, "land_id");
            String temp_land_owner = LandsManager.getLandDetailById(new Integer(temp_land_id), "owner_name");

            if (!temp_land_owner.equalsIgnoreCase(player.getName()))
                return true;
        }

        if (ChunksManager.isChunkClaimed(west)) {
            String temp_land_id = ChunksManager.getChunkDetail(west, "land_id");
            String temp_land_owner = LandsManager.getLandDetailById(new Integer(temp_land_id), "owner_name");

            if (!temp_land_owner.equalsIgnoreCase(player.getName()))
                return true;
        }

        if (ChunksManager.isChunkClaimed(east)) {
            String temp_land_id = ChunksManager.getChunkDetail(east, "land_id");
            String temp_land_owner = LandsManager.getLandDetailById(new Integer(temp_land_id), "owner_name");

            if (!temp_land_owner.equalsIgnoreCase(player.getName()))
                return true;
        }

        return false;
    }

    public static void spawnParticlesAroundChunk(Player player, int land_id, double y, boolean is_owner,
            boolean is_trusted) {
        List<List<Object>> chunks = listChunksFromLandId(land_id);

        for (List<Object> chunk : chunks) {
            World world = player.getWorld();
            int chunkX = (Integer) chunk.get(0);
            int chunkZ = (Integer) chunk.get(1);
            String chunkWorldName = (String) chunk.get(2);
            int minX = chunkX * 16;
            int minZ = chunkZ * 16;

            if (!player.getLocation().getWorld().getName().equals(chunkWorldName))
                continue;

            DustOptions dustoptions;

            if (is_owner) {
                List<String> bordercolor = LoadConfig.landsStringList("border-colors.owner");

                dustoptions = new DustOptions(Color.fromRGB(new Integer(bordercolor.get(0)),
                        new Integer(bordercolor.get(1)), new Integer(bordercolor.get(2))), 1.0F);
            } else if (is_trusted) {
                List<String> bordercolor = LoadConfig.landsStringList("border-colors.trusted");

                dustoptions = new DustOptions(Color.fromRGB(new Integer(bordercolor.get(0)),
                        new Integer(bordercolor.get(1)), new Integer(bordercolor.get(2))), 1.0F);
            } else {
                List<String> bordercolor = LoadConfig.landsStringList("border-colors.visitor");

                dustoptions = new DustOptions(Color.fromRGB(new Integer(bordercolor.get(0)),
                        new Integer(bordercolor.get(1)), new Integer(bordercolor.get(2))), 1.0F);
            }

            Chunk north = world.getChunkAt(chunkX, chunkZ - 1);
            if (!ChunksManager.isChunkClaimed(north)) {
                for (int x = minX; x < minX + 16; x++) {
                    player.spawnParticle(Particle.REDSTONE, x, y, minZ, 5, dustoptions);
                }
            } else {
                String temp_land_id = ChunksManager.getChunkDetail(north, "land_id");
                String temp_land_owner = LandsManager.getLandDetailById(new Integer(temp_land_id), "owner_name");

                if (!temp_land_owner.equalsIgnoreCase(player.getName())) {
                    for (int x = minX; x < minX + 16; x++) {
                        player.spawnParticle(Particle.REDSTONE, x, y, minZ, 5, dustoptions);
                    }
                }
            }

            Chunk south = world.getChunkAt(chunkX, chunkZ + 1);
            if (!ChunksManager.isChunkClaimed(south)) {
                for (int x = minX; x < minX + 16; x++) {
                    player.spawnParticle(Particle.REDSTONE, x, y, minZ + 16, 5, dustoptions);
                }
            } else {
                String temp_land_id = ChunksManager.getChunkDetail(south, "land_id");
                String temp_land_owner = LandsManager.getLandDetailById(new Integer(temp_land_id), "owner_name");

                if (!temp_land_owner.equalsIgnoreCase(player.getName())) {
                    for (int x = minX; x < minX + 16; x++) {
                        player.spawnParticle(Particle.REDSTONE, x, y, minZ + 16, 5, dustoptions);
                    }
                }
            }

            Chunk west = world.getChunkAt(chunkX - 1, chunkZ);
            if (!ChunksManager.isChunkClaimed(west)) {
                for (int z = minZ; z < minZ + 16; z++) {
                    player.spawnParticle(Particle.REDSTONE, minX, y, z, 5, dustoptions);
                }
            } else {
                String temp_land_id = ChunksManager.getChunkDetail(west, "land_id");
                String temp_land_owner = LandsManager.getLandDetailById(new Integer(temp_land_id), "owner_name");

                if (!temp_land_owner.equalsIgnoreCase(player.getName())) {
                    for (int z = minZ; z < minZ + 16; z++) {
                        player.spawnParticle(Particle.REDSTONE, minX, y, z, 5, dustoptions);
                    }
                }
            }

            Chunk east = world.getChunkAt(chunkX + 1, chunkZ);
            if (!ChunksManager.isChunkClaimed(east)) {
                for (int z = minZ; z < minZ + 16; z++) {
                    player.spawnParticle(Particle.REDSTONE, minX + 16, y, z, 5, dustoptions);
                }
            } else {
                String temp_land_id = ChunksManager.getChunkDetail(east, "land_id");
                String temp_land_owner = LandsManager.getLandDetailById(new Integer(temp_land_id), "owner_name");

                if (!temp_land_owner.equalsIgnoreCase(player.getName())) {
                    for (int z = minZ; z < minZ + 16; z++) {
                        player.spawnParticle(Particle.REDSTONE, minX + 16, y, z, 5, dustoptions);
                    }
                }
            }
        }
    }

    public static void startParticleTask(Player player, int land_id, double y, boolean is_owner,
            boolean is_trusted) {
        BukkitTask task = Bukkit.getScheduler().runTaskTimer(RealmProtection.getPlugin(RealmProtection.class), () -> {
            spawnParticlesAroundChunk(player, land_id, player.getLocation().getY() + y, is_owner, is_trusted);
        }, 0L, 15L);

        Bukkit.getScheduler().runTaskLater(RealmProtection.getPlugin(RealmProtection.class),
                () -> cancelParticleTask(task), 60 * 20L);
    }

    private static void cancelParticleTask(BukkitTask task) {
        if (task != null) {
            task.cancel();
            task = null;
        }
    }

    private static String createCacheKey(int chunk_x, int chunk_z, String chunk_world) {
        return chunk_x + "," + chunk_z + "," + chunk_world;
    }
}