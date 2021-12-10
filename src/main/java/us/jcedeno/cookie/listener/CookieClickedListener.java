package us.jcedeno.cookie.listener;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.SoundCategory;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.map.MapCanvas;

import me.aleiv.core.paper.map.packet.WrapperPlayServerMap;
import us.jcedeno.cookie.CookieManager;
import us.jcedeno.cookie.events.PlayerClickedCookieEvent;
import us.jcedeno.cookie.events.PlayerPaintedCookieEvent;

/**
 * Listener for interactions with item frames.
 * 
 * @author jcedeno
 */
public class CookieClickedListener implements Listener {
    private CookieManager cookieManager;

    public CookieClickedListener(CookieManager cookieManager) {
        this.cookieManager = cookieManager;
    }

    /**
     * Event called when a player click on an item frame which contains a full map.
     * Only the player who created the map (or a permission) should be able to
     * interact with it.
     */
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onInteractAtEntity(final PlayerInteractAtEntityEvent e) {
        if (!CookieManager.EDIT)
            return;

        final var entity = e.getRightClicked();

        if (entity != null && entity instanceof ItemFrame frame) {
            ItemStack itemInFrame = frame.getItem();
            if (itemInFrame != null && itemInFrame.getType() == Material.FILLED_MAP
                    && itemInFrame.getItemMeta() instanceof MapMeta map) {

                var entry = cookieManager.getFrameMap().get(entity.getLocation().getBlock());
                if (entry != null) {

                    var interactionPoint = entity.getLocation().add(e.getClickedPosition());

                    Bukkit.getPluginManager().callEvent(new PlayerClickedCookieEvent(interactionPoint,
                            e.getPlayer(), frame, !Bukkit.isPrimaryThread()));

                }
            }

        }
    }

    /**
     * Event called when a player clicks on a block which contains a map on an item
     * frame above of it.
     */
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerInteractAtItemFramesBlock(final PlayerInteractEvent e) {
        if (!CookieManager.EDIT || e.getAction() != Action.RIGHT_CLICK_BLOCK || e.getBlockFace() != BlockFace.UP)
            return;

        var block = e.getClickedBlock().getRelative(BlockFace.UP);

        var map = cookieManager.getFrameMap().get(block);
        if (map != null) {
            var interaction = e.getInteractionPoint();
            if (interaction != null) {
                // Normalize the vector and reduce it by one vector unit for accuracy.
                var vec = interaction.toVector().clone();
                var normie = e.getPlayer().getEyeLocation().toVector().subtract(vec).normalize();
                var l = interaction.toVector().clone().add(normie.multiply(0.05)).toLocation(block.getWorld());
                // Call the Event and handle everything else there.
                Bukkit.getPluginManager()
                        .callEvent(new PlayerClickedCookieEvent(l, e.getPlayer(), map, !Bukkit.isPrimaryThread()));
            }

        }

    }

