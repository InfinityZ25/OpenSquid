package me.aleiv.core.paper.detection.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import lombok.Getter;
import me.aleiv.core.paper.detection.objects.Polygon;

public class BaseEvent extends Event {

    private static final @Getter HandlerList HandlerList = new HandlerList();
    @SuppressWarnings({ "java:S116", "java:S1170" })
    private final @Getter HandlerList Handlers = HandlerList;
    protected Player player;
    protected Polygon polygon;

    public BaseEvent(Player player, Polygon polygon, boolean async) {
        super(async);
        this.player = player;
        this.polygon = polygon;
    }

    /**
     * @return The polygon involved in the event.
     */
    public Polygon getPolygon() {
        return polygon;
    }

    /**
     * @return The player involved in the event.
     */
    public Player getPlayer() {
        return player;

    }

}
