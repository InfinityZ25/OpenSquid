package us.jcedeno.cookie.objects;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.map.MapView;
import org.bukkit.map.MapView.Scale;

import lombok.Getter;
import me.aleiv.core.paper.map.packet.WrapperPlayServerMap;

/**
 * An object which represents an abstract MapView and Canvas that get's updated
 * asyncrhonously. Each CookieMap should contain a MapView, a MapId, and other
 * metadata about the map and the owner of the map.
 * 
 * @author jcedeno
 */
public class CookieMap {
    /** Constant for admins to access any map. */
    public static String EDIT_MAP_PERMISSION = "cookie.admin.edit";
    /** MapView used by bukkit to handle all the logic of the bukkit map. */
    private @Getter MapView mapView;
    /** A Map Id obtained from MapView. */
    private final @Getter Integer mapId;
    /** Renderer used to draw the pixels of the images that we use. */
    private @Getter CustomRender mapRenderer;
    /** The type of the current cookie. */
    private @Getter CookieEnum cookieType;
    /** Owner, the only owner allowed to edit the map. */
    private @Getter UUID owner;
    /** Whether the owner is currently allowed to edit or not. */
    private @Getter Boolean lockedForEditing;
    /**
     * A thread-safe 2D byte array to represent the color of all pixels in this map.
     */
    private volatile @Getter Byte[][] asyncPixelCanvas;

    /**
     * Constructor for the CookieMap object.
     * 
     * @param mapView The MapView object that will be used to render the map.
     */
    public CookieMap(World world, CookieEnum cookieType) {
        /** Create the bukkit map and initialize everything */
        this.mapView = Bukkit.createMap(world);
        this.mapId = mapView.getId();
        this.cookieType = cookieType;
        /** Initialize the canvas with the 128x128 pixel size. */
        this.asyncPixelCanvas = new Byte[128][128];

        /** Intialize map with all the important stuff. */
        this.mapView.getRenderers().forEach(mapView::removeRenderer);
        /** Add a custom renderer from the asset location. */
        this.mapView.addRenderer(new CustomRender(cookieType));
        /** boiler-plate */
        this.mapView.setTrackingPosition(false);
        this.mapView.setUnlimitedTracking(false);
        this.mapView.setScale(Scale.CLOSEST);
    }

    /**
     * @return A new ItemStack with this map as the item.
     */
    public ItemStack getMapAsItem() {
        /** Create map item and clone meta */
        final var item = new ItemStack(Material.FILLED_MAP);
        final var meta = item.getItemMeta();
        /** This if statement should always be true, it's more like a secure cast. */
        if (meta instanceof MapMeta mapMeta)
            mapMeta.setMapView(mapView);
        /** Set the new meta */
        item.setItemMeta(meta);

        return item;
    }

    private static Material CASE = Material.FERMENTED_SPIDER_EYE;

    /**
     * A function that returns a case
     * 
     * @return
     */
    public ItemStack getCookieCase() {
        var item = new ItemStack(CASE);
        return item;
    }

    /**
     * 
     * Functional that paints a pixel and then returns a packet with that update
     * contained.
     * 
     * @param x     The x coordinate of the pixel to be painted.
     * @param y     The y coordinate of the pixel to be painted. Also called Z by
     *              spigot.
     * @param color A byte color, look into mojang colors for this value.
     * @return A new packet or null if color already same.
     */
    public WrapperPlayServerMap paintPixel(int x, int y, byte color) {
        /** If coolor same, return null */
        if (asyncPixelCanvas[x][y] != null && asyncPixelCanvas[x][y] == color)
            return null;
        /** Paint pixel in storage matrix. */
        asyncPixelCanvas[x][y] = color;
        /** Create and return packet */
        var packet = new WrapperPlayServerMap();
        /** Set map id */
        packet.setItemDamage(mapId);
        /** Set scale */
        packet.setScale((byte) 4);
        /** Set rows and colms */
        packet.setColumns(1);
        packet.setRows(1);
        /** Set tracking position */
        packet.setTrackingPosition(false);
        packet.setX(x);
        packet.setZ(y);
        /** Set map data to array of 1 pixel of the given color */
        packet.setData(new byte[] { color });

        return packet;

    }

}
