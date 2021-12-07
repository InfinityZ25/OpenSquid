package uk.lewdev.entitylib.entity;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import com.comphenix.protocol.wrappers.EnumWrappers.NativeGameMode;
import com.comphenix.protocol.wrappers.EnumWrappers.PlayerInfoAction;
import com.comphenix.protocol.wrappers.PlayerInfoData;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.WrappedDataWatcher.Registry;
import com.comphenix.protocol.wrappers.WrappedDataWatcher.WrappedDataWatcherObject;
import com.comphenix.protocol.wrappers.WrappedGameProfile;
import com.comphenix.protocol.wrappers.WrappedSignedProperty;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import me.aleiv.core.paper.Core;
import uk.lewdev.entitylib.entity.protocol.FakeEquippableEntity;
import uk.lewdev.entitylib.entity.protocol.wrappers.WrapperPlayServerNamedEntitySpawn;
import uk.lewdev.entitylib.entity.protocol.wrappers.WrapperPlayServerPlayerInfo;
import uk.lewdev.entitylib.entity.protocol.wrappers.WrapperPlayServerScoreboardTeam;
import uk.lewdev.entitylib.utils.AngleUtil;
import uk.lewdev.entitylib.utils.MCVersion;

/**
 * https://mineskin.org/ https://namemc.com/skin/4056dde9cc057115
 *
 * @author Lewys Davies (Lew_)
 * @version 1.15
 */
public class FakePlayer extends FakeEquippableEntity {

    private static final String DEFAULT_SKIN = "eyJ0aW1lc3RhbXAiOjE1NjIwNzc3ODMzMzksInByb2ZpbGVJZCI6IjdkYTJhYjNhOTNjYTQ4ZWU4MzA0OGFmYzNiODBlNjhlIiwicHJvZmlsZU5hbWUiOiJHb2xkYXBmZWwiLCJzaWduYXR1cmVSZXF1aXJlZCI6dHJ1ZSwidGV4dHVyZXMiOnsiU0tJTiI6eyJ1cmwiOiJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzkzYjUwZDdlODM4M2Y2NTZhODY1YTlhZjA1ZTNmYzk3ZWM2ZWZmOGNlYzU0NmFkZmI1MGI1M2UzZGU0ZGU2MjYifX19";
    private static final String DEFAULT_SKIN_SIG = "dubcivWbARSmeEN1NJg89tvVFUuiFwZ3NBmPLNlUX4qwSDg3eqHyCEcl8fRu5Cf9R4Y4YQ782K+WtUszeRkkywChtZtyJ1jF7YbD2UDOH6N/gqTBETiKgkGrXyJ6idIfQKZ1LRhCAlj44K7bpW5OTjyyZESTmWkGc4AEXrIfEtGcvRA5oKk2JencxOwtoTnRkwMOBcZ1mezdHJGv5/iLc45B0P19SOo4yxlRM1zX/88g2euQyy+QUcXa2lROhIaARjDpvwd8BePWA0xnKD+T7h/UXl5FTouDCntcI8w0lOpo7FEOwgtMvHXo788iIZ4rJ2LWbHCIki7Dboj7ILyrAOXITCiOfYnn88ZDLW0bnah06Mqk/XkmzqamkYL8KsBAQK2u6e5mgOL0kzyRj3vKsWpbzQNFYtRDbVfaEBN+OleyeQTWlSPn2Ka7g9IzkQ21lrkYc683eP4FZABBCsPeKyXpzU0A4DjuG5WHitdsMue81CfbxwSkCgUI5DU/LDgbWDl+4S+MeLhZKg/cK+AmbPhDU9/KnGoHknUts7PhZOrz0qDsgCqOsPpYtH5SXMuSA0Anu5ozikbAdMnMVS8G4scKl0WengVKBkL+fGe6m/J3JFFyraoOQ0mL4pIUZd05MxGtpaMs2OROsn/lbH6l5dhgcsJIsKwStO6RUl87TBs=";

    private final WrappedGameProfile playerProfile;

    private WrapperPlayServerScoreboardTeam teamPacket;

    private final WrappedDataWatcherObject playerByteWatcher = new WrappedDataWatcherObject(PlayerMetaData.byteIndex(),
            Registry.get(Byte.class));

    private final String name;

    public static FakePlayer of(String name, Location loc, String texture, String signature) {
        return new FakePlayer(name, true, true, loc.getWorld(), texture, signature, loc.getX(), loc.getY(), loc.getZ(),
                loc.getYaw(), loc.getPitch(), loc.getYaw());
    }

    public FakePlayer(String name, Boolean hideTag, Boolean collide, World world, String texture, String signature,
            double x, double y, double z, float yaw, float headPitch, float headYaw) {
        super(EntityType.PLAYER, UUID.randomUUID(), world, x, y, z, yaw, headPitch, headYaw);

        this.name = name;
        super.setCustomName(this.name);

        this.playerProfile = new WrappedGameProfile(super.getUUID(), this.name);
        this.playerProfile.getProperties().put("textures", new WrappedSignedProperty("textures", texture, signature));

        if (hideTag)
            teamPacket = generatePacketFor(name, collide);

        showSecondSkinLayer(true);

    }

