package uk.lewdev.entitylib.entity.protocol;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.EnumWrappers.EntityPose;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import com.comphenix.protocol.wrappers.WrappedDataWatcher.Registry;
import com.comphenix.protocol.wrappers.WrappedDataWatcher.WrappedDataWatcherObject;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import uk.lewdev.entitylib.FakeEntityAPI;
import uk.lewdev.entitylib.entity.EntityId;
import uk.lewdev.entitylib.utils.AngleUtil;
import uk.lewdev.entitylib.utils.MCVersion;
import uk.lewdev.entitylib.utils.MaskUtil;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author Lewys Davies (Lew_)
 */
public abstract class FakeEntity {

    /**
     * Entity ID, Fake Entity Instance
     */
    public final static ConcurrentHashMap<Integer, FakeEntity> ALL_ALIVE_INSTANCES = new ConcurrentHashMap<Integer, FakeEntity>();

    protected static final ProtocolManager protocol = ProtocolLibrary.getProtocolManager();
    private final PacketContainer destroyPacket = new PacketContainer(PacketType.Play.Server.ENTITY_DESTROY);
    private final WrappedDataWatcher dataWatcher = new WrappedDataWatcher();
    private final VisibilityHandler visibilityHandler = new VisibilityHandler(this);
    private final WrappedDataWatcherObject entityByteWatcher = new WrappedDataWatcherObject(EntityMetaByte.byteIndex(),
      Registry.get(Byte.class));
    private final WrappedDataWatcherObject customNameWatcher = new WrappedDataWatcherObject(EntityMetaData.customNameIndex(),
      Registry.getChatComponentSerializer(true));
    private final WrappedDataWatcherObject customNameVisibleWatcher = new WrappedDataWatcherObject(
      EntityMetaData.nameVisible(), Registry.get(Boolean.class));
    private final WrappedDataWatcherObject gravityWatcher = new WrappedDataWatcherObject(EntityMetaData.gravityIndex(),
      Registry.get(Boolean.class));
    private final WrappedDataWatcherObject poseWatcher = new WrappedDataWatcherObject(EntityMetaData.poseIndex(),
      Registry.get(EnumWrappers.getEntityPoseClass()));
    private final int entityId;
    private final UUID uuid;
    private final EntityType type;
    private final int protocolID;
    private final AtomicBoolean isDead = new AtomicBoolean(false); // Has destroy been called
    private final World world;
    private double x, y, z;
    private float yaw = 0, pitch = 0;

    private byte entityDataByte = (byte) 0;
    private boolean isInvisible = false;
    private String customName = "";
    private boolean noGravity = false;
    private boolean customNameVisible = false;
    private boolean glow = false;
    private EntityPose wrappedPose = EntityPose.STANDING;

    private final PacketContainer movePacket = new PacketContainer(PacketType.Play.Server.REL_ENTITY_MOVE_LOOK);
    private final PacketContainer teleportPacket = new PacketContainer(PacketType.Play.Server.ENTITY_TELEPORT);
    private final PacketContainer metaPacket = new PacketContainer(PacketType.Play.Server.ENTITY_METADATA);

    private Integer vehicleEid = null;
    private final Set<Integer> passengerEids = new HashSet<>();

    protected FakeEntity(EntityType type, UUID uuid, World world, double x, double y, double z, float yaw, float pitch) {
        this.entityId = FakeEntityAPI.get().getEntityIdProvider().nextAndIncrement();
        this.uuid = uuid;
        this.type = type;
        this.protocolID = EntityId.fromType(type);

        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;

        this.initMovePackets();

        if (MCVersion.getCurrentVersion().ordinal() >= MCVersion.V1_17.ordinal()) {
            ArrayList<Integer> id = new ArrayList<>();
            id.add(this.entityId);
            this.destroyPacket.getIntLists().write(0, id);
        } else {
            this.destroyPacket.getIntegerArrays().write(0, new int[]{this.entityId});
        }

        this.metaPacket.getIntegers().write(0, this.getEntityId());

        this.initDataWatcherObjs();

        ALL_ALIVE_INSTANCES.put(this.entityId, this);
    }

