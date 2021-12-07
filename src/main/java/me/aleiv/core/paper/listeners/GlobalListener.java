package me.aleiv.core.paper.listeners;

import com.destroystokyo.paper.ParticleBuilder;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerRespawnEvent.RespawnFlag;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import me.aleiv.core.paper.AnimationTools;
import me.aleiv.core.paper.Core;
import me.aleiv.core.paper.Game.DeathReason;
import me.aleiv.core.paper.Game.GameStage;
import me.aleiv.core.paper.Game.PvPType;
import me.aleiv.core.paper.Game.Role;
import me.aleiv.core.paper.events.GameTickEvent;
import me.aleiv.core.paper.utilities.TCT.BukkitTCT;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.md_5.bungee.api.ChatColor;
import us.jcedeno.libs.rapidinv.ItemBuilder;

public class GlobalListener implements Listener {

    Core instance;

    String RED = "<#c11f27>";
    String CYAN = "<#4be2ba>";

    public GlobalListener(Core instance) {
        this.instance = instance;
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent e){
        var player = e.getEntity();
        var game = instance.getGame();
        var roles = game.getRoles();
        var uuid = player.getUniqueId().toString();
        var role = roles.get(uuid);
        var gamemode = player.getGameMode();
        if(role == Role.PLAYER && gamemode == GameMode.ADVENTURE){
            var damageEvent = player.getLastDamageCause();
            if(damageEvent instanceof EntityDamageByEntityEvent damageEntity && damageEntity.getDamager() instanceof Projectile projectile){
                AnimationTools.summonDeadBody(player, DeathReason.PROJECTILE, projectile);
            }else{
                var cause = damageEvent.getCause();
                if(cause == DamageCause.BLOCK_EXPLOSION || cause == DamageCause.ENTITY_EXPLOSION){
                    AnimationTools.summonDeadBody(player, DeathReason.EXPLOSION, null);

                }else{
                    AnimationTools.summonDeadBody(player, DeathReason.NORMAL, null);
                }
            }
            //TODO:make dead
        }

        if(player.hasPermission("admin.perm") || game.isGuard(player) || player.getGameMode() != GameMode.ADVENTURE){
            e.deathMessage(MiniMessage.get().parse(""));
        }else{
            e.deathMessage(MiniMessage.get().parse(CYAN + "Player " + ChatColor.WHITE + "0 " + player.getName() + CYAN + " eliminated."));
        }

    }

    @EventHandler
    public void onCredits(PlayerRespawnEvent e){
        var flags = e.getRespawnFlags();
        var player = e.getPlayer();
        var game = instance.getGame();
        player.setGameMode(GameMode.SPECTATOR);

        if(flags.contains(RespawnFlag.BED_SPAWN) && flags.contains(RespawnFlag.END_PORTAL)){
            //end credits
            if(game.isPlayer(player)){
                player.kick(MiniMessage.get().parse(RED + "¡Gracias por jugar!"));
            }

        }else if(flags.contains(RespawnFlag.BED_SPAWN)){
            //just died

            e.setRespawnLocation(new Location(Bukkit.getWorld("world"), 18, 69, -6));
            //TODO: SEND Credits
            //AnimationTools.sendCredits(player);
        }
    }