    public FakePlayer(String name, Boolean hideTag, Boolean collide, Location loc, float headYaw) {
        this(name, hideTag, collide, loc.getWorld(), DEFAULT_SKIN, DEFAULT_SKIN_SIG, loc.getX(), loc.getY(), loc.getZ(),
                loc.getYaw(), loc.getPitch(), headYaw);
    }

    public FakePlayer(String name, Boolean hideTag, Boolean collide, World world, double x, double y, double z) {
        this(name, hideTag, collide, world, DEFAULT_SKIN, DEFAULT_SKIN_SIG, x, y, z, 0, 0, 0);
    }

    public FakePlayer(OfflinePlayer player, Boolean hideTag, Boolean collide, Location loc) {
        this(Objects.requireNonNull(player.getPlayer()), hideTag, collide, loc);
    }

    public FakePlayer(Player player, Boolean hideTag, Boolean collide, Location loc) {
        super(EntityType.PLAYER, UUID.randomUUID(), loc.getWorld(), loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(),
                loc.getPitch(), loc.getYaw());

        this.name = player.getName();
        super.setCustomName(this.name);

        WrappedGameProfile actualProfile = WrappedGameProfile.fromPlayer(player);
        this.playerProfile = new WrappedGameProfile(super.getUUID(), this.name);
        this.playerProfile.getProperties().put("textures",
                actualProfile.getProperties().get("textures").iterator().next());

        if (hideTag)
            teamPacket = generatePacketFor(name, collide);

        showSecondSkinLayer(true);
    }

    public FakePlayer(WrappedGameProfile profile, Boolean hideTag, Boolean collide, Location loc) {
        super(EntityType.PLAYER, profile.getUUID(), loc.getWorld(), loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(),
                loc.getPitch(), loc.getYaw());

        this.name = profile.getName().substring(0, Math.min(profile.getName().length(), 15));
        super.setCustomName(this.name);

        this.playerProfile = profile;

        if (hideTag)
            teamPacket = generatePacketFor(name, collide);

        showSecondSkinLayer(true);
    }

    private static WrapperPlayServerScoreboardTeam generatePacketFor(String name, boolean collide) {
        WrapperPlayServerScoreboardTeam teamPacket = new WrapperPlayServerScoreboardTeam();
        teamPacket.setMode(0);
        teamPacket.setNameTagVisibility("never");
        if (!collide)
            teamPacket.setCollisionRule("never");
        teamPacket.setName(UUID.randomUUID().toString().split("-")[0]);
        teamPacket.setPlayers(List.of(name));
        return teamPacket;
    }

    public final String getName() {
        return this.name;
    }

    public final void showSecondSkinLayer(boolean enabled) {
        super.getDataWatcher().setObject(this.playerByteWatcher, enabled ? Byte.MAX_VALUE : 0);
        super.sendMetaUpdate();
    }

    @Override
    protected void sendSpawnPacket(Player player) {
        this.assertNotDead();

        WrapperPlayServerPlayerInfo playerInfoPacket = new WrapperPlayServerPlayerInfo();
        playerInfoPacket.setAction(PlayerInfoAction.ADD_PLAYER);
        playerInfoPacket.setData(this.getPlayerInfoData());

        WrapperPlayServerNamedEntitySpawn playerSpawnPacket = new WrapperPlayServerNamedEntitySpawn();

        playerSpawnPacket.setEntityID(super.getEntityId());
        playerSpawnPacket.setPlayerUUID(super.getUUID());

        playerSpawnPacket.setX(super.getX());
        playerSpawnPacket.setY(super.getY());
        playerSpawnPacket.setZ(super.getZ());
        playerSpawnPacket.setYaw(AngleUtil.fromDegrees(super.getYaw()));
        playerSpawnPacket.setPitch(AngleUtil.fromDegrees(super.getPitch()));

        // Spawn
        try {
            protocol.sendServerPacket(player, playerInfoPacket.getHandle());
            protocol.sendServerPacket(player, playerSpawnPacket.getHandle());
            if (teamPacket != null)
                protocol.sendServerPacket(player, teamPacket.getHandle());
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

        // Set head yaw to current
        this.sendHeadYawPacket(player);
        this.sendMetaUpdate();

        Bukkit.getScheduler().runTaskLater(Core.getInstance(), () -> {
            playerInfoPacket.setAction(PlayerInfoAction.REMOVE_PLAYER);
            try {
                protocol.sendServerPacket(player, playerInfoPacket.getHandle());
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }, 25);
    }

    private List<PlayerInfoData> getPlayerInfoData() {
        PlayerInfoData data = new PlayerInfoData(this.playerProfile, 1, NativeGameMode.CREATIVE,
                WrappedChatComponent.fromText(this.name));

        List<PlayerInfoData> dataList = new ArrayList<PlayerInfoData>();
        dataList.add(data);

        return dataList;
    }

    private enum PlayerMetaData {
        MC1_15(16), MC1_17(17);

        private final int byteIndex;

        PlayerMetaData(int playerByte) {
            this.byteIndex = playerByte;
        }

        private static PlayerMetaData get() {
            if (MCVersion.getCurrentVersion().ordinal() >= MCVersion.V1_17.ordinal()) {
                return MC1_17;
            }
            return MC1_15;
        }

        public static int byteIndex() {
            return get().byteIndex;
        }
    }
}