package me.aleiv.core.paper.detection.events;

import org.bukkit.entity.Player;

import me.aleiv.core.paper.detection.objects.Polygon;

/**
 * This event is called when a player enters a polygon.
 * 
 * @author jcedeno
 */
public class PlayerEnterPolygonEvent extends BaseEvent {

    /**
     * Constructor for the event intended to be called by the plugin when a player
     * enters a polygon.
     * 
     * @param player  The player that entered the polygon.
     * @param polygon The polygon that the player entered.
     * @param async   Whether the event is asynchronous or not.
     */
    public PlayerEnterPolygonEvent(Player player, Polygon polygon, Boolean async) {
        super(player, polygon, async);
    }

}
