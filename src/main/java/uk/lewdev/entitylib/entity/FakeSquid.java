package uk.lewdev.entitylib.entity;

import org.bukkit.World;
import org.bukkit.entity.EntityType;
import uk.lewdev.entitylib.entity.protocol.FakeLivingEntity;

import java.util.UUID;

/**
 * @author Lewys Davies (Lew_)
 */
public class FakeSquid extends FakeLivingEntity {

    public FakeSquid(World world, double x, double y, double z) {
        super(EntityType.SQUID, UUID.randomUUID(), world, x, y, z);
    }
}
