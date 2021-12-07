package me.aleiv.core.paper.listeners;

import java.util.List;

import com.destroystokyo.paper.ParticleBuilder;

import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.entity.ArmorStand;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;

import me.aleiv.core.paper.Core;
import me.aleiv.core.paper.events.GameTickEvent;

public class ChairListener implements Listener {

    Core instance;

    public ChairListener(Core instance) {
        this.instance = instance;
    }

    List<Integer> sitModels = List.of(40, 23, 53, 60, 61, 66, 67, 68, 69);
    
    @EventHandler
    public void gameTickEvent(GameTickEvent e){
        var game = instance.getGame();
        var chairGame = game.getChairGame();
        var music = chairGame.isMusic();
        if(music){
            var notes = chairGame.getDiscoNotes();
            Bukkit.getScheduler().runTask(instance, task->{
                
                notes.forEach(note ->{
                    new ParticleBuilder(Particle.NOTE).location(note).receivers(60).force(true).count(50).offset(1, 1, 1).extra(150).spawn();
                });
                Bukkit.getScheduler().runTaskLater(instance, task2 ->{
                    notes.forEach(note ->{
                        new ParticleBuilder(Particle.NOTE).location(note).receivers(60).force(true).count(50).offset(1, 1, 1).extra(150).spawn();
                    });
                }, 10);
            });
        }
    }

    @EventHandler
    public void onJoin(PlayerInteractAtEntityEvent e){
        var entity = e.getRightClicked();
        if(entity instanceof ArmorStand stand){
            var equip = stand.getEquipment();
            var brick = equip.getHelmet();
            if(brick != null && brick.hasItemMeta() && brick.getItemMeta().hasCustomModelData()){
                var model = brick.getItemMeta().getCustomModelData();
                if(sitModels.contains(model) && stand.getPassengers().isEmpty()){
                    var player = e.getPlayer();
                    stand.addPassenger(player);
                }
            }   
        }
    }
}
