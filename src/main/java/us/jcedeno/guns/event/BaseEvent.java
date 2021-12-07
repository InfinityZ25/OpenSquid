package us.jcedeno.guns.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import lombok.Getter;

/**
 * An event containing all the boiler plate required for bukkit to read events.
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
