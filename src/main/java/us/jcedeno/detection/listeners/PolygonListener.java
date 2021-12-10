package us.jcedeno.detection.listeners;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import net.kyori.adventure.text.minimessage.MiniMessage;
import us.jcedeno.detection.events.PlayerEnterPolygonEvent;
import us.jcedeno.detection.events.PlayerExitPloygonEvent;

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
