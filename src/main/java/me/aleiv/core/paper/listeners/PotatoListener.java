package me.aleiv.core.paper.listeners;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import me.aleiv.core.paper.Core;
import net.md_5.bungee.api.ChatColor;
import us.jcedeno.libs.rapidinv.ItemBuilder;

public class PotatoListener implements Listener {

    Core instance;

    ItemStack potato = new ItemBuilder(Material.RABBIT_FOOT).name(ChatColor.RED + "POTATO").build();
    
    public PotatoListener(Core instance) {
        this.instance = instance;
    }


    @EventHandler
    public void onDamage(EntityDamageByEntityEvent e){
        var entity = e.getEntity();
        var damager = e.getDamager();
        if(entity instanceof Player player && damager instanceof Player damagerPlayer){
            var invDamager = damagerPlayer.getInventory();
            var invPlayer = player.getInventory();

            if(invDamager.contains(Material.RABBIT_FOOT) && !invPlayer.contains(Material.RABBIT_FOOT)){
                var loc1 = player.getLocation();
                var loc2 = damagerPlayer.getLocation();

                damagerPlayer.playSound(loc1, "squid:sfx.potato_pass", 1, 1);
                player.removePotionEffect(PotionEffectType.GLOWING);

                player.playSound(loc2, "squid:sfx.potato_receive", 1, 1);
                damagerPlayer.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 10000*20, 0, false, false, false));
                

                invDamager.remove(Material.RABBIT_FOOT);
                invPlayer.addItem(potato);
            }

        }
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent e){
        var item = e.getItemDrop().getItemStack();
        if(item.getType() ==  Material.RABBIT_FOOT){
            e.setCancelled(true);
        }

    }
}
