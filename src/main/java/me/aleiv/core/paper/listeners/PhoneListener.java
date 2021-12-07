package me.aleiv.core.paper.listeners;

import org.bukkit.entity.Player;
import org.bukkit.entity.Ravager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import me.aleiv.core.paper.Core;

public class PhoneListener implements Listener {
    Core instance;

    public PhoneListener(Core instance) {
        this.instance = instance;
    }

    @EventHandler
    public void onSpawn(CreatureSpawnEvent e){
        var entity = e.getEntity();
        if(entity instanceof Ravager ravager){
            ravager.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 10000*20, 2, false, false));

        }
    }

    @EventHandler
    public void onTarget(EntityTargetEvent e){
        var entity = e.getEntity();
        var game = instance.getGame().getPhoneGame();
        var target = e.getTarget();
        var uuids = game.getTargetList();
        if(entity instanceof Ravager ravager && target instanceof Player player && !uuids.contains(player.getUniqueId())){
            e.setCancelled(true);
        }

    }

    @EventHandler
    public void onDeath(PlayerDeathEvent e){
        var player = e.getEntity();
        var game = instance.getGame().getPhoneGame();
        var uuids = game.getTargetList();
        var uuid = player.getUniqueId();
        if(uuids.contains(uuid)){
            uuids.remove(uuid);
        }
    }
}
