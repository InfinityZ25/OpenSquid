package me.aleiv.core.paper.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import lombok.Getter;

public class LeftWinsEvent extends Event {
    
    private static final @Getter HandlerList HandlerList = new HandlerList();
    @SuppressWarnings({"java:S116", "java:S1170"})
    private final @Getter HandlerList Handlers = HandlerList;


    public LeftWinsEvent(boolean async) {
        super(async);

    }

    public LeftWinsEvent() {
        this(false);
    }

}