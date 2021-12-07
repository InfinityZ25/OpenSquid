package me.aleiv.core.paper.Games;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import me.aleiv.core.paper.AnimationTools;
import me.aleiv.core.paper.Core;
import me.aleiv.core.paper.Game.GameStage;
import me.aleiv.core.paper.Game.HideMode;
import me.aleiv.core.paper.utilities.Frames;
import me.aleiv.core.paper.utilities.TCT.BukkitTCT;

public class GlobalGame {
    Core instance;
    
    public GlobalGame(Core instance){
        this.instance = instance;

    }

    public CompletableFuture<Boolean> applyGas(List<Player> players){ 
        players.forEach(player -> {
            var loc = player.getLocation();
            player.playSound(loc, "squid:sfx.gas", 11, 1);
        });
        return playAnimation(players, 3402, 3601, 0, 0);
    }

    public CompletableFuture<Boolean> sendCountDown(List<Player> players){
        players.forEach(player -> {
            var loc = player.getLocation();
            player.playSound(loc, "squid:sfx.countdown", 11, 1);
        });
        return playAnimation(players, 3602, 4392, 0, 0);
    }

    public void makeAllSleep(List<Player> players){
        var beds = AnimationTools.findLocations("BED");
        players.forEach(player -> {
            player.setGameMode(GameMode.ADVENTURE);
        });
        AnimationTools.forceSleep(players, beds);
    }

    public void sleep(Player player, Location loc){
        AnimationTools.forceSleep(player, loc);
    }

    public void playSquidGameStart(){ 
        var game = instance.getGame();
        var mainRoom = game.getMainRoom();
        var task = new BukkitTCT();

        for (int i = 0; i < 160; i++) {
            task.addWithDelay(new BukkitRunnable() {
                @Override
                public void run() {
                    Bukkit.getOnlinePlayers().forEach(player -> {
                        instance.showTitle(player, Character.toString('\u3400') + "", "", 0, 20, 0);
                    });
                }
            }, 50);
        }

        var tk = task.execute();
        tk.thenAccept(action ->{
            Bukkit.getScheduler().runTask(instance, tks ->{
                game.setHideMode(HideMode.INGAME);
                game.setGameStage(GameStage.INGAME);
                game.refreshHide();

                Bukkit.getWorlds().forEach(world ->{
                    world.setTime(20000);
                });

                var allPlayers = Bukkit.getOnlinePlayers();
                
                allPlayers.forEach(player -> {
                    player.teleport(new Location(Bukkit.getWorld("world"), 232, 6, -80));
                    instance.sendActionBar(player, Character.toString('\u3400') + "");
                    var inv = player.getInventory();
                    inv.clear();
                    
                });

                var players = allPlayers.stream().filter(player -> game.isPlayer(player)).map(player -> (Player) player).toList();

                players.forEach(player ->{
                    //TODO: PLAYER UNIFORM
                });

                try {
                    makeAllSleep(players);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                
                mainRoom.lights(true);
            });
        });

    }

    public CompletableFuture<Boolean> playAnimation(List<Player> players, Integer from, Integer until, int fadeIn, int fadeOut){
        
        var task = new BukkitTCT();

        var animation = Frames.getFramesCharsIntegersAll(from, until);

        animation.forEach(frame -> {
            task.addWithDelay(new BukkitRunnable() {
                @Override
                public void run() {
                    players.forEach(player -> {
                        instance.showTitle(player, frame + "", "", fadeIn, 20, fadeOut);
                    });
                }
            }, 50);
        });

        task.addWithDelay(new BukkitRunnable() {
            @Override
            public void run() {
                players.forEach(player -> {
                    instance.sendActionBar(player, Character.toString('\u3400') + "");
                });
            }
        }, 50);

        return task.execute();
    }
}
