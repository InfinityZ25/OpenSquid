package us.jcedeno.cookie;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;

import lombok.Getter;
import me.aleiv.core.paper.Core;
import us.jcedeno.cookie.commands.CookieCMD;
import us.jcedeno.cookie.listener.CookieCaseListener;
import us.jcedeno.cookie.listener.CookieClickedListener;
import us.jcedeno.cookie.objects.CookieMap;
import us.jcedeno.cookie.packet.WrapperPlayServerSpawnEntity;

/**
 * The CookieManager class is a singleton that manages the cookie map views for
 * the SquidGame Cookie Game. Yes yes.
 * 
 * @author jcedeno
 */
public class CookieManager {

    private @Getter static Core instance;
    private @Getter CookieCMD cookieCMD;
    private @Getter ConcurrentHashMap<UUID, CookieMap> cookieMaps;
    private @Getter volatile HashMap<Block, ItemFrame> frameMap = new HashMap<Block, ItemFrame>();

    public static boolean EDIT = true;

    public CookieManager(Core plugin) {
        instance = plugin;
        this.cookieCMD = new CookieCMD(plugin, this);
        this.cookieMaps = new ConcurrentHashMap<>();

        Bukkit.getPluginManager().registerEvents(new CookieClickedListener(this), plugin);

        Bukkit.getPluginManager().registerEvents(new CookieCaseListener(this), plugin);
    }

    /**
     * Method that spawns an item frame and attempts to place a image map on it
     * using packets.
     * 
     * @param player The player that will see the item frame.
     * @param loc    The location at which the item frame will be placed.
     */
    public int packetCookieMap(Player player, Location loc, Integer rotation) {
        var spawnPacket = new WrapperPlayServerSpawnEntity();

        var eid = (int) (Math.random() * Integer.MAX_VALUE);
        spawnPacket.setEntityID(eid);
        spawnPacket.setType(EntityType.ITEM_FRAME);
        spawnPacket.setX(loc.getX());
        spawnPacket.setY(loc.getY() + 1);
        spawnPacket.setZ(loc.getZ());

        // Object data here is rotation. 1 is rotation up. see protocol
        // https://wiki.vg/Object_Data

        spawnPacket.setObjectData(rotation);
        spawnPacket.setPitch(-90.0F);
        loc.getBlock().setType(Material.GOLD_BLOCK);

        spawnPacket.sendPacket(player);
        return eid;
    }

}