    @EventHandler
    public void onFood(FoodLevelChangeEvent e){
        var player = (Player) e.getEntity();
        if(!player.hasPotionEffect(PotionEffectType.SATURATION) && !player.hasPotionEffect(PotionEffectType.HUNGER)){
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e){
        var player = e.getPlayer();
        e.quitMessage(MiniMessage.get().parse(""));
        instance.adminMessage(ChatColor.LIGHT_PURPLE + player.getName() + " left the game");
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        var game = instance.getGame();
        var roles = game.getRoles();
        var player = e.getPlayer();
        var uuid = player.getUniqueId().toString();

        e.joinMessage(MiniMessage.get().parse(""));
        instance.adminMessage(ChatColor.YELLOW + player.getName() + " joined the game");

        if (!roles.containsKey(uuid)) {
            if (player.hasPermission("admin.perm")) {
                roles.put(uuid, Role.GUARD);
            } else {
                roles.put(uuid, Role.PLAYER);
            }
        }

        var timer = game.getTimer();
        timer.getBossBar().addPlayer(player);

        var city = game.getCity();
        var whiteLobby = game.getWhiteLobby();


        var world = Bukkit.getWorld("world");
        if(city == null) game.setCity(new Location(world, 180.5, 35, 401.5));
        //if(whiteLobby == null) game.setWhiteLobby(new Location(world, 274, 55, -61));

        //TODO WHITE LOBBY
        if(whiteLobby == null) game.setWhiteLobby(new Location(world, 18, 69, -6));

        if (game.getGameStage() == GameStage.LOBBY) {
            player.teleport(city);

            if(game.isPlayer(player)){
                var inv = player.getInventory();
                inv.clear();

                var card = new ItemBuilder(Material.BRICK).meta(meta -> meta.setCustomModelData(25)).name("●▲■").build();
                inv.addItem(card);

                //TODO: ADD CIVILIAN SKIN

            }

        }else if(game.getGameStage() ==  GameStage.INGAME){

            if(!player.hasPlayedBefore()){

                player.teleport(whiteLobby);
            }
        }

        if (game.getLights()) {
            player.addPotionEffect(
                    new PotionEffect(PotionEffectType.NIGHT_VISION, 20 * 1000000, 100, false, false, false));
        } else {
            player.removePotionEffect(PotionEffectType.NIGHT_VISION);
        }

    }

    @EventHandler
    public void onSpawn(CreatureSpawnEvent e) {
        if (e.getSpawnReason().toString().contains("NATURAL")) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void gameTickEvent(GameTickEvent e) {
        var game = instance.getGame();
        Bukkit.getScheduler().runTask(instance, () -> {

            var timer = game.getTimer();
            if (timer.isActive()) {
                var currentTime = (int) game.getGameTime();
                timer.refreshTime(currentTime);
            }

        });

    }

    @EventHandler
    public void playerMove(PlayerMoveEvent e) {
        var game = instance.getGame();
        if (game.getGameStage() != GameStage.LOBBY)
            return;

        var player = e.getPlayer();

        if (game.isPlayer(player)) {
            var from = e.getFrom();
            var to = e.getTo();
            var x1 = from.getX();
            var z1 = from.getZ();
            var x2 = to.getX();
            var z2 = to.getZ();
            if (x1 != x2 || z1 != z2) {
                e.setCancelled(true);
            }
        }

    }

    @EventHandler
    public void onShoot(EntityDamageByEntityEvent e) {
        var entity = e.getEntity();
        var damager = e.getDamager();
        if (entity instanceof Player player) {

            var game = instance.getGame();
            var roles = game.getRoles();
            var uuid = player.getUniqueId().toString();
            var role = roles.get(uuid);
            var pvp = game.getPvp();
            var loc = player.getLocation().clone().add(0, 1, 0);

            var animation = 0;

            if (damager instanceof Player playerDamager && pvp != PvPType.ALL) {
                var damagerRole = roles.get(playerDamager.getUniqueId().toString());

                if (role == Role.GUARD && role == damagerRole && !playerDamager.hasPermission("admin.perm")) {
                    // GUARD TO GUARD CASE
                    e.setCancelled(true);
                    return;

                } else if (role == Role.GUARD && damagerRole == Role.PLAYER){
                    // PLAYER TO GUARD CASE
                    e.setCancelled(true);
                    return;

                }else if (role == Role.PLAYER && role == damagerRole && pvp == PvPType.ONLY_GUARDS) {
                    // PLAYER TO PLAYER CASE
                    e.setCancelled(true);
                    return;
                }

            } else if (damager instanceof Projectile) {
                animation = 2;
                e.setDamage(30);
            }

            var inv = player.getInventory();
            var item = inv.getItemInMainHand();
            if (item != null && item.getType().toString().contains("SWORD")) {
                animation = 1;
            }

            if (animation == 0) {
                new ParticleBuilder(Particle.TOTEM).location(loc).receivers(20).force(true).count(20)
                        .offset(0.0001, 0.0001, 0.0001).extra(0.05).spawn();
            } else {

                var task = new BukkitTCT();
                final var a = animation;

                for (int i = 0; i < 5; i++) {
                    task.addWithDelay(new BukkitRunnable() {
                        @Override
                        public void run() {
                            var loc = player.getLocation();
                            switch (a) {
                            case 1: {
                                // KNIFE CASE
                                new ParticleBuilder(Particle.TOTEM).location(loc).receivers(20).force(true).count(100)
                                        .offset(0.1, 0.5, 0.1).extra(0.05).spawn();
                            }
                                break;

                            case 2: {
                                // SHOOT CASE
                                new ParticleBuilder(Particle.TOTEM).location(loc).receivers(20).force(true).count(100)
                                        .offset(0.1, 0.5, 0.1).extra(0.05).spawn();
                            }
                                break;

                            default:
                                break;
                            }

                        }
                    }, 50 * 2);
                }

                task.execute();
            }

        }
    }

}
