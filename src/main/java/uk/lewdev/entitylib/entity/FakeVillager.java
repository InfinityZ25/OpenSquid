package uk.lewdev.entitylib.entity;

import com.comphenix.protocol.wrappers.WrappedDataWatcher.Registry;
import com.comphenix.protocol.wrappers.WrappedDataWatcher.WrappedDataWatcherObject;
import com.comphenix.protocol.wrappers.WrappedVillagerData;
import com.comphenix.protocol.wrappers.WrappedVillagerData.Profession;
import com.comphenix.protocol.wrappers.WrappedVillagerData.Type;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import uk.lewdev.entitylib.entity.protocol.FakeInsentient;
import uk.lewdev.entitylib.utils.MCVersion;

import java.util.UUID;

/**
 * @author Lewys Davies (Lew_)
 * @since 1.15+
 */
public class FakeVillager extends FakeInsentient {

    private final WrappedDataWatcherObject villagerDataWatcher = new WrappedDataWatcherObject(VillagerMetaData.villagerDataIndex(),
      Registry.get(WrappedVillagerData.getNmsClass()));

    private Type type = Type.PLAINS;
    private Profession profession = Profession.NONE;

    public FakeVillager(World world, double x, double y, double z, float yaw, float headPitch, float headYaw) {
        super(EntityType.VILLAGER, UUID.randomUUID(), world, x, y, z, yaw, headPitch, headYaw);
        this.initDataWatcherObjs();
    }

    public FakeVillager(World world, double x, double y, double z, float yaw, float headPitch) {
        this(world, x, y, z, yaw, headPitch, 0);
    }

    public FakeVillager(Location loc, float headYaw) {
        this(loc.getWorld(), loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch(), headYaw);
    }

    private final void initDataWatcherObjs() {
        WrappedVillagerData vd = WrappedVillagerData.fromValues(this.type, this.profession, 1);
        super.getDataWatcher().setObject(this.villagerDataWatcher, vd.getHandle());
    }

    public final void setVillagerData(Type type, Profession profession) {
        super.assertNotDead();

        this.type = type;
        this.profession = profession;

        WrappedVillagerData vd = WrappedVillagerData.fromValues(type, profession, 2);
        super.getDataWatcher().setObject(this.villagerDataWatcher, vd.getHandle());
        super.sendMetaUpdate();
    }

    private enum VillagerMetaData {
        MC1_15(17),
        MC1_17(18);

        private final int villagerDataIndex;

        VillagerMetaData(int villagerDataIndex) {
            this.villagerDataIndex = villagerDataIndex;
        }

        private static VillagerMetaData get() {
            if (MCVersion.getCurrentVersion().ordinal() >= MCVersion.V1_17.ordinal()) {
                return MC1_17;
            }
            return MC1_15;
        }

        public static int villagerDataIndex() {
            return get().villagerDataIndex;
        }
    }
}