    private void initMovePackets() {
        this.teleportPacket.getIntegers().write(0, this.getEntityId());
        this.teleportPacket.getDoubles().write(0, this.x);
        this.teleportPacket.getDoubles().write(1, this.y);
        this.teleportPacket.getDoubles().write(2, this.z);
        this.teleportPacket.getBytes().write(0, AngleUtil.fromDegrees(this.yaw));
        this.teleportPacket.getBytes().write(1, AngleUtil.fromDegrees(this.pitch));

        short dx = (short) ((this.x * 32 - this.x * 32) * 128);
        short dy = (short) ((this.y * 32 - this.y * 32) * 128);
        short dz = (short) ((this.z * 32 - this.z * 32) * 128);

        this.movePacket.getIntegers().write(0, this.getEntityId());
        this.movePacket.getShorts().write(0, dx);
        this.movePacket.getShorts().write(1, dy);
        this.movePacket.getShorts().write(2, dz);
        this.movePacket.getBytes().write(0, AngleUtil.fromDegrees(this.yaw));
        this.movePacket.getBytes().write(1, AngleUtil.fromDegrees(this.pitch));
        this.movePacket.getBooleans().write(0, true);
    }

    /**
     * Init all DataWatchers relevant to the base entity metadata
     */
    private void initDataWatcherObjs() {
        this.dataWatcher.setObject(this.entityByteWatcher, this.entityDataByte);
        this.dataWatcher.setObject(this.customNameWatcher, Optional.of(WrappedChatComponent.fromText(this.customName).getHandle()));
        this.dataWatcher.setObject(this.customNameVisibleWatcher, this.customNameVisible);
        this.dataWatcher.setObject(this.gravityWatcher, this.noGravity);
        this.dataWatcher.setObject(this.poseWatcher, this.wrappedPose.toNms());
    }

    /**
     * Destroy this entities instances. Cleans memory and despawns the entity
     */
    public final void destroy() {
        if (this.isDead()) return;

        ALL_ALIVE_INSTANCES.remove(this.entityId);

        // Get copy of current render list and destroy visibility handler
        this.visibilityHandler.visibleTo().forEach(this::sendDestroyPacket);
        this.visibilityHandler.destroy();

        this.isDead.set(true);
    }

    /**
     * Some entity types are spawned differently. i.e. Pig vs Player<br>
     * Therefore handle spawning in each child class
     *
     * @param player to send packet to
     */
    protected abstract void sendSpawnPacket(Player player);

