package uk.lewdev.entitylib;

import com.comphenix.protocol.ProtocolLibrary;

import org.jetbrains.annotations.NotNull;

import me.aleiv.core.paper.Core;
import uk.lewdev.entitylib.entity.protocol.EntityAsyncRenderTicker;
import uk.lewdev.entitylib.entity.protocol.FakeEntity;
import uk.lewdev.entitylib.event.ProtocolLibListeners;
import uk.lewdev.entitylib.utils.EntityIdProvider;
import uk.lewdev.entitylib.utils.MCVersion;

/**
 * @author Lewys Davies (Lew_)
 */
public class FakeEntityPlugin implements FakeEntityAPI {

    static FakeEntityPlugin instance;
    private EntityIdProvider entityIdProvider;

    public void onEnable() {
        instance = this;

        this.entityIdProvider = MCVersion.getCurrentVersion().getProvider();

        new EntityAsyncRenderTicker().runTaskTimerAsynchronously(Core.getInstance(), 10, 10);
        new ProtocolLibListeners(this);
    }

    public void onDisable() {
        FakeEntity.ALL_ALIVE_INSTANCES.values().forEach(FakeEntity::destroy);
        ProtocolLibrary.getProtocolManager().removePacketListeners(Core.getInstance());
    }

    @NotNull
    public EntityIdProvider getEntityIdProvider() {
        return this.entityIdProvider;
    }
}