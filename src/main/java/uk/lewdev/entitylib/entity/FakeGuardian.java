package uk.lewdev.entitylib.entity;

import com.comphenix.protocol.wrappers.WrappedDataWatcher.Registry;
import com.comphenix.protocol.wrappers.WrappedDataWatcher.WrappedDataWatcherObject;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import uk.lewdev.entitylib.entity.protocol.FakeLivingEntity;
import uk.lewdev.entitylib.utils.MCVersion;

import java.util.UUID;

/**
 * @author Lewys Davies (Lew_)
 */
public class FakeGuardian extends FakeLivingEntity {

    private final WrappedDataWatcherObject guardianTarget = new WrappedDataWatcherObject(GuardianMetaData.targetIndex(),
      Registry.get(Integer.class));

    private int entityTargetID = 0;

    public FakeGuardian(World world, double x, double y, double z) {
        super(EntityType.GUARDIAN, UUID.randomUUID(), world, x, y, z);
        super.getDataWatcher().setObject(this.guardianTarget, this.entityTargetID);
    }

    protected FakeGuardian(EntityType type, World world, double x, double y, double z) {
        super(type, UUID.randomUUID(), world, x, y, z);
        super.getDataWatcher().setObject(this.guardianTarget, this.entityTargetID);
    }

    public void clearTarget() {
        this.setTarget(0);
    }

    public int getTarget() {
        return this.entityTargetID;
    }

    public void setTarget(int entityID) {
        super.assertNotDead();

        this.entityTargetID = entityID;
        super.getDataWatcher().setObject(this.guardianTarget, this.entityTargetID);

        super.sendMetaUpdate();
    }

    private enum GuardianMetaData {
        MC1_15(15, 16),
        MC1_17(16, 17);

        private final int spikesIndex;
        private final int targetIndex;

        GuardianMetaData(int spikesIndex, int targetIndex) {
            this.spikesIndex = spikesIndex;
            this.targetIndex = targetIndex;
        }

        private static GuardianMetaData get() {
            if (MCVersion.getCurrentVersion().ordinal() >= MCVersion.V1_17.ordinal()) {
                return MC1_17;
            }
            return MC1_15;
        }

        @SuppressWarnings("unused")
        public static int spikesIndex() {
            return get().spikesIndex;
        }

        public static int targetIndex() {
            return get().targetIndex;
        }
    }
}
