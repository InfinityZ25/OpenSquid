package us.jcedeno.cookie.listener;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.ItemFrame;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.MapMeta;

import me.aleiv.core.paper.map.packet.WrapperPlayServerMap;
import us.jcedeno.cookie.CookieManager;
import us.jcedeno.cookie.events.PlayerClickedCookieEvent;

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
    @EventHandler
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
    @EventHandler
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

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerPaintedCookie(PlayerClickedCookieEvent e) {
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
            int x, z;
            WrapperPlayServerMap packet = null;

            switch (frame.getRotation()) {

                case NONE:
                case FLIPPED: {
                    x = (int) (relativeX * 128);
                    // Add 1 - z to adjust for rotation.
                    z = (int) ((relativeZ) * 128);
                    packet = cookieMap.paintPixel(Math.min(127, x), Math.min(127, z), (byte) 24);
                    break;
                }
                case CLOCKWISE_45:
                case FLIPPED_45:

                    z = (int) Math.ceil((position.getX()) * 128);
                    x = (int) Math.ceil((position.getZ()) * 128);
                    packet = cookieMap.paintPixel(Math.min(x, 127), Math.min(128 - z, 127), (byte) 24);

                    break;
                case COUNTER_CLOCKWISE:
                case CLOCKWISE:

                    x = (int) Math.ceil((position.getX()) * 128);
                    z = (int) Math.ceil((position.getZ()) * 128);
                    packet = cookieMap.paintPixel(Math.min(128 - x, 127), Math.min(128 - z, 127), (byte) 24);

                    break;
                case COUNTER_CLOCKWISE_45:
                case CLOCKWISE_135:

                    z = (int) Math.ceil((position.getX()) * 128);
                    x = (int) Math.ceil((position.getZ()) * 128);
                    packet = cookieMap.paintPixel(Math.min(128 - x, 127), Math.min(127, z), (byte) 24);

                    break;
            }
            if (packet != null) {
                packet.broadcastPacket();
            }
        }
    }

}
