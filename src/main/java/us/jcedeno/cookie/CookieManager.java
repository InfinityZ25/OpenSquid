package us.jcedeno.cookie;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import com.comphenix.protocol.wrappers.WrappedWatchableObject;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.MapMeta;

import lombok.Getter;
import me.aleiv.core.paper.Core;
import us.jcedeno.cookie.commands.CookieCMD;
import us.jcedeno.cookie.listener.CookieCaseListener;
import us.jcedeno.cookie.listener.CookieClickedListener;
import us.jcedeno.cookie.objects.CookieMap;
import us.jcedeno.cookie.packet.WrapperPlayServerEntityMetadata;
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
    private static Object temporal = null;

    public CookieManager(Core plugin) {
        instance = plugin;
        this.cookieCMD = new CookieCMD(plugin, this);
        this.cookieMaps = new ConcurrentHashMap<>();

        Bukkit.getPluginManager().registerEvents(new CookieClickedListener(this), plugin);

        Bukkit.getPluginManager().registerEvents(new CookieCaseListener(this), plugin);
        var protocolManager = ProtocolLibrary.getProtocolManager();

        // Censor
        protocolManager.addPacketListener(new PacketAdapter(plugin,
                ListenerPriority.NORMAL,
                PacketType.Play.Server.ENTITY_METADATA) {
            @Override
            public void onPacketReceiving(PacketEvent event) {
            }

            @Override
            public void onPacketSending(PacketEvent event) {
                if (event.getPacketType() == PacketType.Play.Server.ENTITY_METADATA) {
                    PacketContainer packet = event.getPacket();
                    var wrappedPacket = new WrapperPlayServerEntityMetadata(packet);
                    var actualEntity = wrappedPacket.getEntity(event.getPlayer().getWorld());
                    if (actualEntity != null && actualEntity.getType() == EntityType.ITEM_FRAME)
                        wrappedPacket.getMetadata().forEach(all -> {
                            // System.out.println(all.toString());
                            if (all.getValue().getClass().getName()
                                    .equalsIgnoreCase("org.bukkit.craftbukkit.v1_16_R3.inventory.CraftItemStack")) {
                                System.out.println(
                                        "Setting to: " + all.getValue() + "," + all.getValue().getClass().getName());
                                temporal = all.getValue();
                            }

                        });
                }

            }
        });

    }

    /**
     * Method that spawns an item frame and attempts to place a image map on it
     * using packets.
     * 
     * @param player The player that will see the item frame.
     * @param loc    The location at which the item frame will be placed.
     */
    public int packetCookieMap(Player player, Location loc, Integer rotation, BufferedImage image) {

        System.out.println(temporal);
        var spawnPacket = new WrapperPlayServerSpawnEntity();

        var eid = (int) (Math.random() * Integer.MAX_VALUE);
        spawnPacket.setEntityID(eid);
        spawnPacket.setType(EntityType.ITEM_FRAME);
        spawnPacket.setX(loc.getX());
        spawnPacket.setY(loc.getY() + 1);
        spawnPacket.setZ(loc.getZ());
        // MapPalette.imageToBytes(image);

        // Object data here is rotation. 1 is rotation up. see protocol
        // https://wiki.vg/Object_Data

        spawnPacket.setObjectData(rotation);
        spawnPacket.setPitch(-90.0F);

        spawnPacket.sendPacket(player);

        // Add to item frame the map item

        var hookMapPacket = new WrapperPlayServerEntityMetadata();
        hookMapPacket.setEntityID(eid);

        var itemStack = new ItemStack(Material.FILLED_MAP);
        var meta = itemStack.getItemMeta();
        if (meta instanceof MapMeta mapMeta)
            mapMeta.setMapId(420);

        itemStack.setItemMeta(meta);

        
        WrappedDataWatcher watcher = new WrappedDataWatcher();

        


        var watchable = new WrappedWatchableObject(8, temporal);
        var li = new ArrayList<WrappedWatchableObject>();
        li.add(watchable);

        hookMapPacket.setMetadata(li);
        hookMapPacket.sendPacket(player);
        System.out.println("Packet is: " + hookMapPacket.toString());

        return eid;
    }

}
