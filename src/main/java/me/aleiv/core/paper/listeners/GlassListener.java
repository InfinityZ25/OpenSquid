package me.aleiv.core.paper.listeners;

import org.bukkit.GameMode;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

import me.aleiv.core.paper.Core;

public class GlassListener implements Listener {

    Core instance;

    public GlassListener(Core instance) {
        this.instance = instance;
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e){
        var block = e.getClickedBlock();
        var player = e.getPlayer();
        var glass = instance.getGame().getGlassGame();
        if(block != null && player.getGameMode() == GameMode.CREATIVE && !player.isSneaking() && glass.isGlass(block.getType())){
            glass.breakGlass(block, true);
            
        }
    }

}