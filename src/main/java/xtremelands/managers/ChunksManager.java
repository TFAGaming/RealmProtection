package xtremelands.managers;

import java.sql.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Color;
import org.bukkit.Particle;
import org.bukkit.Particle.DustOptions;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import com.google.common.collect.Lists;

import xtremelands.Plugin;

public class ChunksManager {
    private static final Map<String, Object[]> claimedChunksCache = new HashMap<>();

    public static void cacheSetAllClaimedChunks() {
        String sql = "SELECT * FROM claimed_chunks";

        try {
            claimedChunksCache.clear();

            Connection connection = Plugin.database.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);

            ResultSet result = statement.executeQuery();

            while (result.next()) {
                int chunk_x = result.getInt("chunk_x");
                int chunk_z = result.getInt("chunk_z");
                String chunk_world = result.getString("chunk_world");

                Object[] coordinates = { chunk_x, chunk_z, chunk_world };

                claimedChunksCache.put(getChunkCacheKey(chunk_x, chunk_z, chunk_world), coordinates);
            }

            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void claimNewChunk(Chunk chunk, Integer land_id) {
        String sql = "INSERT INTO claimed_chunks (chunk_x, chunk_z, chunk_world, land_id, created_at) VALUES (?, ?, ?, ?, ?)";

        try {
            Connection connection = Plugin.database.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);

            long timestamp = System.currentTimeMillis();

            statement.setInt(1, chunk.getX());
            statement.setInt(2, chunk.getZ());
            statement.setString(3, chunk.getWorld().getName());

            statement.setInt(4, new Integer(land_id));
            statement.setLong(5, timestamp);

            statement.executeUpdate();
            statement.close();

            cacheSetAllClaimedChunks();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void removeClaimedChunk(Chunk chunk) {
        String sql = "DELETE FROM claimed_chunks WHERE chunk_x = ? AND chunk_z = ? AND chunk_world = ?";

        try {
            Connection connection = Plugin.database.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);

            statement.setInt(1, chunk.getX());
            statement.setInt(2, chunk.getZ());
            statement.setString(3, chunk.getWorld().getName());

            statement.executeUpdate();
            statement.close();

            cacheSetAllClaimedChunks();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static boolean isChunkClaimed(Chunk chunk) {
        String cacheKey = getChunkCacheKey(chunk.getX(), chunk.getZ(), chunk.getWorld().getName());

        if (claimedChunksCache.containsKey(cacheKey)) {
            return true;
        }

        String sql = "SELECT COUNT (*) AS count FROM claimed_chunks WHERE chunk_x = ? AND chunk_z = ? AND chunk_world = ?";
        boolean claimed = false;

        try {
            Connection connection = Plugin.database.getConnection();
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
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return claimed;
    }

    public static boolean isLandHaveAtLeastOneChunk(Integer land_id) {
        String sql = "SELECT COUNT (*) AS count FROM claimed_chunks WHERE land_id = ?";
        boolean claimed = false;

        try {
            Connection connection = Plugin.database.getConnection();
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
        String sql = "SELECT * FROM claimed_chunks WHERE chunk_x = ? AND chunk_z = ? AND chunk_world = ?";

        try {
            Connection connection = Plugin.database.getConnection();
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

    public static List<List<Object>> listChunksFromLandId(Integer land_id) {
        String sql = "SELECT * FROM claimed_chunks WHERE land_id = ?";
        List<List<Object>> chunks = Lists.newArrayList();

        try {
            Connection connection = Plugin.database.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);

            statement.setInt(1, land_id);

            ResultSet result = statement.executeQuery();

            while (result.next()) {
                Integer chunk_x = new Integer(result.getString("chunk_x"));
                Integer chunk_z = new Integer(result.getString("chunk_z"));
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

    public static void spawnParticlesAroundChunk(Player player, Integer land_id, double y, boolean is_owner, boolean is_trusted) {
        List<List<Object>> chunks = listChunksFromLandId(land_id);

        for (List<Object> chunk : chunks) {
            World world = player.getWorld();
            Integer chunkX = (Integer) chunk.get(0);
            Integer chunkZ = (Integer) chunk.get(1);
            String chunkWorldName = (String) chunk.get(2);
            Integer minX = chunkX * 16;
            Integer minZ = chunkZ * 16;

            if (!player.getLocation().getWorld().getName().equals(chunkWorldName)) continue;

            DustOptions dustoptions;
            
            if (is_owner) {
                dustoptions = new DustOptions(Color.fromRGB(0, 255, 0), 1.0F); // Green
            } else if (is_trusted) {
                dustoptions = new DustOptions(Color.fromRGB(255, 255, 0), 1.0F); // Yellow
            } else {
                dustoptions = new DustOptions(Color.fromRGB(255, 0, 0), 1.0F); // Red
            }

            Chunk north = world.getChunkAt(chunkX, chunkZ - 1);
            if (!ChunksManager.isChunkClaimed(north)) {
                for (int x = minX; x < minX + 16; x++) {
                    player.spawnParticle(Particle.REDSTONE, x, y, minZ, 5, dustoptions);
                }
            }

            Chunk south = world.getChunkAt(chunkX, chunkZ + 1);
            if (!ChunksManager.isChunkClaimed(south)) {
                for (int x = minX; x < minX + 16; x++) {
                    player.spawnParticle(Particle.REDSTONE, x, y, minZ + 16, 5, dustoptions);
                }
            }

            Chunk west = world.getChunkAt(chunkX - 1, chunkZ);
            if (!ChunksManager.isChunkClaimed(west)) {
                for (int z = minZ; z < minZ + 16; z++) {
                    player.spawnParticle(Particle.REDSTONE, minX, y, z, 5, dustoptions);
                }
            }

            Chunk east = world.getChunkAt(chunkX + 1, chunkZ);
            if (!ChunksManager.isChunkClaimed(east)) {
                for (int z = minZ; z < minZ + 16; z++) {
                    player.spawnParticle(Particle.REDSTONE, minX + 16, y, z, 5, dustoptions);
                }
            }
        }
    }

    public static void startParticleTask(Player player, Integer land_id, double y, boolean is_owner, boolean is_trusted) {
        BukkitTask task = Bukkit.getScheduler().runTaskTimer(Plugin.getPlugin(Plugin.class), () -> {
            spawnParticlesAroundChunk(player, land_id, player.getLocation().getY() + y, is_owner, is_trusted);
        }, 0L, 15L); 

        Bukkit.getScheduler().runTaskLater(Plugin.getPlugin(Plugin.class), () -> cancelParticleTask(task), 60 * 20L);
    }

    public static void cancelParticleTask(BukkitTask task) {
        if (task != null) {
            task.cancel();
            task = null;
        }
    }

    private static String getChunkCacheKey(int chunk_x, int chunk_z, String chunk_world) {
        return chunk_x + "," + chunk_z + "," + chunk_world;
    }
}