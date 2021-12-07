package me.aleiv.core.paper.listeners;

import java.util.List;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerTakeLecternBookEvent;
import org.bukkit.event.world.WorldLoadEvent;

import io.papermc.paper.event.player.AsyncChatEvent;
import me.aleiv.core.paper.Core;

public class CanceledListener implements Listener {

    Core instance;

    List<Material> bannedMoveList = List.of(Material.NOTE_BLOCK);
    List<Material> bannedSpawnList = List.of(Material.TWISTING_VINES);
    List<Integer> customModelAntiDrop = List.of(25, 24);

    public CanceledListener(Core instance) {
        this.instance = instance;
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void checkForMute(AsyncChatEvent e) {
        if (!e.getPlayer().hasPermission("globalmute.talk")) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onSpawn(CreatureSpawnEvent e){
        if(e.getSpawnReason().toString().contains("NATURAL")){
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onDamage(EntityDamageEvent e){
        var game = instance.getGame();
        var entity = e.getEntity();
        if(entity instanceof Player player && game.isGuard(player)){
            e.setDamage(1);
        }
    }

    @EventHandler
    public void cancelInteract(PlayerInteractEvent e){
        var player = e.getPlayer();
        var block = e.getClickedBlock();
        var action = e.getAction();

        if(block != null && block.getType() == Material.BIRCH_TRAPDOOR){
            e.setCancelled(true);

            var face = e.getBlockFace();
            if(face == BlockFace.DOWN){
                var loc = block.getRelative(BlockFace.UP).getLocation();
                player.teleport(loc);

            }else if(face == BlockFace.UP){
                var loc = block.getRelative(BlockFace.DOWN).getLocation();
                player.teleport(loc);
            }
            

            return;
        }

        if(block == null || action != Action.RIGHT_CLICK_BLOCK || player.getGameMode() == GameMode.CREATIVE) return;
        
        if(bannedMoveList.contains(block.getType()) || block.getType().toString().contains("TRAPDOOR")
                || block.getType().toString().contains("FENCE_GATE") || (block.getType().toString().contains("JUNGLE_DOOR") && !player.hasPermission("guard.perm"))){
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void inventoryOpen(InventoryOpenEvent e){
        var player = e.getPlayer();
        var inv = e.getInventory();
        if(player.getGameMode() != GameMode.CREATIVE && !inv.getType().toString().contains("CHEST") && !inv.getType().toString().contains("BARREL") 
            && !inv.getType().toString().contains("LECTERN")){
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void itemSpawn(ItemSpawnEvent e){
        var item = e.getEntity().getItemStack();
        if(bannedSpawnList.contains(item.getType())){
            e.setCancelled(true);
        }
    }

    
    @EventHandler
    public void onWorldLoad(WorldLoadEvent e){
        var world = e.getWorld();
        var game = instance.getGame();
        var doll = game.getDollGame();
        doll.setPos1(new Location(world, 470, 65, 22));
        doll.setPos2(new Location(world, 326, 80, 106));
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent e){
        var item = e.getItemDrop().getItemStack().getItemMeta();
        if(item.hasCustomModelData() && customModelAntiDrop.contains(item.getCustomModelData())){
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerLectern(PlayerTakeLecternBookEvent e) {
        var player = e.getPlayer();
        var game = instance.getGame();
        if(game.isPlayer(player)){
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void craft(CraftItemEvent e){
        e.setCancelled(true);
    }

}



