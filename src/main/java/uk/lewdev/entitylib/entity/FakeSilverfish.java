package uk.lewdev.entitylib.entity;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import uk.lewdev.entitylib.entity.protocol.FakeLivingEntity;

import java.util.UUID;

/**
 * @author Lewys Davies (Lew_)
 */
public class FakeSilverfish extends FakeLivingEntity {

    /**
     * @param world
     * @param x
     * @param y
     * @param z
     */
    public FakeSilverfish(World world, double x, double y, double z) {
        super(EntityType.SILVERFISH, UUID.randomUUID(), world, x, y, z);
    }

    /**
     * @param loc
     */
    public FakeSilverfish(Location loc) {
        this(loc.getWorld(), loc.getX(), loc.getY(), loc.getZ());
    }
}
