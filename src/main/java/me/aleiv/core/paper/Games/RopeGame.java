package me.aleiv.core.paper.Games;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import lombok.Data;
import me.aleiv.core.paper.AnimationTools;
import me.aleiv.core.paper.Core;
import me.aleiv.core.paper.events.LeftWinsEvent;
import me.aleiv.core.paper.events.RightWinsEvent;
import me.aleiv.core.paper.utilities.NegativeSpaces;

@Data
public class RopeGame {
    Core instance;

    BossBar ropeBar;
    BossBar flagBar;
    BossBar yellowBar;

    Integer points = 0;

    Integer multiplier = 1;

    String bar = Character.toString('\u0250');
    String rope = Character.toString('\u0251');
    String flag = Character.toString('\u0252');

    Boolean ropeBossbar = false;
    Boolean inGame = false;
    Boolean boolMode = false;
    Boolean boolModeBool = false;

    List<ArmorStand> rightRope = new ArrayList<>();
    List<ArmorStand> leftRope = new ArrayList<>();


    public RopeGame(Core instance) {
        this.instance = instance;

        this.ropeBar = Bukkit.createBossBar(new NamespacedKey(instance, "ROPE"), "", BarColor.WHITE, BarStyle.SOLID);
        this.flagBar = Bukkit.createBossBar(new NamespacedKey(instance, "FLAG"), "", BarColor.WHITE, BarStyle.SOLID);
        this.yellowBar = Bukkit.createBossBar(new NamespacedKey(instance, "YELLOW"), "", BarColor.WHITE,
                BarStyle.SOLID);
        ropeBar.setTitle(rope);
        updateBossBar();
        yellowBar.setTitle(bar);

        enableRope(ropeBossbar);
    }

    public void updateBossBar() {
        flagBar.setTitle(NegativeSpaces.get(points) + flag);
    }

    public void addBossBars(Player player) {
        ropeBar.addPlayer(player);
        flagBar.addPlayer(player);
        yellowBar.addPlayer(player);
    }

    public void enableRope(Boolean bool) {
        ropeBossbar = bool;
        ropeBar.setVisible(bool);
        flagBar.setVisible(bool);
        yellowBar.setVisible(bool);
        updateBossBar();

    }

    public void initRope() {
        var specialObjects = AnimationTools.specialObjects;
        var world = Bukkit.getWorld("world");
        var r1 = AnimationTools.parseLocation(specialObjects.get("RIGHT_SIDE_POS1"), world);
        var r2 = AnimationTools.parseLocation(specialObjects.get("RIGHT_SIDE_POS2"), world);

        var l1 = AnimationTools.parseLocation(specialObjects.get("LEFT_SIDE_POS1"), world);
        var l2 = AnimationTools.parseLocation(specialObjects.get("LEFT_SIDE_POS2"), world);

        var entitys = world.getEntities().stream().filter(entity -> entity instanceof ArmorStand).map(entity -> (ArmorStand) entity).toList();

        rightRope.clear();
        leftRope.clear();

        entitys.stream().filter(entity -> AnimationTools.isInCube(r1, r2, entity.getLocation())).toList().forEach(stand ->{
            rightRope.add(stand);
        });

        entitys.stream().filter(entity -> AnimationTools.isInCube(l1, l2, entity.getLocation())).toList().forEach(stand ->{
            leftRope.add(stand);
        });

    }

    public void addPoints(Integer points) {
        if (this.points == 246 || this.points == -246)
            return;

        var p = Math.abs(points * multiplier);
        var v = points < 0 ? -p : p;

        if (this.points + v >= 246) {
            this.points = 246;
            Bukkit.getPluginManager().callEvent(new RightWinsEvent(!Bukkit.isPrimaryThread()));

        } else if (this.points + v <= -246) {
            this.points = -246;
            Bukkit.getPluginManager().callEvent(new LeftWinsEvent(!Bukkit.isPrimaryThread()));

        } else {
            this.points += v;

        }

        updateBossBar();
    }

    public void rightElevator(Boolean bool) {

        var specialObjects = AnimationTools.specialObjects;
        var world = Bukkit.getWorld("world");

        var loc1 = AnimationTools.parseLocation(specialObjects.get("ROPE_ELEVATOR_RIGHT_POS1"), world);
        var loc2 = AnimationTools.parseLocation(specialObjects.get("ROPE_ELEVATOR_RIGHT_POS2"), world);

        var players = AnimationTools.getPlayersInsideCube(loc1, loc2);

        if (!players.isEmpty()) {

            var pos1 = AnimationTools.parseLocation(specialObjects.get("ROPE_ELEVATOR_RIGHT_FLOOR_POS1"), world);
            var pos2 = AnimationTools.parseLocation(specialObjects.get("ROPE_ELEVATOR_RIGHT_FLOOR_POS2"), world);

            List<Entity> entitys = new ArrayList<>();
            entitys.add(AnimationTools.getEntity("ROPE_FRONT_RIGHT"));
            entitys.add(AnimationTools.getEntity("ROPE_BACK_RIGHT"));

            players.forEach(player -> {
                player.addPotionEffect(
                        new PotionEffect(PotionEffectType.LEVITATION, 20 * 15, 255, false, false, false));
                entitys.add(player);
            });

            var v = (28f / 300f);

            if (bool) {
                players.forEach(player -> {
                    player.playSound(player.getLocation(), "squid:sfx.old_elevator", 1, 1);
                });

                var tk = AnimationTools.moveEntitys(entitys, 300, 1, 'y', v);

                tk.thenAccept(action -> {
                    Bukkit.getScheduler().runTask(instance, task -> {
                        AnimationTools.fill(pos1, pos2, Material.BARRIER);
                        entitys.forEach(entity -> {
                            var l = entity.getLocation();
                            l.setY(62);
                            entity.teleport(l);
                        });
                    });
                });

            } else {
                players.forEach(player -> {
                    player.playSound(player.getLocation(), "squid:sfx.old_elevator", 1, 1);
                });

                var tk = AnimationTools.moveEntitys(entitys, 300, 1, 'y', -v);

                tk.thenAccept(action -> {
                    Bukkit.getScheduler().runTask(instance, task -> {
                        entitys.forEach(entity -> {
                            var l = entity.getLocation();
                            l.setY(34);
                            entity.teleport(l);
                        });
                    });
                });

                AnimationTools.fill(pos1, pos2, Material.AIR);
            }

        }

    }

