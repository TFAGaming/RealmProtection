package realmprotection.utils;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;

public class ParticleSpawner {
    public void spawnTemporaryParticle(World world, double x, double y, double z, int count, double offsetX, double offsetY, double offsetZ, double extra) {
        Location location = new Location(world, x, y, z);
        
        world.spawnParticle(Particle.SMOKE_NORMAL, location, count, offsetX, offsetY, offsetZ, extra);
    }
}