    @EventHandler
    public void onPlayerClickedCookie(final PlayerClickedCookieEvent e) {
        // TODO Ensure that players can only edit a cookie if it belongs to them.
        if (CookieManager.EDIT) {
            var player = e.getPlayer();
            var frame = e.getItemFrame();
            var position = e.getInteractionPoint();

            var bX = frame.getLocation().getBlockX();
            var bZ = frame.getLocation().getBlockZ();

            var relativeX = position.getX() - bX;
            var relativeZ = position.getZ() - bZ;
            // Send relative to player
            // player.sendMessage("rotation: " + frame.getRotation() + ", " + relativeX + ",
            // " + relativeZ);
            var cookieMap = cookieManager.getCookieMaps().get(player.getUniqueId());
            // Null safety
            if (cookieMap == null)
                return;
            byte x = 0;
            byte z = 0;
            WrapperPlayServerMap packet = null;
            // Handle all the cases based on rotation; improve this later.
            switch (frame.getRotation()) {
                case NONE:
                case FLIPPED: {
                    x = (byte) (relativeX * 128);
                    // Add 1 - z to adjust for rotation.
                    z = (byte) ((relativeZ) * 128);
                    packet = cookieMap.paintPixel(Math.min(127, x), Math.min(127, z), CookieManager.getPaintColor());
                    break;
                }
                case CLOCKWISE_45:
                case FLIPPED_45:

                    z = (byte) Math.ceil((position.getX()) * 128);
                    x = (byte) Math.ceil((position.getZ()) * 128);
                    packet = cookieMap.paintPixel(Math.min(x, 127), Math.min(128 - z, 127),
                            CookieManager.getPaintColor());

                    break;
                case COUNTER_CLOCKWISE:
                case CLOCKWISE:

                    x = (byte) Math.ceil((position.getX()) * 128);
                    z = (byte) Math.ceil((position.getZ()) * 128);
                    packet = cookieMap.paintPixel(Math.min(128 - x, 127), Math.min(128 - z, 127),
                            CookieManager.getPaintColor());

                    break;
                case COUNTER_CLOCKWISE_45:
                case CLOCKWISE_135:

                    z = (byte) Math.ceil((position.getX()) * 128);
                    x = (byte) Math.ceil((position.getZ()) * 128);
                    packet = cookieMap.paintPixel(Math.min(128 - x, 127), Math.min(127, z),
                            CookieManager.getPaintColor());

                    break;
            }
            if (packet != null) {
                packet.broadcastPacket();
                // Call the Event
                var canvas = cookieMap.getMapRenderer().getCanvas();
                if (canvas != null)
                    Bukkit.getPluginManager()
                            .callEvent(PlayerPaintedCookieEvent.of(cookieMap, player, x, z,
                                    canvas.getPixel(x, z)));

            }
        }
    }

    @EventHandler
    public void onPlayerPaintedMap(final PlayerPaintedCookieEvent e) {
        /*
         * TODO Detection
         * Color que clickear = -93
         * Safe zone = -96
         * error = -94
         * ignore/outside = -95
         */
        var px = e.getPixel();
        /**
         * Pixel count color -93:
         * 
         * Creeper = 968 pixeles
         * Eye = 864 pixeles
         * Squid = 1040 pixeles
         * Rodolfo = 1176 pixeles
         */
        if (px.color() == -93) {
            // Correct clicked
            e.getCookieMap().increaseCorrectPixels();
            e.getPlayer().playSound(e.getPlayer().getLocation(), "squid:sfx.right", SoundCategory.AMBIENT, 0.25f,
                    0.0f);
        } else if (px.color() == -94) {
            // incorrect clicked
            e.getCookieMap().increaseIncorrectPixels();
            // Play error sound
            e.getPlayer().playSound(e.getPlayer().getLocation(), "squid:sfx.wrong", SoundCategory.AMBIENT, 1.0f, 0.0f);
        }
    }

    void countPixelColorsOfMap(MapCanvas canvas, Player player) {
        int a = 0;
        int b = 0;
        int c = 0;
        int d = 0;
        if (canvas != null) {
            // Double for loop from 0 to 127
            for (int x = 0; x < 128; x++) {
                for (int z = 0; z < 128; z++) {
                    // If the pixel is the same color as the one clicked
                    var pixel = canvas.getPixel(x, z);

                    switch (pixel) {
                        case -93:
                            a += (1);
                            break;
                        case -94:
                            b += (1);
                            break;
                        case -95:
                            c += (1);
                            break;
                        case -96:
                            d += (1);
                            break;
                    }

                }
            }
        }
        // Send the message to the player
        player.sendMessage("There are " + a + " pixels of color " + -93 + ".");
        player.sendMessage("There are " + b + " pixels of color " + -94 + ".");
        player.sendMessage("There are " + c + " pixels of color " + -95 + ".");
        player.sendMessage("There are " + d + " pixels of color " + -96 + ".");
    }

}
