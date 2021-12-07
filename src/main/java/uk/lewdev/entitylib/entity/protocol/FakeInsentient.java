package uk.lewdev.entitylib.entity.protocol;

import com.comphenix.protocol.wrappers.WrappedDataWatcher.Registry;
import com.comphenix.protocol.wrappers.WrappedDataWatcher.WrappedDataWatcherObject;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import uk.lewdev.entitylib.utils.MCVersion;
import uk.lewdev.entitylib.utils.MaskUtil;

import java.util.UUID;

/**
 * @author Lewys Davies (Lew_)
 */
public abstract class FakeInsentient extends FakeLivingEntity {

    private final WrappedDataWatcherObject insentientByteWatcher = new WrappedDataWatcherObject(InsentientMetaData.byteIndex(),
      Registry.get(Byte.class));

    private byte insentientByte = (byte) 0;
    private boolean leftHanded = false;
    private boolean ai = true;

    protected FakeInsentient(EntityType type, UUID uuid, World world, double x, double y, double z, float yaw, float headPitch, float headYaw) {
        super(type, uuid, world, x, y, z, yaw, headPitch, headYaw);
        super.getDataWatcher().setObject(this.insentientByteWatcher, this.insentientByte);
    }

    protected FakeInsentient(EntityType type, World world, double x, double y, double z, float yaw, float headPitch) {
        this(type, UUID.randomUUID(), world, x, y, z, yaw, headPitch, 0);
    }

    protected FakeInsentient(EntityType type, UUID uuid, World world, double x, double y, double z) {
        this(type, uuid, world, x, y, z, 0, 0, 0);
    }

    protected FakeInsentient(EntityType type, World world, double x, double y, double z) {
        this(type, UUID.randomUUID(), world, x, y, z, 0, 0, 0);
    }

    public final boolean hasAi() {
        return this.ai;
    }

    public void setAi(boolean ai) {
        super.assertNotDead();

        this.ai = ai;
        this.insentientByte = MaskUtil.setBit(this.insentientByte, InsentientMetaData.aiBit(), this.ai);

        super.getDataWatcher().setObject(this.insentientByteWatcher, this.insentientByte);
        super.sendMetaUpdate();
    }

    public boolean isLeftHanded() {
        return this.leftHanded;
    }

    public void setLeftHanded(boolean leftHanded) {
        super.assertNotDead();

        this.leftHanded = leftHanded;
        this.insentientByte = MaskUtil.setBit(this.insentientByte, InsentientMetaData.leftHandBit(), this.leftHanded);

        super.getDataWatcher().setObject(this.insentientByteWatcher, this.insentientByte);
        super.sendMetaUpdate();
    }

    private enum InsentientMetaData {
        MC1_15(14, 0, 1),
        MC1_17(15, 0, 1);

        private final int insentientByteIndex;
        private final int aiIndex;
        private final int leftHandIndex;

        InsentientMetaData(int insentientByteIndex, int aiIndex, int leftHandIndex) {
            this.insentientByteIndex = insentientByteIndex;
            this.aiIndex = aiIndex;
            this.leftHandIndex = leftHandIndex;
        }

        private static InsentientMetaData get() {
            if (MCVersion.getCurrentVersion().ordinal() >= MCVersion.V1_17.ordinal()) {
                return MC1_17;
            }
            return MC1_15;
        }

        public static int byteIndex() {
            return get().insentientByteIndex;
        }

        public static int aiBit() {
            return get().aiIndex;
        }

        public static int leftHandBit() {
            return get().leftHandIndex;
        }
    }
}
