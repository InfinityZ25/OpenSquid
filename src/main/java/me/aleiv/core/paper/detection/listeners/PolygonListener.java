package me.aleiv.core.paper.detection.listeners;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import me.aleiv.core.paper.detection.events.PlayerEnterPolygonEvent;
import me.aleiv.core.paper.detection.events.PlayerExitPloygonEvent;
import net.kyori.adventure.text.minimessage.MiniMessage;

public class PolygonListener implements Listener {

    @EventHandler
    public void onEnteringPolygon(PlayerEnterPolygonEvent e) {
        Bukkit.broadcast(MiniMessage.get().parse(
                "<green>Player " + e.getPlayer().getName() + " has entered <white>" + e.getPolygon().toString()));
    }

    @EventHandler
    public void onLeavingPolygon(PlayerExitPloygonEvent e) {
        Bukkit.broadcast(MiniMessage.get()
                .parse("<red>Player " + e.getPlayer().getName() + " has exited <white>" + e.getPolygon().toString()));

    }

}
