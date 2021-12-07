package me.aleiv.core.paper.detection.events;

import org.bukkit.entity.Player;

import me.aleiv.core.paper.detection.objects.Polygon;

/**
 * This event is called when a player exits a polygon.
 * 
 * @author jcedeno
 */
public class PlayerExitPloygonEvent extends BaseEvent {

    /**
     * Constructor for the event intended to be called by the plugin when a player
     * exits a polygon.
     * 
     * @param player  The player that exited the polygon.
     * @param polygon The polygon that the player exited.
     * @param async   Whether the event is asynchronous or not.
     */
    public PlayerExitPloygonEvent(Player player, Polygon polygon, boolean async) {
        super(player, polygon, async);
    }
}
