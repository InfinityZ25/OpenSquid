package us.jcedeno.cookie.listener;

import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemFrame;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import us.jcedeno.cookie.CookieManager;
import us.jcedeno.cookie.objects.CookieEnum;
import us.jcedeno.cookie.objects.CookieMap;

/**
 * 
 * Listener for intera
 * 
 * @author jcedeno
 */
public class CookieCaseListener implements Listener {
    private final CookieManager cookieManager;

    public CookieCaseListener(CookieManager cookieManager) {
        this.cookieManager = cookieManager;
    }

    private static Material NEEDLE = Material.CLAY_BALL;

    @EventHandler
    public void onPlayerOpenCookieBox(PlayerInteractEvent e) {
        /*
         * TODO: Check if player already has one cookie, if so delete the previous one
         * and replace it with the new one
         */
        if (e.getItem() == null || !CookieManager.EDIT)
            return;
        if (e.getAction() == Action.RIGHT_CLICK_BLOCK && e.getItem().getType() == Material.FERMENTED_SPIDER_EYE) {
            var meta = e.getItem().getItemMeta();

            if (!meta.hasCustomModelData())
                return;
            var cookieType = CookieEnum.values()[meta.getCustomModelData()];

            // Create new cookie
            var cookie = new CookieMap(e.getPlayer().getWorld(), cookieType);
            // Put on map.
            var oldCookie = cookieManager.getCookieMaps().put(e.getPlayer().getUniqueId(), cookie);
            // Remove old cookie
            if (oldCookie != null)
                oldCookie = null;

            // Now attempt to place cookie in ground.
            var block = e.getClickedBlock();
            var itemFrame = (ItemFrame) block.getWorld().spawnEntity(
                    block.getLocation().getBlock().getRelative(BlockFace.UP).getLocation(), EntityType.ITEM_FRAME);
            /** Select the type of cookie based on the custom model data. */

            itemFrame.setItem(cookie.getMapAsItem());
            itemFrame.setFacingDirection(BlockFace.UP);

            // Make it fixed.
            itemFrame.setFixed(true);

            var itemLocation = itemFrame.getLocation();

            // Add to 3d matrix for caching
            cookieManager.getFrameMap().put(itemLocation.getBlock(), itemFrame);

            // Give a needle to the player
            e.getPlayer().getInventory().setItem(e.getPlayer().getInventory().getHeldItemSlot(), new ItemStack(NEEDLE));

        }
    }

}