    public void leftElevator(Boolean bool) {

        var specialObjects = AnimationTools.specialObjects;
        var world = Bukkit.getWorld("world");

        var loc1 = AnimationTools.parseLocation(specialObjects.get("ROPE_ELEVATOR_LEFT_POS1"), world);
        var loc2 = AnimationTools.parseLocation(specialObjects.get("ROPE_ELEVATOR_LEFT_POS2"), world);

        var players = AnimationTools.getPlayersInsideCube(loc1, loc2);

        if (!players.isEmpty()) {

            var pos1 = AnimationTools.parseLocation(specialObjects.get("ROPE_ELEVATOR_LEFT_FLOOR_POS1"), world);
            var pos2 = AnimationTools.parseLocation(specialObjects.get("ROPE_ELEVATOR_LEFT_FLOOR_POS2"), world);

            List<Entity> entitys = new ArrayList<>();
            entitys.add(AnimationTools.getEntity("ROPE_FRONT_LEFT"));
            entitys.add(AnimationTools.getEntity("ROPE_BACK_LEFT"));

            players.forEach(player -> {
                player.addPotionEffect(
                        new PotionEffect(PotionEffectType.LEVITATION, 20 * 15, 255, false, false, false));
                entitys.add(player);
            });

            var v = (28f / 300f);

            if (bool) {
                players.forEach(player -> {
                    player.playSound(player.getLocation(), "squid:sfx.old_elevator", 11, 1);
                });

                var tk = AnimationTools.moveEntitys(entitys, 300, 1, 'y', v);

                tk.thenAccept(action -> {
                    Bukkit.getScheduler().runTask(instance, task -> {
                        AnimationTools.fill(pos1, pos2, Material.BARRIER);
                        entitys.forEach(entity -> {
                            var l = entity.getLocation();
                            l.setY(62);
                            entity.teleport(l);
                        });
                    });
                });

            } else {
                players.forEach(player -> {
                    player.playSound(player.getLocation(), "squid:sfx.old_elevator", 11, 1);
                });

                var tk = AnimationTools.moveEntitys(entitys, 300, 1, 'y', -v);

                tk.thenAccept(action -> {
                    Bukkit.getScheduler().runTask(instance, task -> {
                        entitys.forEach(entity -> {
                            var l = entity.getLocation();
                            l.setY(34);
                            entity.teleport(l);
                        });
                    });
                });

                AnimationTools.fill(pos1, pos2, Material.AIR);
            }

        }

    }

    public void ropeGate(Boolean bool) {
        var world = Bukkit.getWorld("world");
        var specialObjects = AnimationTools.specialObjects;
        var loc1 = AnimationTools.parseLocation(specialObjects.get("ROPE_DOOR_POS1"), world);
        var loc2 = AnimationTools.parseLocation(specialObjects.get("ROPE_DOOR_POS2"), world);

        var ropeDoor = List.of("ROPE_DOOR1", "ROPE_DOOR2", "ROPE_DOOR3");
        if (bool) {

            AnimationTools.fill(loc1, loc2, Material.AIR);

            AnimationTools.playSoundDistance(loc1, 30, "squid:sfx.metal_door_open", 1f, 1f);

            AnimationTools.move(ropeDoor, 42, 1, 'y', 0.1f);

        } else {

            AnimationTools.playSoundDistance(loc1, 30, "squid:sfx.metal_door_close", 1f, 1f);

            var task = AnimationTools.move(ropeDoor, -42, 1, 'y', 0.1f);

            task.thenAccept(action -> {
                Bukkit.getScheduler().runTask(instance, tk -> {
                    AnimationTools.fill(loc1, loc2, Material.PRISMARINE_WALL);
                });
            });

        }
    }

    public CompletableFuture<Boolean> moveGuillotine(Boolean bool) {
        var loc = new Location(Bukkit.getWorld("world"), 241, 61, -301);
        if (bool) {

            AnimationTools.playSoundDistance(loc, 200, "squid:sfx.rope_guillotine", 1f, 1f);
            return AnimationTools.move("ROPE_GUILLOTINE", -12, 1, 'y', 1f);

        } else {

            return AnimationTools.move("ROPE_GUILLOTINE", 12, 1, 'y', 1f);
        }
    }

}
