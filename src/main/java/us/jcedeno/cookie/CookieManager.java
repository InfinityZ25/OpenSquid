package us.jcedeno.cookie;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.ItemFrame;

import lombok.Getter;
import lombok.Setter;
import me.aleiv.core.paper.Core;
import us.jcedeno.cookie.commands.CookieCMD;
import us.jcedeno.cookie.listener.CookieCaseListener;
import us.jcedeno.cookie.listener.CookieClickedListener;
import us.jcedeno.cookie.objects.CookieMap;

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
    private @Getter @Setter static volatile Byte paintColor = (byte) 24;

    public static boolean EDIT = true;

    public CookieManager(Core plugin) {
        instance = plugin;
        this.cookieCMD = new CookieCMD(plugin, this);
        this.cookieMaps = new ConcurrentHashMap<>();

        Bukkit.getPluginManager().registerEvents(new CookieClickedListener(this), plugin);

        Bukkit.getPluginManager().registerEvents(new CookieCaseListener(this), plugin);

    }

}
