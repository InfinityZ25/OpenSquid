package uk.lewdev.entitylib.entity;

import com.comphenix.protocol.wrappers.Vector3F;
import com.comphenix.protocol.wrappers.WrappedDataWatcher.Registry;
import com.comphenix.protocol.wrappers.WrappedDataWatcher.WrappedDataWatcherObject;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.util.EulerAngle;
import uk.lewdev.entitylib.entity.protocol.FakeEquippableEntity;
import uk.lewdev.entitylib.utils.AngleUtil;
import uk.lewdev.entitylib.utils.MCVersion;
import uk.lewdev.entitylib.utils.MaskUtil;

import java.util.UUID;

/**
 * 1.9 = https://wiki.vg/index.php?title=Entity_metadata&diff=7662&oldid=7569
 * 1.10 = https://wiki.vg/index.php?title=Entity_metadata&diff=8026&oldid=7978
 * Current = https://wiki.vg/Entity_metadata#Entity
 */
public class FakeStand extends FakeEquippableEntity {

    private final WrappedDataWatcherObject standByteWatcher = new WrappedDataWatcherObject(StandMetaData.dataByteIndex(), Registry.get(Byte.class));
    private final WrappedDataWatcherObject headRotWatcher = new WrappedDataWatcherObject(StandMetaData.headRotIndex(), Registry.getVectorSerializer());
    private final WrappedDataWatcherObject bodyRotWatcher = new WrappedDataWatcherObject(StandMetaData.bodyRotIndex(), Registry.getVectorSerializer());
    private final WrappedDataWatcherObject leftArmRotWatcher = new WrappedDataWatcherObject(StandMetaData.leftArmRotIndex(), Registry.getVectorSerializer());
    private final WrappedDataWatcherObject rightArmRotWatcher = new WrappedDataWatcherObject(StandMetaData.rightArmRotIndex(), Registry.getVectorSerializer());
    private final WrappedDataWatcherObject leftLegRotWatcher = new WrappedDataWatcherObject(StandMetaData.leftLegRotIndex(), Registry.getVectorSerializer());
    private final WrappedDataWatcherObject rightLegRotWatcher = new WrappedDataWatcherObject(StandMetaData.rightLegRotIndex(), Registry.getVectorSerializer());

    private byte standByte = (byte) 0;

    private boolean isSmall = false;
    private boolean hasArms = false;
    private boolean noBasePlate = false;
    private boolean isMaker = false;

    public FakeStand(World world, double x, double y, double z) {
        super(EntityType.ARMOR_STAND, UUID.randomUUID(), world, x, y, z);
        this.initDataWatcherObjs();
    }

    public FakeStand(World world, double x, double y, double z, float yaw, float headPitch) {
        super(EntityType.ARMOR_STAND, UUID.randomUUID(), world, x, y, z, yaw, headPitch);
        this.initDataWatcherObjs();
    }

    protected void initDataWatcherObjs() {
        super.getDataWatcher().setObject(this.standByteWatcher, this.standByte);
        super.getDataWatcher().setObject(this.headRotWatcher, BodyPart.HEAD.defaultVec3F());
        super.getDataWatcher().setObject(this.bodyRotWatcher, BodyPart.BODY.defaultVec3F());
        super.getDataWatcher().setObject(this.leftArmRotWatcher, BodyPart.LEFT_ARM.defaultVec3F());
        super.getDataWatcher().setObject(this.rightArmRotWatcher, BodyPart.RIGHT_ARM.defaultVec3F());
        super.getDataWatcher().setObject(this.leftLegRotWatcher, BodyPart.LEFT_LEG.defaultVec3F());
        super.getDataWatcher().setObject(this.rightLegRotWatcher, BodyPart.RIGHT_LEG.defaultVec3F());
    }

    public void setHeadRot(EulerAngle angle) {
        super.assertNotDead();

        super.getDataWatcher().setObject(this.headRotWatcher, AngleUtil.fromRadians(angle));
        super.sendMetaUpdate();
    }

    public void setBodyRot(EulerAngle angle) {
        super.assertNotDead();

        super.getDataWatcher().setObject(this.bodyRotWatcher, AngleUtil.fromRadians(angle));
        super.sendMetaUpdate();
    }

    public void setLeftArmRot(EulerAngle angle) {
        super.assertNotDead();

        super.getDataWatcher().setObject(this.leftArmRotWatcher, AngleUtil.fromRadians(angle));
        super.sendMetaUpdate();
    }

    public void setRightArmRot(EulerAngle angle) {
        super.assertNotDead();

        super.getDataWatcher().setObject(this.rightArmRotWatcher, AngleUtil.fromRadians(angle));
        super.sendMetaUpdate();
    }

    public void setLeftLegRot(EulerAngle angle) {
        super.assertNotDead();

        super.getDataWatcher().setObject(this.leftLegRotWatcher, AngleUtil.fromRadians(angle));
        super.sendMetaUpdate();
    }

    public void setRightLegRot(EulerAngle angle) {
        super.assertNotDead();

        super.getDataWatcher().setObject(this.rightLegRotWatcher, AngleUtil.fromRadians(angle));
        super.sendMetaUpdate();
    }

