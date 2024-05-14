package realmprotection.utils;
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

import realmprotection.RealmProtection;
import realmprotection.managers.ChunksManager;

public class ParticleSpawner {
    private static final Map<String, BukkitTask> particles_players_cache = new HashMap<>();

    public static void spawnTemporarySmokeParticle(World world, double x, double y, double z, int count, double offsetX, double offsetY, double offsetZ, double extra) {
        Location location = new Location(world, x, y, z);
        
        world.spawnParticle(Particle.SMOKE, location, count, offsetX, offsetY, offsetZ, extra);
    }

    public static void spawnDelayedParticlesAroundClaimedChunk(Player player, int land_id, double y, boolean is_owner, boolean is_trusted) {
        if (particles_players_cache.containsKey(player.getName())) {
            BukkitTask taskFromMap = particles_players_cache.get(player.getName());

            cancelTaskFromDelayedParticlesAroundChunk(taskFromMap, player.getName());
        }

        BukkitTask task = Bukkit.getScheduler().runTaskTimer(RealmProtection.getPlugin(RealmProtection.class), () -> {
            spawnParticlesAroundChunk(player, land_id, player.getLocation().getY() + y, is_owner, is_trusted);
        }, 0L, 15L);

        particles_players_cache.put(player.getName(), task);

        Bukkit.getScheduler().runTaskLater(RealmProtection.getPlugin(RealmProtection.class),
                () -> cancelTaskFromDelayedParticlesAroundChunk(task, player.getName()), 60 * 20L);
    }

    private static void spawnParticlesAroundChunk(Player player, int land_id, double y, boolean is_owner,
            boolean is_trusted) {
        List<List<Object>> chunks = ChunksManager.listChunksFromLandId(land_id);

        for (List<Object> chunk : chunks) {
            World world = player.getWorld();
            int chunkX = (Integer) chunk.get(0);
            int chunkZ = (Integer) chunk.get(1);
            String chunkWorldName = (String) chunk.get(2);
            int minX = chunkX * 16;
            int minZ = chunkZ * 16;

            if (!player.getLocation().getWorld().getName().equals(chunkWorldName))
                continue;

            RealmProtection plugin = RealmProtection.getPlugin(RealmProtection.class);

            DustOptions dustoptions;

            if (is_owner) {
                List<String> bordercolor = plugin.getConfig().getStringList("border_colors.owner");

                dustoptions = new DustOptions(Color.fromRGB(new Integer(bordercolor.get(0)),
                        new Integer(bordercolor.get(1)), new Integer(bordercolor.get(2))), 1.0F);
            } else if (is_trusted) {
                List<String> bordercolor = plugin.getConfig().getStringList("border_colors.trusted");

                dustoptions = new DustOptions(Color.fromRGB(new Integer(bordercolor.get(0)),
                        new Integer(bordercolor.get(1)), new Integer(bordercolor.get(2))), 1.0F);
            } else {
                List<String> bordercolor = plugin.getConfig().getStringList("border_colors.visitor");

                dustoptions = new DustOptions(Color.fromRGB(new Integer(bordercolor.get(0)),
                        new Integer(bordercolor.get(1)), new Integer(bordercolor.get(2))), 1.0F);
            }

            Chunk north = world.getChunkAt(chunkX, chunkZ - 1);
            if (!ChunksManager.isChunkClaimed(north)) {
                for (int x = minX; x < minX + 16; x++) {
                    player.spawnParticle(Particle.DUST, x, y, minZ, 5, dustoptions);
                }
            }

            Chunk south = world.getChunkAt(chunkX, chunkZ + 1);
            if (!ChunksManager.isChunkClaimed(south)) {
                for (int x = minX; x < minX + 16; x++) {
                    player.spawnParticle(Particle.DUST, x, y, minZ + 16, 5, dustoptions);
                }
            }

            Chunk west = world.getChunkAt(chunkX - 1, chunkZ);
            if (!ChunksManager.isChunkClaimed(west)) {
                for (int z = minZ; z < minZ + 16; z++) {
                    player.spawnParticle(Particle.DUST, minX, y, z, 5, dustoptions);
                }
            }

            Chunk east = world.getChunkAt(chunkX + 1, chunkZ);
            if (!ChunksManager.isChunkClaimed(east)) {
                for (int z = minZ; z < minZ + 16; z++) {
                    player.spawnParticle(Particle.DUST, minX + 16, y, z, 5, dustoptions);
                }
            }
        }
    }

    private static void cancelTaskFromDelayedParticlesAroundChunk(BukkitTask task, String player_name) {
        if (task != null) {
            particles_players_cache.remove(player_name);

            task.cancel();
            task = null;
        }
    }
}