    /**
     * All entities handle this the same.
     *
     * @param player
     */
    protected final void sendDestroyPacket(Player player) {
        this.assertNotDead();

        try {
            protocol.sendServerPacket(player, this.destroyPacket);
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    /**
     * @return is dead
     */
    public final boolean isDead() {
        return this.isDead.get();
    }

    /**
     * @return Unique entity ID for use in packets
     */
    public final int getEID() {
        return this.entityId;
    }

    /**
     * @return the type
     */
    public final EntityType getType() {
        return this.type;
    }

    /**
     * @return the world
     */
    public final World getWorld() {
        return this.world;
    }

    /**
     * @return the x
     */
    public final double getX() {
        return this.x;
    }

    /**
     * @param x the x to set
     */
    public final void setX(double x) {
        this.assertNotDead();
        this.setLocation(x, this.y, this.z, this.yaw, this.pitch);
    }

    /**
     * @return the y
     */
    public final double getY() {
        return this.y;
    }

    /**
     * @param y the y to set
     */
    public final void setY(double y) {
        this.assertNotDead();
        this.setLocation(this.x, y, this.z, this.yaw, this.pitch);
    }

    /**
     * @return the z
     */
    public final double getZ() {
        return this.z;
    }

    /**
     * @param z the z to set
     */
    public final void setZ(double z) {
        this.assertNotDead();
        this.setLocation(this.x, this.y, z, this.yaw, this.pitch);
    }

    /**
     * @return the yaw
     */
    public final float getYaw() {
        return this.yaw;
    }

    /**
     * @param yaw the yaw to set
     */
    public final void setYaw(float yaw) {
        this.assertNotDead();
        this.setLocation(this.x, this.y, this.z, yaw, this.pitch);
    }

    /**
     * @return the pitch
     */
    public final float getPitch() {
        return this.pitch;
    }

    /**
     * @param pitch the pitch to set
     */
    public final void setPitch(float pitch) {
        this.assertNotDead();
        this.setLocation(this.x, this.y, this.z, this.yaw, pitch);
    }

    /**
     * @return uuid the uuid
     */
    public final UUID getUUID() {
        return this.uuid;
    }

    /**
     * @param glow should glow
     */
    public final void setGlow(boolean glow) {
        this.assertNotDead();
        this.glow = glow;
        this.entityDataByte = MaskUtil.setBit(this.entityDataByte, EntityMetaByte.glowingIndex(), glow);
        this.dataWatcher.setObject(this.entityByteWatcher, this.entityDataByte);
        this.sendMetaUpdate();
    }

    /**
     * @return is glowing (has outline)
     */
    public final boolean isGlowing() {
        return this.glow;
    }

    /**
     * @return the entity pose
     */
    public final EntityPose getPose() {
        return this.wrappedPose;
    }

    /**
     * Set the entity pose
     *
     * @param pose
     */
    public final void setPose(EntityPose pose) {
        this.assertNotDead();

        this.wrappedPose = pose;
        this.dataWatcher.setObject(this.poseWatcher, this.wrappedPose.toNms());
        this.sendMetaUpdate();
    }

    /**
     * @return is invisible
     */
    public final boolean isInvisible() {
        return this.isInvisible;
    }

    /**
     * @param invisible
     */
    public final void setInvisible(boolean invisible) {
        this.assertNotDead();

        this.isInvisible = invisible;
        this.entityDataByte = MaskUtil.setBit(this.entityDataByte, EntityMetaByte.invisibleIndex(), this.isInvisible);
        this.dataWatcher.setObject(this.entityByteWatcher, this.entityDataByte);
        this.sendMetaUpdate();
    }

    public final void setGravity(boolean gravity) {
        this.assertNotDead();

        this.noGravity = !gravity;
        this.dataWatcher.setObject(this.gravityWatcher, this.noGravity);
        this.sendMetaUpdate();
    }

    /**
     * @param json
     */
    public void setCustomNameJson(String json) {
        this.assertNotDead();

        this.customName = json;
        this.dataWatcher.setObject(this.customNameWatcher, Optional.of(WrappedChatComponent.fromJson(json).getHandle()));
        this.sendMetaUpdate();
    }

    /**
     * @return Entity is swimming
     */
    public boolean isSwimming() {
        return MaskUtil.getBit(this.entityDataByte, 4);
    }

    /**
     * @param swimming
     */
    public void setSwimming(boolean swimming) {
        this.assertNotDead();

        this.entityDataByte = MaskUtil.setBit(this.entityDataByte, 4, swimming);
        this.dataWatcher.setObject(this.entityByteWatcher, this.entityDataByte);

        this.sendMetaUpdate();
    }

    /**
     * @return name
     */
    public String getCustomName() {
        return this.customName;
    }

    /**
     * @param name
     */
    public void setCustomName(String name) {
        this.assertNotDead();

        this.customName = name;
        this.dataWatcher.setObject(this.customNameWatcher, Optional.of(WrappedChatComponent.fromText(name).getHandle()));
        this.sendMetaUpdate();
    }

    /**
     * @return custom name visible
     */
    public boolean isCustomNameVisible() {
        return this.customNameVisible;
    }

    /**
     * @param visible
     */
    public void setCustomNameVisible(boolean visible) {
        this.assertNotDead();

        this.customNameVisible = visible;
        this.dataWatcher.setObject(this.customNameVisibleWatcher, this.customNameVisible);

        this.sendMetaUpdate();
    }

    /**
     * @return Location of this entity
     */
    public Location getLocation() {
        return new Location(this.world, this.x, this.y, this.z, this.getYaw(), this.getPitch());
    }

    /**
     * Move or teleport an entity to the new location, depending on distance<br>
     *
     * @param loc
     * @apiNote Calls {@link #setLocation(double, double, double, float, float)
     * setLocation(loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(),
     * loc.getPitch())}
     */
    public final void setLocation(Location loc) {
        if (!loc.getWorld().equals(this.world)) throw new IllegalArgumentException("Cannot change an entity's world");
        this.setLocation(loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch());
    }

    /**
     * Move or teleport an entity to the new location, depending on distance<br>
     *
     * @param x
     * @param y
     * @param z
     * @apiNote Calls {@link #setLocation(double, double, double, float, float)
     * setLocation(x, y, z, 0, 0)}
     */
    public final void setLocation(double x, double y, double z) {
        this.setLocation(x, y, z, 0, 0);
    }

    /**
     * Teleport an entity to the new location, depending on distance
     *
     * @param x
     * @param y
     * @param z
     * @param yaw   degrees such as {@link Location#getYaw()}
     * @param pitch degrees such as {@link Location#getPitch()}
     */
    public final void setLocation(double x, double y, double z, float yaw, float pitch) {
        this.assertNotDead();

        this.sendTeleportPacket(x, y, z, yaw, pitch);

        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
    }

    /**
     * Only move an entity without any additional checks. <b>Distance must be <8
     * blocks.</b><br>
     * Typically used for path finding<br>
     * If you can't guarantee distance is <8, use
     * {@link #setLocation(double, double, double, float, float)}
     *
     * @param x
     * @param y
     * @param z
     * @param yaw
     * @param pitch
     */
    public final void move(double x, double y, double z, float yaw, float pitch) {
        this.assertNotDead();

        this.sendMovePacket(x, y, z, yaw, pitch);

        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
    }

    /**
     * Only move an entity without any additional checks. <b>Distance must be <8
     * blocks.</b><br>
     * Typically used for path finding<br>
     * If you can't guarantee distance is <8, use
     * {@link #setLocation(double, double, double, float, float)}
     *
     * @param loc
     */
    public final void move(Location loc) {
        if (!loc.getWorld().equals(this.world)) throw new IllegalArgumentException("Cannot change an entity's world");
        this.move(loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch());
    }

    /**
     * Allow the player to see this entity
     *
     * @param player
     */
    public final void show(Player player) {
        this.assertNotDead();
        this.visibilityHandler.setVisible(player);
    }

    /**
     * Do not allow the player to see this entity
     *
     * @param player
     */
    public final void hide(Player player) {
        this.assertNotDead();
        this.visibilityHandler.setNotVisible(player);
    }

    /**
     * @param visibleToAll
     */
    public final void setGloballyVisible(boolean visibleToAll) {
        this.assertNotDead();
        this.visibilityHandler.setGloballyVisible(visibleToAll);
    }

    public void mountVehicle(int eid) {
        this.vehicleEid = eid;
        this.sendMountPackets();
    }

    public void unmountVehicle() {
        if (this.vehicleEid != null) {
            this.sendUnMountFromVehicle(this.vehicleEid);
        }
        this.vehicleEid = null;
    }

    public void addPassengers(Integer... eids) {
        this.passengerEids.addAll(Arrays.asList(eids));
        this.sendMountPackets();
    }

    public void removePassenger(int eid) {
        this.passengerEids.remove(eid);
        this.sendMountPackets();
    }

    public void removePassengers() {
        this.passengerEids.clear();
        this.sendMountPackets();
    }

    public Optional<Integer> getVehicleEid() {
        return Optional.ofNullable(this.vehicleEid);
    }

    public int[] getPassengerEids() {
        return this.passengerEids.stream().mapToInt(i -> i).toArray();
    }

    public boolean hasPassengers() {
        return !this.passengerEids.isEmpty();
    }

    /**
     * @return this entities unique id
     */
    protected final int getEntityId() {
        return this.entityId;
    }

    /**
     * @return the entity type protocol id
     */
    protected final int getProtocolId() {
        return this.protocolID;
    }

    /**
     * @return the visibilityHandler
     */
    protected final VisibilityHandler getVisibilityHandler() {
        this.assertNotDead();
        return this.visibilityHandler;
    }

    /**
     * @return the dataWatcher
     */
    protected final WrappedDataWatcher getDataWatcher() {
        this.assertNotDead();
        return this.dataWatcher;
    }

    /**
     * Sends a Entity Metadata update packet to players this entity is visible to
     */
    protected final void sendMetaUpdate() {
        this.assertNotDead();

        this.metaPacket.getWatchableCollectionModifier()
          .write(0, this.dataWatcher.getWatchableObjects());

        for (Player p : this.visibilityHandler.renderedTo()) {
            try {
                protocol.sendServerPacket(p, metaPacket);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Update this entities passengers / vehicle state
     */
    protected final void sendMountPackets() {
        // https://wiki.vg/Protocol#Set_Passengers
        PacketContainer mountPacket = new PacketContainer(PacketType.Play.Server.MOUNT);

        // If this entity is mounted, send that mount packet
        if (this.vehicleEid != null) {
            mountPacket.getIntegers()
              .write(0, this.vehicleEid);
            mountPacket.getIntegerArrays()
              .write(0, new int[]{this.entityId});

            try {
                for (Player p : this.visibilityHandler.renderedTo()) {
                    protocol.sendServerPacket(p, mountPacket);
                }
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }

        // Then mount this entities passengers
        mountPacket.getIntegers()
          .write(0, this.entityId);
        mountPacket.getIntegerArrays()
          .write(0, this.getPassengerEids());

        try {
            for (Player p : this.visibilityHandler.renderedTo()) {
                protocol.sendServerPacket(p, mountPacket);
            }
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    private final void sendMovePacket(double newX, double newY, double newZ, float newYaw, float newPitch) {
        this.assertNotDead();
        if (this.visibilityHandler.renderedTo().isEmpty()) return;

        if (newX != this.x) {
            short dx = (short) ((newX * 32 - this.x * 32) * 128);
            movePacket.getShorts().write(0, dx);
        }

        if (newY != this.y) {
            short dy = (short) ((newY * 32 - this.y * 32) * 128);
            movePacket.getShorts().write(1, dy);
        }

        if (newZ != this.z) {
            short dz = (short) ((newZ * 32 - this.z * 32) * 128);
            movePacket.getShorts().write(2, dz);
        }

        if (newYaw != this.yaw) {
            movePacket.getBytes().write(0, AngleUtil.fromDegrees(newYaw));
        }

        if (newPitch != this.pitch) {
            movePacket.getBytes().write(1, AngleUtil.fromDegrees(newPitch));
        }

        try {
            for (Player p : this.visibilityHandler.renderedTo()) {
                protocol.sendServerPacket(p, movePacket);
            }
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    private final void sendTeleportPacket(double newX, double newY, double newZ, float newYaw, float newPitch) {
        this.assertNotDead();
        if (this.visibilityHandler.renderedTo().isEmpty()) return;

        if (newX != this.x) {
            teleportPacket.getDoubles().write(0, newX);
        }

        if (newY != this.y) {
            teleportPacket.getDoubles().write(1, newY);
        }

        if (newZ != this.z) {
            teleportPacket.getDoubles().write(2, newZ);
        }

        if (newYaw != this.yaw) {
            teleportPacket.getBytes().write(0, AngleUtil.fromDegrees(newYaw));
        }

        if (newPitch != this.pitch) {
            teleportPacket.getBytes().write(1, AngleUtil.fromDegrees(newPitch));
        }

        try {
            for (Player p : this.visibilityHandler.renderedTo()) {
                protocol.sendServerPacket(p, teleportPacket);
            }
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    /**
     * Remove all passengers from a vehicle
     *
     * @param vehicleEid
     */
    private final void sendUnMountFromVehicle(int vehicleEid) {
        // https://wiki.vg/Protocol#Set_Passengers
        PacketContainer mountPacket = new PacketContainer(PacketType.Play.Server.MOUNT);
        mountPacket.getIntegers()
          .write(0, vehicleEid);
        mountPacket.getIntegerArrays()
          .write(0, new int[0]);

        try {
            for (Player p : this.visibilityHandler.renderedTo()) {
                protocol.sendServerPacket(p, mountPacket);
            }
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    /**
     * @throws IllegalStateException If entity is dead
     */
    protected final void assertNotDead() {
        if (this.isDead.get())
            throw new IllegalStateException(
              "FakeEntity: Cannot Perform Set / Update Operations On A Destroyed Entity");
    }

    protected final static class EntityMetaByte {
        /**
         * Where in the DataWrapper the byte of booleans is located
         **/
        public static int byteIndex() {
            return 0;
        }

        /**
         * @return Where within the Byte the invisible bit is located.
         */
        static int invisibleIndex() {
            switch (MCVersion.getCurrentVersion()) {
                case V1_10:
                case V1_11:
                case V1_12:
                    return 4;
                case V1_13:
                case V1_14:
                case V1_15:
                case V1_16:
                default:
                    return 5;
            }
        }

        static int glowingIndex() {
            switch (MCVersion.getCurrentVersion()) {
                case V1_10:
                case V1_11:
                case V1_12:
                    return 5;
                case V1_13:
                case V1_14:
                case V1_15:
                case V1_16:
                default:
                    return 6;
            }
        }
    }

    protected final static class EntityMetaData {
        /**
         * @return Index inside DataWatcher the custom name is located.
         */
        static int customNameIndex() {
            switch (MCVersion.getCurrentVersion()) {
                case V1_10:
                case V1_11:
                case V1_12:
                case V1_13:
                case V1_14:
                case V1_15:
                case V1_16:
                default:
                    return 2;
            }
        }

        /**
         * @return Index inside DataWatcher the custom name visible is located.
         */
        static int nameVisible() {
            switch (MCVersion.getCurrentVersion()) {
                case V1_10:
                case V1_11:
                case V1_12:
                case V1_13:
                case V1_14:
                case V1_15:
                case V1_16:
                default:
                    return 3;
            }
        }

        /**
         * @return Index inside DataWatcher the invisibility is located.
         */
        static int gravityIndex() {
            switch (MCVersion.getCurrentVersion()) {
                case V1_10:
                case V1_11:
                case V1_12:
                case V1_13:
                case V1_14:
                case V1_15:
                case V1_16:
                default:
                    return 5;
            }
        }

        static int poseIndex() {
            switch (MCVersion.getCurrentVersion()) {
                case V1_10:
                case V1_11:
                case V1_12:
                case V1_13:
                case V1_14:
                case V1_15:
                case V1_16:
                default:
                    return 6;
            }
        }
    }
}