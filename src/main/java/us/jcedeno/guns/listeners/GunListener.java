package us.jcedeno.guns.listeners;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityShootBowEvent;

/**
 * A bukkit listener to handle the events related to the gun logic.
 * 
 * @author jcedeno
 */
public class GunListener implements Listener {

    @EventHandler
    public void onBowShot(EntityShootBowEvent e) {
        if (e.getEntity() instanceof Player p) {
            var bow = e.getBow();
            // If crossbow, it should be a smg
            if (bow.getType() == Material.CROSSBOW) {
                // TODO: Continue with the logic here.

            }

        }

    }

}
