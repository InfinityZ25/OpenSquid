package uk.lewdev.entitylib.entity;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.BlockPosition;
import com.comphenix.protocol.wrappers.WrappedDataWatcher.Registry;
import com.comphenix.protocol.wrappers.WrappedDataWatcher.WrappedDataWatcherObject;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import uk.lewdev.entitylib.entity.protocol.FakeEntity;
import uk.lewdev.entitylib.utils.MCVersion;

import java.lang.reflect.InvocationTargetException;
import java.util.Optional;
import java.util.UUID;

/**
 * @author Lewys Davies (Lew_)
 */
public class FakeEndCrystal extends FakeEntity {

    private static final Optional<Object> EMPTY = Optional.empty();
    private final WrappedDataWatcherObject beamTarget = new WrappedDataWatcherObject(EndCrystalMetaData.beamIndex(),
      Registry.getBlockPositionSerializer(true));
    private final WrappedDataWatcherObject showBottom = new WrappedDataWatcherObject(EndCrystalMetaData.showBottomIndex(),
      Registry.get(Boolean.class));

    /**
     * @param world
     * @param x
     * @param y
     * @param z
     */
    public FakeEndCrystal(World world, double x, double y, double z) {
        super(EntityType.ENDER_CRYSTAL, UUID.randomUUID(), world, x, y, z, 0, 0);
        this.initDataWatcherObjs();
    }

    protected void initDataWatcherObjs() {
        super.getDataWatcher().setObject(this.beamTarget, Optional.empty());
        super.getDataWatcher().setObject(this.showBottom, false);
    }

    @Override
    protected void sendSpawnPacket(Player player) {
        super.assertNotDead();

        PacketContainer spawnPacket = new PacketContainer(PacketType.Play.Server.SPAWN_ENTITY);
        
        spawnPacket.getIntegers()
          .write(0, super.getEntityId());
        spawnPacket.getEntityTypeModifier()
          .write(0, EntityType.ENDER_CRYSTAL);
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

    /**
     * @param loc (nullable)
     */
    public void setBeamTarget(Location loc) {
        if (loc == null) {
            super.getDataWatcher().setObject(this.beamTarget, EMPTY);
        } else {
            super.getDataWatcher().setObject(this.beamTarget, Optional.of(BlockPosition.getConverter().getGeneric(new BlockPosition((int) loc.getX(), (int) loc.getY(), (int) loc.getZ()))));
        }

        super.sendMetaUpdate();
    }

    /**
     * @param position (nullable)
     */
    public void setBeamTarget(BlockPosition position) {
        if (position == null) {
            super.getDataWatcher().setObject(this.beamTarget, EMPTY);
        } else {
            super.getDataWatcher().setObject(this.beamTarget, Optional.of(BlockPosition.getConverter().getGeneric(position)));
        }

        super.sendMetaUpdate();
    }

    /**
     * For the OPTIMAL performance. Use only if you know wtf you're doing. You will crash
     * stuff if you use this wrong.
     *
     * @param nmsBlockPos from BlockPosition.getConverter().getGeneric()
     */
    public void setBeamTarget(Optional<Object> nmsBlockPos) {
        if (nmsBlockPos == null) {
            super.getDataWatcher().setObject(this.beamTarget, EMPTY);
        } else {
            super.getDataWatcher().setObject(this.beamTarget, nmsBlockPos);
        }

        super.sendMetaUpdate();
    }

    /**
     * Remove any target from this end crystal
     */
    public void clearBeamTarget() {
        super.getDataWatcher().setObject(this.beamTarget, EMPTY);
        super.sendMetaUpdate();
    }

    public void setShowBottom(boolean showBottom) {
        super.getDataWatcher().setObject(this.showBottom, showBottom);
        super.sendMetaUpdate();
    }

    private enum EndCrystalMetaData {
        MC1_15(7, 8),
        MC1_17(8, 9);

        private final int beamIndex;
        private final int showBottomIndex;

        EndCrystalMetaData(int beamIndex, int showBottomIndex) {
            this.beamIndex = beamIndex;
            this.showBottomIndex = showBottomIndex;
        }

        private static EndCrystalMetaData get() {
            if (MCVersion.getCurrentVersion().ordinal() >= MCVersion.V1_17.ordinal()) {
                return MC1_17;
            }
            return MC1_15;
        }

        public static int beamIndex() {
            return get().beamIndex;
        }

        public static int showBottomIndex() {
            return get().showBottomIndex;
        }
    }
}
