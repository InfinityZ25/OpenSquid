package me.aleiv.core.paper;

import java.time.Duration;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import co.aikar.commands.PaperCommandManager;
import io.github.znetworkw.znpcservers.NPCLibrary;
import kr.entree.spigradle.annotations.SpigotPlugin;
import lombok.Getter;
import me.aleiv.core.paper.commands.ChairCMD;
import me.aleiv.core.paper.commands.DollCMD;
import me.aleiv.core.paper.commands.ElevatorsCMD;
import me.aleiv.core.paper.commands.GlassCMD;
import me.aleiv.core.paper.commands.HideSeekCMD;
import me.aleiv.core.paper.commands.MainCMD;
import me.aleiv.core.paper.commands.PhoneCMD;
import me.aleiv.core.paper.commands.PotatoCMD;
import me.aleiv.core.paper.commands.RopeCMD;
import me.aleiv.core.paper.commands.SpecialCMD;
import me.aleiv.core.paper.commands.SquidCMD;
import me.aleiv.core.paper.commands.TestCMD;
import me.aleiv.core.paper.commands.UtilsCMD;
import me.aleiv.core.paper.listeners.CanceledListener;
import me.aleiv.core.paper.listeners.ChairListener;
import me.aleiv.core.paper.listeners.GlassListener;
import me.aleiv.core.paper.listeners.GlobalListener;
import me.aleiv.core.paper.listeners.HideListener;
import me.aleiv.core.paper.listeners.ItemListener;
import me.aleiv.core.paper.listeners.MechanicsListener;
import me.aleiv.core.paper.listeners.PhoneListener;
import me.aleiv.core.paper.listeners.PotatoListener;
import me.aleiv.core.paper.listeners.RopeListener;
import me.aleiv.core.paper.utilities.JsonConfig;
import me.aleiv.core.paper.utilities.NegativeSpaces;
import me.aleiv.core.paper.utilities.TCT.BukkitTCT;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.title.Title;
import net.kyori.adventure.title.Title.Times;
import uk.lewdev.entitylib.FakeEntityPlugin;
import us.jcedeno.cookie.CookieManager;
import us.jcedeno.detection.CollisionManager;
import us.jcedeno.libs.rapidinv.RapidInvManager;
import us.jcedeno.skins.SkinCMD;
import us.jcedeno.vectors.VectorsManager;

@SpigotPlugin
public class Core extends JavaPlugin {

    private static @Getter Core instance;
    private @Getter Game game;
    private @Getter PaperCommandManager commandManager;
    private @Getter static MiniMessage miniMessage = MiniMessage.get();
    private @Getter CollisionManager collisionManager;
    private @Getter VectorsManager vectorManager;
    // private @Getter MapSystemManager mapSystemManager;
    private @Getter SkinCMD skinCMD;
    private @Getter NPCLibrary npcLibrary;
    private @Getter CookieManager cookieManager;
    private static Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private @Getter FakeEntityPlugin fakeEntityPlugin;

    @Override
    public void onEnable() {

        instance = this;
        this.npcLibrary = new NPCLibrary();
        this.npcLibrary.register(this);

        RapidInvManager.register(this);
        BukkitTCT.registerPlugin(this);
        NegativeSpaces.registerCodes();

        try {
            var jsonConfig = new JsonConfig("special.json");
            var list = jsonConfig.getJsonObject();
            var iter = list.entrySet().iterator();
            var map = AnimationTools.specialObjects;

            while (iter.hasNext()) {
                var entry = iter.next();
                var name = entry.getKey();
                var value = entry.getValue();
                map.put(name, value.getAsString());

            }

        } catch (Exception e) {

            e.printStackTrace();
        }

        game = new Game(this);
        game.runTaskTimerAsynchronously(this, 0L, 20L);

        // LISTENERS

        registerListener(new GlobalListener(this));
        registerListener(new RopeListener(this));
        registerListener(new CanceledListener(this));
        registerListener(new HideListener(this));
        registerListener(new ChairListener(this));
        registerListener(new MechanicsListener(this));
        registerListener(new PotatoListener(this));
        registerListener(new GlassListener(this));
        registerListener(new ItemListener(this));
        registerListener(new PhoneListener(this));

        // COMMANDS

        commandManager = new PaperCommandManager(this);
        commandManager.registerCommand(new DollCMD(this));
        commandManager.registerCommand(new MainCMD(this));
        commandManager.registerCommand(new SquidCMD(this));
        commandManager.registerCommand(new RopeCMD(this));
        commandManager.registerCommand(new HideSeekCMD(this));
        commandManager.registerCommand(new ElevatorsCMD(this));
        commandManager.registerCommand(new ChairCMD(this));
        commandManager.registerCommand(new PotatoCMD(this));
        commandManager.registerCommand(new GlassCMD(this));
        commandManager.registerCommand(new PhoneCMD(this));

        commandManager.registerCommand(new UtilsCMD(this));
        commandManager.registerCommand(new SpecialCMD(this));
        commandManager.registerCommand(new TestCMD(this));

        // Register skin command
        this.skinCMD = new SkinCMD(this);

        // Start collision manager
        this.collisionManager = new CollisionManager(this);
        // Start vectors manager
        this.vectorManager = new VectorsManager(this);
        // Start map system manager
        // this.mapSystemManager = new MapSystemManager(this);
        // Start effect manager
        // Start cookie manager
        this.cookieManager = new CookieManager(this);

        this.fakeEntityPlugin = new FakeEntityPlugin();

        this.fakeEntityPlugin.onEnable();
    }

    @Override
    public void onDisable() {

        this.npcLibrary.unregister();
        try {
            this.fakeEntityPlugin.onDisable();
        } catch (Exception e) {
        }
    }

    public void refreshJson() {
        var list = AnimationTools.specialObjects;

        try {
            var jsonConfig = new JsonConfig("special.json");
            var json = gson.toJson(list);
            var obj = gson.fromJson(json, JsonObject.class);
            jsonConfig.setJsonObject(obj);
            jsonConfig.save();

        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    public void unregisterListener(Listener listener) {
        HandlerList.unregisterAll(listener);
    }

    public void registerListener(Listener listener) {
        Bukkit.getPluginManager().registerEvents(listener, instance);
    }

    public void adminMessage(String text) {
        Bukkit.getOnlinePlayers().forEach(player -> {
            if (player.hasPermission("admin.perm"))
                player.sendMessage(text);
        });
    }

    public Component componentToString(String str) {
        return miniMessage.parse(str);
    }

    public void broadcastMessage(String text) {
        Bukkit.broadcast(miniMessage.parse(text));
    }

    public void sendActionBar(Player player, String text) {
        player.sendActionBar(miniMessage.parse(text));
    }

    public void showTitle(Player player, String title, String subtitle, int fadeIn, int stay, int fadeOut) {
        player.showTitle(Title.title(miniMessage.parse(title), miniMessage.parse(subtitle), Times
                .of(Duration.ofMillis(50 * fadeIn), Duration.ofMillis(50 * stay), Duration.ofMillis(50 * fadeIn))));
    }

    public void sendHeader(Player player, String text) {
        player.sendPlayerListHeader(miniMessage.parse(text));
    }

    public void sendFooter(Player player, String text) {
        player.sendPlayerListFooter(miniMessage.parse(text));
    }

}