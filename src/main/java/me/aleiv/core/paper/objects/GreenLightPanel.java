package me.aleiv.core.paper.objects;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import lombok.Data;
import lombok.EqualsAndHashCode;
import me.aleiv.core.paper.AnimationTools;
import me.aleiv.core.paper.Core;
import me.aleiv.core.paper.listeners.GreenLightListener;
import net.md_5.bungee.api.ChatColor;
import us.jcedeno.libs.rapidinv.ItemBuilder;
import us.jcedeno.libs.rapidinv.RapidInv;

@Data
@EqualsAndHashCode(callSuper=false)
public class GreenLightPanel extends RapidInv{

    GreenLightListener greenLightListener;
    Core instance;

    List<Player> playersMoved = new ArrayList<>();
    Boolean greenLight = true;
    Boolean door1 = false;
    Boolean door2 = false;
    Boolean door3 = false;
    Boolean headDoll = true;
     

    public GreenLightPanel(Core instance){
        super(6*9, "Green light-Red light");
        this.instance = instance;

        this.greenLightListener = new GreenLightListener(instance);

        updateEnableDisable();
        updateMovedPlayers();

        updateDoor1();
        updateDoor2();
        updateDoor3();

        updateHeadDoll();

        
    }

    public void greenLight(Boolean bool){
        if(bool){
            greenLight = true;
            playersMoved.clear();
            instance.unregisterListener(greenLightListener);
            
        }else{
            greenLight = false;
            playersMoved.clear();
            instance.registerListener(greenLightListener);
        }
        updateEnableDisable();
        updateMovedPlayers();
    }

    public void updateEnableDisable(){

        var enableDisable = new ItemBuilder(greenLight ? Material.GREEN_DYE : Material.RED_DYE)
            .name(greenLight ? ChatColor.GREEN + "Green light" : ChatColor.RED + "Red light").build();

        this.setItem(1, enableDisable, action ->{
            if(greenLight){
                greenLight = false;
                instance.registerListener(greenLightListener);

            }else{
                greenLight = true;
                playersMoved.clear();
                instance.unregisterListener(greenLightListener);
            }
            updateEnableDisable();
            updateMovedPlayers();
        });
        
    }

    public void updateDoor1(){
        var door = new ItemBuilder(Material.DARK_PRISMARINE)
            .name(!door1 ? ChatColor.GREEN + "OPEN DOOR 1" : ChatColor.RED + "CLOSE DOOR 1").build();

        this.setItem(2, door, action ->{
            door1 = !door1;
            var bool = door1;
            var player = (Player) action.getWhoClicked();
            instance.getGame().getDollGame().dollDoor1(bool);
            instance.adminMessage(ChatColor.GREEN + player.getName() + " DOOR 1 " + bool);
            updateDoor1();
        });

    }

    public void updateDoor2(){
        var door = new ItemBuilder(Material.DARK_PRISMARINE)
            .name(!door2 ? ChatColor.GREEN + "OPEN DOOR 2" : ChatColor.RED + "CLOSE DOOR 2").build();

        this.setItem(3, door, action ->{
            door2 = !door2;
            var bool = door2;
            var player = (Player) action.getWhoClicked();
            instance.getGame().getDollGame().dollDoor2(bool);
            instance.adminMessage(ChatColor.GREEN + player.getName() + " DOOR 2 " + bool);
            updateDoor2();
        });

    }

    public void updateDoor3(){
        var door = new ItemBuilder(Material.DARK_PRISMARINE)
            .name(!door3 ? ChatColor.GREEN + "OPEN DOOR 3" : ChatColor.RED + "CLOSE DOOR 3").build();

        this.setItem(4, door, action ->{
            door3 = !door3;
            var bool = door3;
            var player = (Player) action.getWhoClicked();
            instance.getGame().getDollGame().dollDoor3(bool);
            instance.adminMessage(ChatColor.GREEN + player.getName() + " DOOR 3 " + bool);
            updateDoor3();
        });

    }

    public void updateHeadDoll(){
        var head = new ItemBuilder(Material.BRICK).meta(meta -> meta.setCustomModelData(21))
            .name(headDoll ? ChatColor.GREEN + "GREEN LIGHT" : ChatColor.RED + "RED LIGHT").build();

        this.setItem(5, head, action ->{
            headDoll = !headDoll;
            var bool = headDoll;
            var player = (Player) action.getWhoClicked();
            instance.getGame().getDollGame().dollHead(bool);
            instance.adminMessage(ChatColor.GREEN + player.getName() + " HEAD DOLL " + bool);
            updateHeadDoll();
        });

    }

    public void cleanMovedPlayers(){
        for (int i = 9; i < 54; i++) {
            this.setItem(i, new ItemStack(Material.AIR));
        }
    }

    public void updateMovedPlayers(){
        cleanMovedPlayers();
        var count = 9;
        for (var player : playersMoved) {
            if(count == 53) return;

            var head = new ItemBuilder(Material.PLAYER_HEAD)
                    .meta(SkullMeta.class, meta -> meta.setOwningPlayer(Bukkit.getOfflinePlayer(player.getUniqueId())))
                    .name(ChatColor.YELLOW + player.getName()).build();

            this.setItem(count, head, action ->{
                shoot(player);
                playersMoved.remove(player);
                updateMovedPlayers();
            });

            count++;
        }
        
    }

    public void shoot(Player player){
        AnimationTools.shootLocation(player.getLocation());
    }

}
