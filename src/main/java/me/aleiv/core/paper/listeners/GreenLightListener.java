package me.aleiv.core.paper.listeners;

import org.bukkit.GameMode;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import me.aleiv.core.paper.AnimationTools;
import me.aleiv.core.paper.Core;

public class GreenLightListener implements Listener {

    Core instance;

    public GreenLightListener(Core instance) {
        this.instance = instance;
    }

    @EventHandler
    public void move(PlayerMoveEvent e) {
        var game = instance.getGame();
        var player = e.getPlayer();
        var point = player.getLocation();
        var doll = game.getDollGame();
        var pos1 = doll.getPos1();
        var pos2 = doll.getPos2();
        
        if (player.getGameMode() == GameMode.ADVENTURE && game.isPlayer(player) && AnimationTools.isInCube(pos1, pos2, point)){
            
            var panel = game.getDollGame().getGreenLightPanel();
            var moved = panel.getPlayersMoved();
            if(!moved.contains(player)){
                moved.add(player);
                panel.updateMovedPlayers();
            }
        }

    }
}
