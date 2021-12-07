package uk.lewdev.entitylib.entity;

import org.bukkit.World;
import org.bukkit.entity.EntityType;

/**
 * @author Lewys Davies (Lew_)
 */
public class FakeElderGuardian extends FakeGuardian {

    public FakeElderGuardian(World world, double x, double y, double z) {
        super(EntityType.ELDER_GUARDIAN, world, x, y, z);
    }
}
