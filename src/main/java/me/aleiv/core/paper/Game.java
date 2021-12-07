package me.aleiv.core.paper;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import lombok.Data;
import lombok.EqualsAndHashCode;
import me.aleiv.core.paper.Games.ChairGame;
import me.aleiv.core.paper.Games.CookieGame;
import me.aleiv.core.paper.Games.DollGame;
import me.aleiv.core.paper.Games.Elevators;
import me.aleiv.core.paper.Games.GlassGame;
import me.aleiv.core.paper.Games.GlobalGame;
import me.aleiv.core.paper.Games.HideSeekGame;
import me.aleiv.core.paper.Games.MainRoom;
import me.aleiv.core.paper.Games.PhoneGame;
import me.aleiv.core.paper.Games.PotatoGame;
import me.aleiv.core.paper.Games.RopeGame;
import me.aleiv.core.paper.events.GameTickEvent;
import me.aleiv.core.paper.objects.Timer;

@Data
@EqualsAndHashCode(callSuper = false)
public class Game extends BukkitRunnable {
    Core instance;

    long gameTime = 0;
    long startTime = 0;

    Timer timer;

    //GAMES
    GlobalGame globalGame;

    MainRoom mainRoom;
    DollGame dollGame;
    RopeGame ropeGame;
    HideSeekGame hideSeekGame;
    CookieGame cookieGame;
    Elevators elevators;
    ChairGame chairGame;
    PotatoGame potatoGame;
    GlassGame glassGame;
    PhoneGame phoneGame;

    HashMap<String, Role> roles = new HashMap<>();

    Boolean lights = true;
    PvPType pvp = PvPType.ONLY_GUARDS;
    TimerType timerType = TimerType.RED_GREEN;
    HideMode hideMode = HideMode.INGAME;
    GameStage gameStage = GameStage.INGAME;

    String totalPlayers = "000";
    String totalPrize = "000000";

    Location city;
    Location whiteLobby;

    public Game(Core instance) {
        this.instance = instance;
        this.startTime = System.currentTimeMillis();

        this.timer = new Timer(instance, (int) gameTime);

        this.globalGame = new GlobalGame(instance);
        
        this.mainRoom = new MainRoom(instance);
        this.ropeGame = new RopeGame(instance);
        this.dollGame = new DollGame(instance);
        this.hideSeekGame = new HideSeekGame(instance);
        this.cookieGame = new CookieGame(instance);
        this.elevators = new Elevators(instance);
        this.chairGame = new ChairGame(instance);
        this.potatoGame = new PotatoGame(instance);
        this.glassGame = new GlassGame(instance);
        this.phoneGame = new PhoneGame(instance);
    }

    public enum PvPType{
        ONLY_GUARDS, ALL, ONLY_PVP
    }

    public enum Role {
        GUARD, PLAYER, VIP
    }

    public enum TimerType{
        RED_GREEN, GLASS, COOKIE, HIDE_SEEK, POTATO
    }

    public enum HideMode{
        INGAME, LOBBY, TEST
    }

    public enum GameStage{
        INGAME, LOBBY, PAUSE
    }

    public enum DeathReason{
        EXPLOSION, PROJECTILE, NORMAL
    }

    public boolean isGuard(Player player){
        return roles.get(player.getUniqueId().toString()) == Role.GUARD;
    }

    public boolean isPlayer(Player player){
        return roles.get(player.getUniqueId().toString()) == Role.PLAYER;
    }

    public void refreshHide(){
        var players = Bukkit.getOnlinePlayers().stream().map(p -> (Player) p).toList();

        switch (hideMode) {
            case INGAME ->{
                players.forEach(player ->{
                    var gamemode = player.getGameMode();

                    if(gamemode == GameMode.CREATIVE){
                        players.forEach(p ->{
                            player.showPlayer(instance, p);
                        });
                    }else{
                        players.forEach(p ->{
                            var gm = p.getGameMode();
                            if(gm == GameMode.CREATIVE || gm == GameMode.SPECTATOR){
                                player.hidePlayer(instance, p);
                            }else{
                                player.showPlayer(instance, p);
                            }
                        });
                    }

                });
            }
            case LOBBY ->{
                players.forEach(player ->{
                    var gamemode = player.getGameMode();

                    if(gamemode == GameMode.CREATIVE){
                        players.forEach(p ->{
                            player.showPlayer(instance, p);
                        });
                    }else{
                        players.forEach(p ->{
                            player.hidePlayer(instance, p);
                        });
                    }

                });
            }
            case TEST ->{
                players.forEach(player ->{
                    players.forEach(p ->{
                        player.showPlayer(instance, p);
                   });
                });
            }
        }
    }

    @Override
    public void run() {

        var new_time = (int) (Math.floor((System.currentTimeMillis() - startTime) / 1000.0));

        gameTime = new_time;

        Bukkit.getPluginManager().callEvent(new GameTickEvent(new_time, true));
    }
}