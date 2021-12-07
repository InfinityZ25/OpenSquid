package uk.lewdev.entitylib.entity.protocol;

import org.bukkit.scheduler.BukkitRunnable;

/**
 * @author Lewys Davies (Lew_)
 */
public class EntityAsyncRenderTicker extends BukkitRunnable {

    @Override
    public void run() {
        FakeEntity.ALL_ALIVE_INSTANCES.values().forEach(fakeEntity -> fakeEntity.getVisibilityHandler().tick());
    }
}
