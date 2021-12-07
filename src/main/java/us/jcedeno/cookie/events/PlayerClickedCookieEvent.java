package us.jcedeno.cookie.events;

import org.bukkit.Location;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;

import lombok.Getter;
import us.jcedeno.guns.event.BaseEvent;

/**
 * An event that is fired when a player clicks a specific in a cookie canvas.
 * 
 * @author jcedeno
 */
public class PlayerClickedCookieEvent extends BaseEvent {
    private @Getter Location interactionPoint;
    private @Getter Player player;
    private @Getter ItemFrame itemFrame;

    public PlayerClickedCookieEvent(Location interactionPoint, Player player, ItemFrame itemFrame, boolean async) {
        super(async);
        this.interactionPoint = interactionPoint;
        this.player = player;
        this.itemFrame = itemFrame;
    }
}
