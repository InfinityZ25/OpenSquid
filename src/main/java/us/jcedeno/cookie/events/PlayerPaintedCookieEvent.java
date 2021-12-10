package us.jcedeno.cookie.events;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import lombok.Getter;
import us.jcedeno.cookie.objects.CookieMap;
import us.jcedeno.cookie.objects.Pixel;

/**
 * Event fired after {@link CookieMap} is painted. This event
 * should always be called after {@link PlayerClickedCookieEvent}.
 * 
 * @author jcedeno
 */
public class PlayerPaintedCookieEvent extends BaseEvent {

    private @Getter CookieMap cookieMap;
    private @Getter Player player;
    private @Getter Pixel pixel;

    /**
     * Basic constructor.
     * 
     * @param cookieMap {@link CookieMap} painted.
     * @param player    {@link Player} who painted.
     * @param pixel     {@link Pixel} painted.
     * @param async     {@link Boolean} whether the event is asynchronous or not.
     */
    public PlayerPaintedCookieEvent(CookieMap cookieMap, Player player, Pixel pixel, boolean async) {
        super(async);
        this.cookieMap = cookieMap;
        this.player = player;
        this.pixel = pixel;
    }

    /**
     * Static Constructor to create an instance of this event by providing the
     * cookie map, player and pixel color.
     * 
     * @param cookieMap The cookie map that was painted.
     * @param player    The player that painted the cookie map.
     * @param x         The x coordinate of the pixel that was painted.
     * @param z         The z coordinate of the pixel that was painted.
     * @param color     The color of the pixel that was painted.
     * @return The event instance.
     */
    public static PlayerPaintedCookieEvent of(CookieMap cookieMap, Player player, byte x, byte z, byte color) {
        return new PlayerPaintedCookieEvent(cookieMap, player, new Pixel(x, z, color), !Bukkit.isPrimaryThread());
    }

}
