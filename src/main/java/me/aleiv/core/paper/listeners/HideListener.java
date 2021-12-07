package me.aleiv.core.paper.listeners;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import me.aleiv.core.paper.Core;

public class HideListener implements Listener {

    Core instance;

    public HideListener(Core instance) {
        this.instance = instance;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e){
        var game = instance.getGame();
        game.refreshHide();
    }

    @EventHandler
    public void onGamemode(PlayerGameModeChangeEvent e){
        var game = instance.getGame();
        Bukkit.getScheduler().runTaskLater(instance, task->{
            game.refreshHide();
        }, 1);

    }



}