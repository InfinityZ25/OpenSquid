package us.jcedeno.skins;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import com.destroystokyo.paper.profile.ProfileProperty;
import com.mojang.authlib.properties.Property;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Subcommand;
import io.github.znetworkw.znpcservers.NPCLibrary;
import io.github.znetworkw.znpcservers.NPCWrapper;
import io.github.znetworkw.znpcservers.configuration.ConfigurationConstants;
import io.github.znetworkw.znpcservers.npc.NPC;
import me.aleiv.core.paper.Core;
import net.md_5.bungee.api.ChatColor;
import net.minecraft.server.v1_16_R3.PacketPlayOutPlayerInfo;
import uk.lewdev.entitylib.entity.FakePlayer;

/**
 * A command to interact with the skin-tool app from minecraft.
 * 
 * @author jcedeno
 */
@CommandPermission("admin.skin.cmd")
@CommandAlias("skin")
public class SkinCMD extends BaseCommand {
    Core instance;

    public SkinCMD(Core instance) {
        this.instance = instance;
        // register command completion
        instance.getCommandManager().getCommandCompletions().registerStaticCompletion("variants",
                List.of("civilian", "guard", "participant", "tux", "original"));
        // register the command itself
        instance.getCommandManager().registerCommand(this);
    }

    @Subcommand("npc")
    public void spawnNpc(Player sender) {

        var npc = NPCLibrary.createPlayerNPC(sender.getLocation(), sender.getName(), false, sender.getInventory(),
                sender);

        npc.getViewers().forEach(npc::spawn);

    }

