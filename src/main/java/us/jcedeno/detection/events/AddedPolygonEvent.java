package us.jcedeno.detection.events;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import lombok.Getter;
import us.jcedeno.detection.objects.Polygon;

/**
 * Event that is fired when a polygon is added to the map.
 */
public class AddedPolygonEvent extends BaseEvent {
    private @Getter CommandSender sender;

    /**
     * Constructor for event fired when a player adds a polygon to the system.
     */
    public AddedPolygonEvent(CommandSender sender, Polygon polygon, boolean async) {
        super(sender instanceof Player ? (Player) sender : null, polygon, async);
        this.sender = sender;
    }

    /**
     * Cos
     * 
     * @param polygon
     * @param async
     */
    public AddedPolygonEvent(Polygon polygon, boolean async) {
        this(Bukkit.getConsoleSender(), polygon, async);
    }

}
