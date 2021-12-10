package us.jcedeno.cookie.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import lombok.Getter;

/**
 * Boiler-plate event designed to quickyl create more events without having to
 * worry about bukkit's stupid event system.
 * 
 * @author jcedeno
 */
public class BaseEvent extends Event {

    private static final @Getter HandlerList HandlerList = new HandlerList();
    @SuppressWarnings({ "java:S116", "java:S1170" })
    private final @Getter HandlerList Handlers = HandlerList;

    public BaseEvent(boolean async) {
        super(async);
    }

}
