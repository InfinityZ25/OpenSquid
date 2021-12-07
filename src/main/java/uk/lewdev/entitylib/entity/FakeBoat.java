package uk.lewdev.entitylib.entity;

import java.lang.reflect.InvocationTargetException;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedDataWatcher.Registry;
import com.comphenix.protocol.wrappers.WrappedDataWatcher.WrappedDataWatcherObject;

import uk.lewdev.entitylib.entity.protocol.FakeEntity;

/**
 * @author Lewys Davies (Lew_)
 */
public class FakeBoat extends FakeEntity {

    private final WrappedDataWatcherObject boatType = new WrappedDataWatcherObject(11,
            Registry.get(Integer.class));
    
    private final BoatType type;
    
    public FakeBoat(World world, double x, double y, double z, float yaw, float pitch, BoatType type) {
        super(EntityType.BOAT, UUID.randomUUID(), world, x, y, z, yaw, pitch);
        this.type = type;
        this.initDataWatcherObjs();
    }
    
    public FakeBoat(Location location, BoatType type) {
        this(location.getWorld(), location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch(), type);
    }
    
    protected void initDataWatcherObjs() {
        super.getDataWatcher().setObject(this.boatType, this.type.getID());
    }

    @Override
    protected void sendSpawnPacket(Player player) {
        super.assertNotDead();
        
        PacketContainer spawnPacket = new PacketContainer(PacketType.Play.Server.SPAWN_ENTITY);
        
        spawnPacket.getIntegers()
            .write(0, super.getEntityId());
        spawnPacket.getEntityTypeModifier()
            .write(0, EntityType.BOAT);
        spawnPacket.getUUIDs()
            .write(0, super.getUUID());
        spawnPacket.getDoubles()
            .write(0, super.getX())
            .write(1, super.getY())
            .write(2, super.getZ());
        
        try {
            protocol.sendServerPacket(player, spawnPacket);
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

        super.sendMetaUpdate();
    }
    
    public static enum BoatType {
        OAK(0),
        SPRUCE(1),
        BIRCH(2),
        JUNGLE(3),
        ACACIA(4),
        DARK_OAK(5);
        
        private final int id;
        
        private BoatType(int id) {
            this.id = id;
        }
        
        public int getID() {
            return this.id;
        }
    }
}
