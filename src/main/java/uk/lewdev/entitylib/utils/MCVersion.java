package uk.lewdev.entitylib.utils;

import org.bukkit.Bukkit;
import uk.lewdev.entitylib.utils.nms.EntityIdProvider1_14;
import uk.lewdev.entitylib.utils.nms.EntityIdProvider1_17;
import uk.lewdev.entitylib.utils.nms.EntityIdProvider1_9;

import java.util.function.Supplier;

public enum MCVersion {

    V1_10("1.10", EntityIdProvider1_9::new),
    V1_11("1.11", EntityIdProvider1_9::new),
    V1_12("1.12", EntityIdProvider1_9::new),
    V1_13("1.13", EntityIdProvider1_9::new),
    V1_14("1.14", EntityIdProvider1_14::new),
    V1_15("1.15", EntityIdProvider1_14::new),
    V1_16("1.16", EntityIdProvider1_14::new),
    V1_17("1.17", EntityIdProvider1_17::new);

    private static MCVersion CUR_VERSION = V1_10;

    static {
        String curVerStr = Bukkit.getBukkitVersion();

        for (MCVersion version : MCVersion.values()) {
            if (curVerStr.toLowerCase().contains(version.versionString)) {
                CUR_VERSION = version;
                break;
            }
        }
    }

    private final String versionString;
    private final Supplier<EntityIdProvider> provider;

    MCVersion(String verStr, Supplier<EntityIdProvider> provider) {
        this.versionString = verStr;
        this.provider = provider;
    }

    public static MCVersion getCurrentVersion() {
        return CUR_VERSION;
    }

    public String toMinecraftVersionStr() {
        return this.versionString;
    }

    public EntityIdProvider getProvider() {
        return this.provider.get();
    }

    public boolean isAfter1_14() {
        return this.ordinal() > MCVersion.V1_14.ordinal();
    }

    public boolean isAfterOr1_16() {
        return this.ordinal() >= MCVersion.V1_16.ordinal();
    }

    public boolean isAfterOr1_17() {
        return this.ordinal() >= MCVersion.V1_17.ordinal();
    }
}