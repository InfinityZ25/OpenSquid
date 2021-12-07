package us.jcedeno.guns;

import org.bukkit.Bukkit;

import lombok.Getter;
import me.aleiv.core.paper.Core;
import us.jcedeno.guns.listeners.GunListener;

/**
 * A class to easily manage all the gun system for the squidgame.
 * 
 * @author jcedeno
 */
public class GunManager {
    private @Getter final Core instance;
    // TODO: Make this work with multiple "guns"

    public GunManager(Core instance) {
        this.instance = instance;
        // Register the listener
        Bukkit.getPluginManager().registerEvents(new GunListener(), instance);
    }

}
