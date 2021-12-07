package me.aleiv.core.paper.Games;

import org.bukkit.Bukkit;
import org.bukkit.Material;

import me.aleiv.core.paper.AnimationTools;
import me.aleiv.core.paper.Core;

public class CookieGame {
    Core instance;
    
    public CookieGame(Core instance){
        this.instance = instance;

    }

    public void cookieDoor1(Boolean bool){
        var specialObjects = AnimationTools.specialObjects;
        var world = Bukkit.getWorld("world");
        var loc1 = AnimationTools.parseLocation(specialObjects.get("COOKIE_DOOR1_POS1"), world);
        var loc2 = AnimationTools.parseLocation(specialObjects.get("COOKIE_DOOR1_POS2"), world);
        if(bool){

            AnimationTools.fill(loc1, loc2, Material.AIR);

            AnimationTools.playSoundDistance(loc1, 50, "squid:sfx.cookie_symbol_open", 1f, 1f);

            AnimationTools.rotate("COOKIE_DOOR1_LEFT", -20, 1, 0.1f);
            AnimationTools.rotate("COOKIE_DOOR1_RIGHT", 20, 1, 0.1f);

        }else{

            AnimationTools.fill(loc1, loc2, Material.BARRIER);

            AnimationTools.playSoundDistance(loc1, 50, "squid:sfx.cookie_symbol_close", 1f, 1f);

            AnimationTools.rotate("COOKIE_DOOR1_LEFT", 20, 1, 0.1f);
            AnimationTools.rotate("COOKIE_DOOR1_RIGHT", -20, 1, 0.1f);

        }
    }

    public void cookieDoor2(Boolean bool){
        var specialObjects = AnimationTools.specialObjects;
        var world = Bukkit.getWorld("world");
        var loc1 = AnimationTools.parseLocation(specialObjects.get("COOKIE_DOOR2_POS1"), world);
        var loc2 = AnimationTools.parseLocation(specialObjects.get("COOKIE_DOOR2_POS2"), world);
        if(bool){

            AnimationTools.fill(loc1, loc2, Material.AIR);

            AnimationTools.playSoundDistance(loc1, 50, "squid:sfx.cookie_symbol_open", 1f, 1f);

            AnimationTools.rotate("COOKIE_DOOR2_LEFT", -20, 1, 0.1f);
            AnimationTools.rotate("COOKIE_DOOR2_RIGHT", 20, 1, 0.1f);

        }else{

            AnimationTools.fill(loc1, loc2, Material.BARRIER);

            AnimationTools.playSoundDistance(loc1, 50, "squid:sfx.cookie_symbol_close", 1f, 1f);

            AnimationTools.rotate("COOKIE_DOOR2_LEFT", 20, 1, 0.1f);
            AnimationTools.rotate("COOKIE_DOOR2_RIGHT", -20, 1, 0.1f);

        }
    }

    public void cookieDoor3(Boolean bool){
        var specialObjects = AnimationTools.specialObjects;
        var world = Bukkit.getWorld("world");
        var loc1 = AnimationTools.parseLocation(specialObjects.get("COOKIE_DOOR3_POS1"), world);
        var loc2 = AnimationTools.parseLocation(specialObjects.get("COOKIE_DOOR3_POS2"), world);
        if(bool){

            AnimationTools.fill(loc1, loc2, Material.AIR);

            AnimationTools.playSoundDistance(loc1, 50, "squid:sfx.cookie_symbol_open", 1f, 1f);

            AnimationTools.rotate("COOKIE_DOOR3_LEFT", -20, 1, 0.1f);
            AnimationTools.rotate("COOKIE_DOOR3_RIGHT", 20, 1, 0.1f);

        }else{

            AnimationTools.fill(loc1, loc2, Material.BARRIER);

            AnimationTools.playSoundDistance(loc1, 50, "squid:sfx.cookie_symbol_close", 1f, 1f);

            AnimationTools.rotate("COOKIE_DOOR3_LEFT", 20, 1, 0.1f);
            AnimationTools.rotate("COOKIE_DOOR3_RIGHT", -20, 1, 0.1f);

        }
    }

    public void cookieDoor4(Boolean bool){
        var specialObjects = AnimationTools.specialObjects;
        var world = Bukkit.getWorld("world");
        var loc1 = AnimationTools.parseLocation(specialObjects.get("COOKIE_DOOR4_POS1"), world);
        var loc2 = AnimationTools.parseLocation(specialObjects.get("COOKIE_DOOR4_POS2"), world);
        if(bool){

            AnimationTools.fill(loc1, loc2, Material.AIR);

            AnimationTools.playSoundDistance(loc1, 50, "squid:sfx.cookie_symbol_open", 1f, 1f);

            AnimationTools.rotate("COOKIE_DOOR4_LEFT", -20, 1, 0.1f);
            AnimationTools.rotate("COOKIE_DOOR4_RIGHT", 20, 1, 0.1f);

        }else{

            AnimationTools.fill(loc1, loc2, Material.BARRIER);

            AnimationTools.playSoundDistance(loc1, 50, "squid:sfx.cookie_symbol_close", 1f, 1f);

            AnimationTools.rotate("COOKIE_DOOR4_LEFT", 20, 1, 0.1f);
            AnimationTools.rotate("COOKIE_DOOR4_RIGHT", -20, 1, 0.1f);

        }
    }

    public void mainDoor(Boolean bool){
        var specialObjects = AnimationTools.specialObjects;
        var world = Bukkit.getWorld("world");
        var loc1 = AnimationTools.parseLocation(specialObjects.get("COOKIE_DOOR_POS1"), world);
        var loc2 = AnimationTools.parseLocation(specialObjects.get("COOKIE_DOOR_POS2"), world);
        if(bool){

            AnimationTools.fill(loc1, loc2, Material.AIR);

            AnimationTools.playSoundDistance(loc1, 50, "squid:sfx.cookie_door_open", 1f, 1f);

            AnimationTools.move("COOKIE_DOOR_LEFT", -28, 1, 'z', 0.1f);
            AnimationTools.move("COOKIE_DOOR_RIGHT", 28, 1, 'z', 0.1f);

        }else{

            AnimationTools.fill(loc1, loc2, Material.BARRIER);

            AnimationTools.playSoundDistance(loc1, 50, "squid:sfx.cookie_door_close", 1f, 1f);

            AnimationTools.move("COOKIE_DOOR_LEFT", 28, 1, 'z', 0.1f);
            AnimationTools.move("COOKIE_DOOR_RIGHT", -28, 1, 'z', 0.1f);

        }
    }
}