    // @Subcommand("npc-demo")
    public void npcDemo(Player sender, @Default("5") Integer seconds) {
        /** Generate npcs along a circle away from the player */
        double radius = 5;
        /** Get the location of the player and store the world as a constant. */
        final var loc = sender.getLocation();
        final var world = loc.getWorld();

        /** Obtain the skin variants available for the current sender. */
        SkinToolApi.getElseComputeSkins(sender.getUniqueId()).whenComplete((skins, exception) -> {
            if (exception != null) {
                sender.sendMessage("Command ended exceptionally: " + exception.getMessage());
                exception.printStackTrace();
            } else {
                try {

                    var t = 360 / skins.size();
                    var iter = skins.iterator();

                    var npcs = new ArrayList<NPC>();

                    for (int i = 0; i < 360; i += t) {
                        var angle = Math.toRadians(i);
                        var x = Math.cos(angle) * radius;
                        var z = Math.sin(angle) * radius;

                        // Get the new location
                        final var newLoc = loc.clone().add(x, 0, z);
                        // Change Y To Highest block.
                        newLoc.setY(world.getHighestBlockYAt(newLoc.getBlockX(), newLoc.getBlockZ()) + 1);
                        var next = iter.next();
                        // Spawn the NPC
                        var name = Core.getMiniMessage().parse("<rainbow>" + sender.getName() + "-" + next.getName());
                        npcs.add(NPCLibrary.createPlayerNPC(newLoc, name.toString(), true, sender.getInventory(),
                                next.getValue(), next.getSignature()));

                    }

                    var wrapper = NPCWrapper.create(npcs);
                    // Delete the wrapper some seconds later.
                    Bukkit.getScheduler().runTaskLaterAsynchronously(Core.getInstance(), wrapper::deleteAll,
                            20 * seconds);

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

        });

    }

    private static ConcurrentHashMap<UUID, NPCWrapper> npcSkinnedCache = new ConcurrentHashMap<>();

    private AtomicInteger npcRandomCount = new AtomicInteger(0);

    @Subcommand("spawn-here")
    @CommandCompletion("@variants")
    public void spawnRandomHere(Player sender, String variant) {

        var id = sender.getUniqueId();

        final var wrapper = npcSkinnedCache.computeIfAbsent(id, (uid) -> NPCWrapper.create(new ArrayList<NPC>()));

        CompletableFuture.runAsync(() -> {

            var loc = sender.getLocation();
            var skinTexture = "";
            var skinSignature = "";

            var skinsOptional = SkinToolApi.getAllVariant(variant);
            if (skinsOptional.isPresent()) {
                var skins = skinsOptional.get();

                if (skins.size() < 1) {
                    sender.sendMessage(Core.getMiniMessage().parse("<red>No skins found for variant: " + variant));
                }

                var randomSkin = skins.get(npcRandomCount.getAndIncrement() < skins.size() ? npcRandomCount.get()
                        : npcRandomCount.getAndSet(0));
                var actualSkins = randomSkin;

                if (actualSkins != null) {
                    skinTexture = actualSkins.getValue();
                    skinSignature = actualSkins.getSignature();
                }

            }

            var npc = NPCLibrary.createPlayerNPC(loc, sender.getName() + "-" + variant, false, sender.getInventory(),
                    skinTexture, skinSignature);

            sender.sendMessage(
                    Core.getMiniMessage().parse("<green>Npc created with id <white>" + npc.getNpcPojo().getId()));

            wrapper.getNpcs().add(npc);

            npc.getViewers().forEach(npc::spawn);
        });

    }

    /**
     * 
     * @param sender
     */

    @Subcommand("delete-last")
    public void deleteLast(Player sender) {

        if (npcSkinnedCache.containsKey(sender.getUniqueId())) {
            var wrapper = npcSkinnedCache.get(sender.getUniqueId());

            sender.sendMessage("Deleting last NPC.");

            var npcs = new ArrayList<>(wrapper.getNpcs());
            Collections.reverse(npcs);

            var first = npcs.get(0);

            try {
                NPC.unregister(first.getNpcPojo().getId());
                ConfigurationConstants.NPC_LIST.remove(first.getNpcPojo());
            } catch (Exception e) {
                e.printStackTrace();
            }

            npcs.remove(0);

            Collections.reverse(npcs);
            wrapper.setNpcs(npcs);

        }

    }

    @Subcommand("delete-spawned-here")
    public void deleteNpcs(Player sender) {

        if (npcSkinnedCache.containsKey(sender.getUniqueId())) {
            var wrapper = npcSkinnedCache.get(sender.getUniqueId());

            sender.sendMessage("Deleting " + wrapper.getNpcs().size() + " npcs");

            wrapper.deleteAll();
        }
    }

    @Subcommand("spawn-new")
    public void spawnNewMethod(Player sender) {
        var playerSkin = sender.getPlayerProfile().getProperties().iterator().next();
        var loc = sender.getLocation();
        // Value es texture data, y signature es skin signature. Toma la posicion actual
        // del sender y el inventario.
        var npc = FakePlayer.of(sender.getName(), loc, playerSkin.getValue(), playerSkin.getSignature());
        // Enseñaselo a los jugadores que deberian de ver a ese npc.
        npc.show(sender);

    }

    @CommandCompletion("[integer]")
    @Subcommand("kill-radius")
    public void killInRadius(Player sender, @Default("2") Integer radius) {

        sender.getLocation();

        var allWrappers = npcSkinnedCache.values();

        var iter = allWrappers.iterator();

        OUTER: while (iter.hasNext()) {
            var next = iter.next();

            var npcs = next.getNpcs();

            var newList = new ArrayList<>(npcs);

            var skinsIter = newList.iterator();

            while (skinsIter.hasNext()) {
                var npc = skinsIter.next();

                if (npc.getLocation().distance(sender.getLocation()) < radius) {

                    sender.sendMessage(Core.getMiniMessage().parse("<green>Killing NPC: " + npc.getNpcPojo().getId()));

                    try {
                        NPC.unregister(npc.getNpcPojo().getId());
                        ConfigurationConstants.NPC_LIST.remove(npc.getNpcPojo());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    skinsIter.remove();

                    next.setNpcs(newList);
                    break OUTER;
                }
            }

        }

    }

    @Subcommand("add")
    @CommandCompletion("@players")
    public void addQueue(CommandSender sender, String... names) {

        List<UUID> users = new ArrayList<>();

        CompletableFuture.supplyAsync(() -> {
            try {
                for (String name : names) {

                    var uuid = SkinToolApi.getUserProfile(name);

                    if (uuid == null) {
                        sender.sendMessage(ChatColor.RED + "Player " + name + " doesn't exist.");
                        return false;
                    }
                    users.add(uuid);

                    sender.sendMessage(ChatColor.DARK_AQUA + name + " added to the queue.");
                }

                SkinToolApi.addSkinsToComputeQueue(users);

            } catch (Exception e) {
                e.printStackTrace();
                sender.sendMessage(ChatColor.RED + "An error ocurred.");
            }

            return false;
        });

    }

    @Subcommand("random")
    @CommandCompletion("@variants")
    public void randomSkins(CommandSender sender, String variant) {

        CompletableFuture.supplyAsync(() -> {
            // Ask the api for all skins of this type
            var optionalSkins = SkinToolApi.getAllVariant(variant);
            if (optionalSkins.isPresent()) {
                // Fitler out the not signed skins.
                var skins = optionalSkins.get().stream().filter(PlayerSkin::isItSigned).toList();
                if (skins.size() > 0) {
                    // Take a snapshot o of all players online to change their skins.
                    var players = Bukkit.getOnlinePlayers().stream().map(m -> m.getPlayer()).toList();
                    int skinIndex = 0;
                    // Get an iterator to easily loop through the players.
                    var playerIter = players.iterator();

                    while (playerIter.hasNext()) {
                        var nextPlayer = playerIter.next();
                        // Get current index and add 1
                        var skin = skins.get(skinIndex++);
                        // Ensure skin not null.
                        if (skin != null) {
                            Bukkit.getScheduler().runTask(instance, task -> {
                                SkinCMD.skinSwapper(nextPlayer, skin.getValue(), skin.getSignature()); // Swap the
                                                                                                       // Player's
                                // skin
                            });
                        }

                        // Handle possible out of bounds exception.
                        if (skinIndex > skins.size() - 1)
                            skinIndex = 0;
                    }
                }
            }
            return false;
        }).whenComplete((action, ex) -> {
            sender.sendMessage(ChatColor.DARK_AQUA + "Task skins random completed.");
        });

    }

    @Subcommand("list")
    @CommandCompletion("@variants")
    public void listVariants(CommandSender sender, String variants) {

    }

    @Subcommand("set-other")
    @CommandCompletion("@players @players @variants")
    public void changeSkinOther(CommandSender sender, String playerTarget, String skinSourcePlayer,
            @Default("original") String variant) {
        CompletableFuture.supplyAsync(() -> {
            try {

                var player = Bukkit.getPlayer(playerTarget);

                if (player != null && player.isOnline()) {
                    UUID id = null;

                    var ofP = Bukkit.getPlayer(skinSourcePlayer);

                    if (ofP != null && ofP.getUniqueId() != null) {
                        id = ofP.getUniqueId();
                    } else {
                        id = SkinToolApi.getUserProfile(skinSourcePlayer);
                    }

                    if (id != null) {
                        if (variant.equalsIgnoreCase("original")) {
                            var skin = SkinToolApi.getCurrentUserSkin(id, false);
                            Bukkit.getScheduler().runTask(Core.getInstance(),
                                    () -> skinSwapper(player, skin.getValue(), skin.getSignature()));

                        } else {
                            SkinToolApi.getElseComputeSkins(id).whenComplete((skins, exception) -> {
                                if (exception != null) {
                                    sender.sendMessage("Command ended exceptionally: " + exception.getMessage());
                                    exception.printStackTrace();
                                } else {
                                    var skin = skins.stream().filter(s -> s.getName().equalsIgnoreCase(variant))
                                            .findFirst();
                                    if (skin.isPresent()) {
                                        var actualSkin = skin.get();
                                        Bukkit.getScheduler().runTask(Core.getInstance(), () -> skinSwapper(player,
                                                actualSkin.getValue(), actualSkin.getSignature()));

                                    } else {
                                        sender.sendMessage("Skin not found");
                                    }
                                }

                            });
                        }
                    } else {
                        sender.sendMessage("§cThe player you specified does not have a skin set.");
                    }

                } else {
                    sender.sendMessage("§cPlayer not found.");
                }
            } catch (Exception e) {
                e.printStackTrace();
                sender.sendMessage("Error changin skins: " + e.getMessage());
            }

            return false;
        });
    }

    /**
     * Function that swaps a player's skin for a different one.
     * 
     * @param player    The player to swap the skin for.
     * @param texture   The texture to apply to the player.
     * @param signature The signature of the texture.
     */
    public static void skinSwapper(Player player, String texture, String signature) {
        player.sendMessage(Core.getMiniMessage().parse("<yellow>Changing your skin..."));
        var entityPlayer = ((CraftPlayer) player.getPlayer()).getHandle();
        var prof = entityPlayer.getProfile();
        var con = entityPlayer.playerConnection;

        con.sendPacket(
                new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, entityPlayer));
        prof.getProperties().removeAll("textures");
        prof.getProperties().put("textures", new Property("textures", texture, signature));
        con.sendPacket(
                new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, entityPlayer));

        var profilePurpur = player.getPlayerProfile();
        profilePurpur.setProperty(new ProfileProperty(player.getName(), texture, signature));

        player.setPlayerProfile(profilePurpur);
        player.sendMessage(Core.getMiniMessage().parse("<green>Skin changed!"));
        player.setPlayerTime(0, true);
        // Reset player time 2 ticks later.
        Bukkit.getScheduler().runTaskLater(Core.getInstance(), () -> player.resetPlayerTime(), 2);
    }

}
