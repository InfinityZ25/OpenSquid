package me.aleiv.core.paper.listeners;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;

import me.aleiv.core.paper.AnimationTools;
import me.aleiv.core.paper.Core;
import me.aleiv.core.paper.events.LeftWinsEvent;
import me.aleiv.core.paper.events.RightWinsEvent;
import me.aleiv.core.paper.utilities.TCT.BukkitTCT;
import net.md_5.bungee.api.ChatColor;

public class RopeListener implements Listener {

    Core instance;
    Random random = new Random();

    public RopeListener(Core instance) {
        this.instance = instance;
    }

    @EventHandler
    public void onJoin(PlayerInteractAtEntityEvent e) {
        var game = instance.getGame();
        var rope = game.getRopeGame();
        var entity = e.getRightClicked();
        if (entity instanceof ArmorStand stand && rope.getRopeBossbar() && rope.getInGame()) {
            var specialObjects = AnimationTools.specialObjects;
            var world = Bukkit.getWorld("world");

            var r1 = AnimationTools.parseLocation(specialObjects.get("RIGHT_SIDE_POS1"), world);
            var r2 = AnimationTools.parseLocation(specialObjects.get("RIGHT_SIDE_POS2"), world);

            var l1 = AnimationTools.parseLocation(specialObjects.get("LEFT_SIDE_POS1"), world);
            var l2 = AnimationTools.parseLocation(specialObjects.get("LEFT_SIDE_POS2"), world);

            var right = AnimationTools.getPlayersInsideCube(r1, r2);
            var left = AnimationTools.getPlayersInsideCube(l1, l2);

            var player = e.getPlayer();
            var loc = player.getLocation();

            var centerVector = AnimationTools.parseLocation(specialObjects.get("ROPE_CENTER"), world);
            if (right.contains(player)) {

                if (rope.getBoolMode()) {
                    if (rope.getBoolModeBool()) {
                        rope.addPoints(1);
                    }
                    rope.setBoolModeBool(!rope.getBoolModeBool());

                } else {
                    rope.addPoints(1);
                }

                if (random.nextInt(10) == 0) {
                    player.playSound(loc, "squid:sfx.rope_pull", 1, 1);
                }

                var leftVector = AnimationTools.parseLocation(specialObjects.get("ROPE_LEFT"), world);
                var vector = AnimationTools.getVector(centerVector, leftVector);
                left.forEach(p -> {
                    p.setVelocity(AnimationTools.superNormalize(vector.normalize()));
                });

                // TODO: MAKE SWING

            } else if (left.contains(player)) {
                if (rope.getBoolMode()) {
                    if (rope.getBoolModeBool()) {
                        rope.addPoints(-1);
                    }
                    rope.setBoolModeBool(!rope.getBoolModeBool());

                } else {
                    rope.addPoints(-1);
                }

                if (random.nextInt(10) == 0) {
                    player.playSound(loc, "squid:sfx.rope_pull", 1, 1);
                }

                var rightVector = AnimationTools.parseLocation(specialObjects.get("ROPE_RIGHT"), world);
                var vector = AnimationTools.getVector(centerVector, rightVector);

                right.forEach(p -> {
                    p.setVelocity(AnimationTools.superNormalize(vector.normalize()));
                });

                // TODO: MAKE SWING
            }

        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        var game = instance.getGame();
        var player = e.getPlayer();

        var rope = game.getRopeGame();
        rope.addBossBars(player);

    }

    @EventHandler
    public void onLeftWins(LeftWinsEvent e) {
        var game = instance.getGame();
        var rope = game.getRopeGame();
        instance.adminMessage(ChatColor.DARK_RED + "LEFT WINS");
        rope.setInGame(false);

        var specialObjects = AnimationTools.specialObjects;
        var world = Bukkit.getWorld("world");

        var centerVector = AnimationTools.parseLocation(specialObjects.get("ROPE_CENTER"), world);
        centerVector.add(0, 0.5, 0);

        var rightVector = AnimationTools.parseLocation(specialObjects.get("ROPE_RIGHT"), world);
        var vector = AnimationTools.getVector(centerVector, rightVector);

        var r1 = AnimationTools.parseLocation(specialObjects.get("RIGHT_SIDE_POS1"), world);
        var r2 = AnimationTools.parseLocation(specialObjects.get("RIGHT_SIDE_POS2"), world);

        var right = AnimationTools.getPlayersAdventureInsideCube(r1, r2);

        var guillotine = rope.moveGuillotine(true);

        guillotine.thenAccept(action -> {
            var task = new BukkitTCT();
            for (int i = 0; i < 5; i++) {
                task.addWithDelay(new BukkitRunnable() {
                    @Override
                    public void run() {
                        right.forEach(p -> {
                            p.setVelocity(vector);
                        });

                    }

                }, 50);
            }

            task.execute();

            Bukkit.getScheduler().runTask(instance, tsk -> {
                var ropeEntities = rope.getRightRope();
                ropeEntities.forEach(stand -> {
                    var equip = stand.getEquipment();
                    var helmet = equip.getHelmet();
                    if (helmet != null && helmet.getItemMeta().hasCustomModelData()
                            && helmet.getItemMeta().getCustomModelData() == 39) {
                        return;
                    }
                    AnimationTools.setStandModel(stand, Material.AIR, 0);
                });

            });
        });

    }

    @EventHandler
    public void onRightWins(RightWinsEvent e) {
        var game = instance.getGame();
        var rope = game.getRopeGame();
        instance.adminMessage(ChatColor.DARK_RED + "RIGHT WINS");
        rope.setInGame(false);

        var specialObjects = AnimationTools.specialObjects;
        var world = Bukkit.getWorld("world");

        var centerVector = AnimationTools.parseLocation(specialObjects.get("ROPE_CENTER"), world);
        centerVector.add(0, 0.5, 0);

        var leftVector = AnimationTools.parseLocation(specialObjects.get("ROPE_LEFT"), world);
        var vector = AnimationTools.getVector(centerVector, leftVector);

        var l1 = AnimationTools.parseLocation(specialObjects.get("LEFT_SIDE_POS1"), world);
        var l2 = AnimationTools.parseLocation(specialObjects.get("LEFT_SIDE_POS2"), world);

        var left = AnimationTools.getPlayersAdventureInsideCube(l1, l2);

        var guillotine = rope.moveGuillotine(true);

        guillotine.thenAccept(action -> {
            var task = new BukkitTCT();
            for (int i = 0; i < 5; i++) {
                task.addWithDelay(new BukkitRunnable() {
                    @Override
                    public void run() {
                        left.forEach(p -> {
                            p.setVelocity(vector);
                        });

                    }

                }, 50);
            }
            task.execute();

            Bukkit.getScheduler().runTask(instance, tsk -> {
                var ropeEntities = rope.getLeftRope();

                ropeEntities.forEach(stand -> {
                    var equip = stand.getEquipment();
                    var helmet = equip.getHelmet();
                    if (helmet != null && helmet.getItemMeta().hasCustomModelData()
                            && helmet.getItemMeta().getCustomModelData() == 39) {
                        return;
                    }
                    AnimationTools.setStandModel(stand, Material.AIR, 0);
                });

            });
        });

    }

}