    public boolean isSmall() {
        return this.isSmall;
    }

    public void setSmall(boolean small) {
        super.assertNotDead();

        this.isSmall = small;

        this.standByte = MaskUtil.setBit(this.standByte, StandMetaData.isSmallBit(), small);
        super.getDataWatcher().setObject(this.standByteWatcher, this.standByte);
        super.sendMetaUpdate();
    }

    public void setHasArms(boolean arms) {
        super.assertNotDead();

        this.hasArms = arms;

        this.standByte = MaskUtil.setBit(this.standByte, StandMetaData.hasArmsBit(), arms);
        super.getDataWatcher().setObject(this.standByteWatcher, this.standByte);
        super.sendMetaUpdate();
    }

    public boolean hasArms() {
        return this.hasArms;
    }

    public void setNoBasePlate(boolean noBasePlate) {
        super.assertNotDead();

        this.noBasePlate = noBasePlate;

        this.standByte = MaskUtil.setBit(this.standByte, StandMetaData.noBasePlateBit(), noBasePlate);
        super.getDataWatcher().setObject(this.standByteWatcher, this.standByte);
        super.sendMetaUpdate();
    }

    public boolean hasNoBasePlate() {
        return this.noBasePlate;
    }

    /**
     * Small Hitbox
     *
     * @return is maker
     */
    public boolean isMaker() {
        return this.isMaker;
    }

    /**
     * Small Hitbox
     *
     * @param maker
     */
    public void setMaker(boolean maker) {
        super.assertNotDead();

        this.isMaker = maker;

        this.standByte = MaskUtil.setBit(this.standByte, StandMetaData.isMakerBit(), maker);
        super.getDataWatcher().setObject(this.standByteWatcher, this.standByte);
        super.sendMetaUpdate();
    }

    // https://wiki.vg/Entity_metadata#ArmorStand
    private enum StandMetaData {
        MC1_10(11, 12, 13, 14, 15, 16, 17, 0, 1, 2, 3),
        MC1_14(13, 14, 15, 16, 17, 18, 19, 0, 1, 2, 3),
        MC1_15(14, 15, 16, 17, 18, 19, 20, 0, 1, 2, 3),
        MC1_17(15, 16, 17, 18, 19, 20, 21, 0, 1, 2, 3);

        private final int dataByte;
        private final int headRot;
        private final int bodyRot;
        private final int leftArmRot;
        private final int rightArmRot;
        private final int leftLegRot;
        private final int rightLegRot;
        private int isSmallBit, hasArmsBit, noBasePlateBit, isMakerBit;
        StandMetaData(int dataByte, int headRot, int bodyRot, int leftArmRot, int rightArmRot, int leftLegRot,
                      int rightLegRot, int isSmall, int hasArms, int noBasePlate, int isMaker) {
            this.dataByte = dataByte;
            this.headRot = headRot;
            this.bodyRot = bodyRot;
            this.leftArmRot = leftArmRot;
            this.rightArmRot = rightArmRot;
            this.leftLegRot = leftLegRot;
            this.rightLegRot = rightLegRot;
        }

        private static StandMetaData get() {
            switch (MCVersion.getCurrentVersion()) {
                case V1_10:
                case V1_11:
                case V1_12:
                case V1_13:
                    return MC1_10;
                case V1_14:
                    return MC1_14;
                case V1_15:
                case V1_16:
                    return MC1_15;
                case V1_17:
                    return MC1_17;
                default:
                    return MC1_15;
            }
        }

        public static int dataByteIndex() {
            return get().dataByte;
        }

        public static int headRotIndex() {
            return get().headRot;
        }

        public static int bodyRotIndex() {
            return get().bodyRot;
        }

        public static int leftArmRotIndex() {
            return get().leftArmRot;
        }

        public static int rightArmRotIndex() {
            return get().rightArmRot;
        }

        public static int leftLegRotIndex() {
            return get().leftLegRot;
        }

        public static int rightLegRotIndex() {
            return get().rightLegRot;
        }

        public static int isSmallBit() {
            return get().isSmallBit;
        }

        public static int hasArmsBit() {
            return get().hasArmsBit;
        }

        public static int noBasePlateBit() {
            return get().noBasePlateBit;
        }

        public static int isMakerBit() {
            return get().isMakerBit;
        }
    }

    public enum BodyPart {
        HEAD(new EulerAngle(0D, 0D, 0D)),
        BODY(new EulerAngle(0D, 0D, 0D)),
        LEFT_ARM(new EulerAngle(-0.174533, 0.0D, -0.174533)),
        RIGHT_ARM(new EulerAngle(-0.261799, 0.0D, 0.174533)),
        LEFT_LEG(new EulerAngle(-0.0174533, 0.0D, -0.0174533)),
        RIGHT_LEG(new EulerAngle(0.0174533, 0.0D, 0.0174533));

        private final EulerAngle defaultAngle;

        BodyPart(EulerAngle defaultAngle) {
            this.defaultAngle = defaultAngle;
        }

        public EulerAngle defaultEuler() {
            return this.defaultAngle;
        }

        public Vector3F defaultVec3F() {
            return AngleUtil.fromRadians(this.defaultAngle);
        }
    }
}
