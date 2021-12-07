package me.aleiv.core.paper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import com.destroystokyo.paper.ParticleBuilder;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Instrument;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.block.data.type.NoteBlock;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

import me.aleiv.core.paper.Game.DeathReason;
import me.aleiv.core.paper.map.packet.WrapperPlayServerGameStateChange;
import me.aleiv.core.paper.objects.NoteBlockData;
import me.aleiv.core.paper.objects.OffSet;
import me.aleiv.core.paper.utilities.LineVector;
import me.aleiv.core.paper.utilities.TCT.BukkitTCT;
import us.jcedeno.libs.rapidinv.ItemBuilder;


public class AnimationTools {

    public static Random random = new Random();
    public static HashMap<String, String> specialObjects = new HashMap<>();
    public static HashMap<String, NoteBlockData> noteBlocksMain = new HashMap<String, NoteBlockData>() {
        {
            put("P", new NoteBlockData(0, 0, Instrument.BASS_DRUM));
            put("L", new NoteBlockData(0, 1, Instrument.BASS_DRUM));
            put("A", new NoteBlockData(0, 2, Instrument.BASS_DRUM));
            put("Y", new NoteBlockData(0, 3, Instrument.BASS_DRUM));
            put("E", new NoteBlockData(0, 4, Instrument.BASS_DRUM));
            put("R", new NoteBlockData(0, 5, Instrument.BASS_DRUM));
            put("S", new NoteBlockData(0, 6, Instrument.BASS_DRUM));
            put("$", new NoteBlockData(1, 0, Instrument.BASS_DRUM));
            put("I", new NoteBlockData(1, 1, Instrument.BASS_DRUM));
            put("Z", new NoteBlockData(1, 2, Instrument.BASS_DRUM));
            put("0", new NoteBlockData(1, 3, Instrument.BASS_DRUM));
            put("1", new NoteBlockData(1, 4, Instrument.BASS_DRUM));
            put("2", new NoteBlockData(1, 5, Instrument.BASS_DRUM));
            put("3", new NoteBlockData(1, 6, Instrument.BASS_DRUM));

            put("4", new NoteBlockData(0, 0, Instrument.GUITAR));
            put("5", new NoteBlockData(0, 1, Instrument.GUITAR));
            put("6", new NoteBlockData(0, 2, Instrument.GUITAR));
            put("7", new NoteBlockData(0, 3, Instrument.GUITAR));
            put("8", new NoteBlockData(0, 4, Instrument.GUITAR));
            put("9", new NoteBlockData(0, 5, Instrument.GUITAR));

        }
    };

    public static HashMap<String, NoteBlockData> noteBlocksCount = new HashMap<String, NoteBlockData>() {
        {
            put(":", new NoteBlockData(0, 0, Instrument.BANJO));
            put("0", new NoteBlockData(0, 1, Instrument.BANJO));
            put("1", new NoteBlockData(0, 2, Instrument.BANJO));
            put("2", new NoteBlockData(0, 3, Instrument.BANJO));
            put("3", new NoteBlockData(0, 4, Instrument.BANJO));
            put("4", new NoteBlockData(0, 5, Instrument.BANJO));
            put("5", new NoteBlockData(0, 6, Instrument.BANJO));
            put("6", new NoteBlockData(1, 0, Instrument.BANJO));
            put("7", new NoteBlockData(1, 1, Instrument.BANJO));
            put("8", new NoteBlockData(1, 2, Instrument.BANJO));
            put("9", new NoteBlockData(1, 3, Instrument.BANJO));

        }
    };

    public static void forceSleep(Player player, Location loc) {
        player.sleep(loc, true);
    }

    public static void forceSleep(List<Player> players, List<Location> beds) {
        var random = new Random();

        for (var player : players) {
            var index = random.nextInt(beds.size());
            var bed = beds.remove(index);
            forceSleep(player, bed);
        }
    }

    public static List<Location> findLocations(String str) {
        var world = Bukkit.getWorld("world");
        return specialObjects.entrySet().stream().filter(entry -> entry.getKey().contains(str))
                .map(entry -> parseLocation(entry.getValue(), world)).collect(Collectors.toList());
    }

    public static List<Location> findOrderedLocations(String str) {
        var world = Bukkit.getWorld("world");
        var entry = specialObjects.entrySet().stream().filter(e -> e.getKey().contains(str)).collect(Collectors.toList());

        Collections.sort(entry, new Comparator<Entry<String, String>>() {
            @Override
            public int compare(Entry<String, String> p1, Entry<String, String> p2){
                var s1 = p1.getKey().split("_");
                Integer n1 = Integer.parseInt(s1[1]);

                var s2 = p2.getKey().split("_");
                Integer n2 = Integer.parseInt(s2[1]);

                return n1.compareTo(n2);
            }
        });

        return entry.stream().map(e -> parseLocation(e.getValue(), world)).toList();
    }

    public static void sendCredits(Player player) {
        // Create packet (src https://wiki.vg/Protocol#Change_Game_State)
        var packet = new WrapperPlayServerGameStateChange();
        // Set reason to 4 (win game)
        packet.setReason(4);
        // Send value to 1 (show end credits)
        packet.setValue(1);
        // Send the credits
        packet.sendPacket(player.getPlayer());
    }

    public static void setStandModel(ArmorStand stand, Material material, Integer model){
        var equip = stand.getEquipment();

        var item = material == Material.AIR ? null : new ItemBuilder(material).meta(meta -> meta.setCustomModelData(model)).build();
        equip.setHelmet(item);
    }

    public static Location getNearbyLocation(List<Location> locations, Location location) {
        Location nearbyLocation = locations.get(0);
        for (var loc : locations) {
            if (getDistance(nearbyLocation, location) > getDistance(loc, location)) {
                nearbyLocation = loc;
            }
        }
        return nearbyLocation;
    }

    public static double getDistance(Location loc1, Location loc2) {
        final int x1 = (int) loc1.getX();
        final int z1 = (int) loc1.getZ();

        final int x2 = (int) loc2.getX();
        final int z2 = (int) loc2.getZ();

        return Math.abs(Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(z2 - z1, 2)));
    }

    public static void shootLocation(Location loc) {
        var locations = findLocations("WALL_GUN");
        var wallGun = getNearbyLocation(locations, loc);
        var vector = getVector(loc.add(0, 1.40, 0), wallGun);

        wallGun.getWorld().spawnArrow(wallGun, vector, 20, 0);
        AnimationTools.playSoundDistance(wallGun, 300, "squid:sfx.dramatic_shot", 1f, 1f);
        var origin = wallGun.toVector();
        var target = loc.toVector();
        var vectors = LineVector.of(origin, target).getPointsInBetween();

        vectors.forEach(v -> {
            var l = v.toLocation(Bukkit.getWorld("world"));
            new ParticleBuilder(Particle.COMPOSTER).location(l).receivers(300).force(true).count(100)
                    .offset(0.000001, 0.000001, 0.000001).extra(0).spawn();
        });

    }

    public static Vector getVector(Location loc1, Location loc2) {
        return loc1.toVector().subtract(loc2.toVector());
    }

    public static Vector superNormalize(Vector vector) {
        var newVector = vector;
        newVector.setX(vector.getX()*0.03);
        newVector.setY(vector.getY()*0.03);
        newVector.setZ(vector.getZ()*0.03);
        return newVector;
    }

    public static CompletableFuture<Boolean> move(String name1, String name2, Integer value, Integer tickSpeed,
            char pos, Float distance) {

        var world = Bukkit.getWorld("world");
        var uuid1 = UUID.fromString(specialObjects.get(name1));
        var stand1 = (ArmorStand) world.getEntity(uuid1);

        var uuid2 = UUID.fromString(specialObjects.get(name2));
        var stand2 = (ArmorStand) world.getEntity(uuid2);

        var task = new BukkitTCT();

        var v = Math.abs(value);
        for (int i = 0; i < v; i++) {
            task.addWithDelay(new BukkitRunnable() {
                @Override
                public void run() {
                    var loc1 = stand1.getLocation();
                    var loc2 = stand2.getLocation();

                    var x1 = loc1.getX();
                    var y1 = loc1.getY();
                    var z1 = loc1.getZ();
                    var v1 = value < 0 ? -distance : distance;
                    var l1 = loc1.clone();

                    var x2 = loc2.getX();
                    var y2 = loc2.getY();
                    var z2 = loc2.getZ();
                    var v2 = value < 0 ? -distance : distance;
                    var l2 = loc2.clone();

                    switch (pos) {
                    case 'x': {
                        l1.setX(x1 + v1);
                        l2.setX(x2 + -v2);
                    }
                        break;
                    case 'y': {
                        l1.setY(y1 + v1);
                        l2.setY(y2 + -v2);
                    }
                        break;
                    case 'z':
                        l1.setZ(z1 + v1);
                        l2.setZ(z2 + -v2);
                        break;

                    default:
                        break;
                    }
                    stand1.teleport(l1);
                    stand2.teleport(l2);
                }
            }, 50 * tickSpeed);
        }
        return task.execute();
    }

    public static CompletableFuture<Boolean> move(String name, Integer value, Integer tickSpeed, char pos,
            Float distance) {

        var uuid = UUID.fromString(specialObjects.get(name));
        var stand = (ArmorStand) Bukkit.getWorld("world").getEntity(uuid);

        var task = new BukkitTCT();

        var v = Math.abs(value);
        for (int i = 0; i < v; i++) {
            task.addWithDelay(new BukkitRunnable() {
                @Override
                public void run() {
                    var loc = stand.getLocation();
                    var x = loc.getX();
                    var y = loc.getY();
                    var z = loc.getZ();
                    var v = value < 0 ? -distance : distance;
                    var l = loc.clone();
                    switch (pos) {
                    case 'x':
                        l.setX(x + v);
                        break;
                    case 'y':
                        l.setY(y + v);
                        break;
                    case 'z':
                        l.setZ(z + v);
                        break;

                    default:
                        break;
                    }
                    stand.teleport(l);
                }
            }, 50 * tickSpeed);
        }
        return task.execute();
    }

    public static CompletableFuture<Boolean> move(List<String> names, Integer value, Integer tickSpeed, char pos,
            Float distance) {

        var world = Bukkit.getWorld("world");
        var stands = names.stream().map(name -> (ArmorStand) world.getEntity(UUID.fromString(specialObjects.get(name))))
                .toList();
        var task = new BukkitTCT();

        var v = Math.abs(value);
        for (int i = 0; i < v; i++) {
            task.addWithDelay(new BukkitRunnable() {
                @Override
                public void run() {
                    stands.forEach(stand -> {
                        var loc = stand.getLocation();
                        var x = loc.getX();
                        var y = loc.getY();
                        var z = loc.getZ();
                        var v = value < 0 ? -distance : distance;
                        var l = loc.clone();
                        switch (pos) {
                        case 'x':
                            l.setX(x + v);
                            break;
                        case 'y':
                            l.setY(y + v);
                            break;
                        case 'z':
                            l.setZ(z + v);
                            break;

                        default:
                            break;
                        }
                        stand.teleport(l);
                    });
                }
            }, 50 * tickSpeed);
        }
        return task.execute();
    }

    public static CompletableFuture<Boolean> moveEntitys(List<Entity> entitys, Integer value, Integer tickSpeed, char pos, Float distance) {
        var task = new BukkitTCT();
        var v = Math.abs(value);
        for (int i = 0; i < v; i++) {
            task.addWithDelay(new BukkitRunnable() {
                @Override
                public void run() {
                    entitys.forEach(stand -> {
                        var loc = stand.getLocation();
                        var x = loc.getX();
                        var y = loc.getY();
                        var z = loc.getZ();
                        var v = value < 0 ? -distance : distance;
                        var l = loc.clone();
                        switch (pos) {
                        case 'x':
                            l.setX(x + v);
                            break;
                        case 'y':
                            l.setY(y + v);
                            break;
                        case 'z':
                            l.setZ(z + v);
                            break;

                        default:
                            break;
                        }
                        stand.teleport(l);
                    });
                }
            }, 50 * tickSpeed);
        }
        return task.execute();
    }

    public static CompletableFuture<Boolean> rotate(String name, Integer value, Integer tickSpeed, Float amount) {
        var uuid = UUID.fromString(specialObjects.get(name));
        var stand = (ArmorStand) Bukkit.getWorld("world").getEntity(uuid);

        var task = new BukkitTCT();

        var v = Math.abs(value);
        for (int i = 0; i < v; i++) {
            task.addWithDelay(new BukkitRunnable() {
                @Override
                public void run() {
                    var part = stand.getHeadPose();
                    var x = part.getX();
                    var y = part.getY();
                    var z = part.getZ();
                    var v = value < 0 ? y - amount : y + amount;

                    stand.setHeadPose(new EulerAngle(x, v, z));
                }
            }, 50 * tickSpeed);
        }
        return task.execute();
    }

    public static List<Location> getBlocksInsideCube(Location loc1, Location loc2) {
        List<Location> locations = new ArrayList<>();

        var xa = (int) (loc1.getX() > loc2.getX() ? loc1.getX() : loc2.getX());
        var ya = (int) (loc1.getY() > loc2.getY() ? loc1.getY() : loc2.getY());
        var za = (int) (loc1.getZ() > loc2.getZ() ? loc1.getZ() : loc2.getZ());

        var xi = (int) (loc1.getX() < loc2.getX() ? loc1.getX() : loc2.getX());
        var yi = (int) (loc1.getY() < loc2.getY() ? loc1.getY() : loc2.getY());
        var zi = (int) (loc1.getZ() < loc2.getZ() ? loc1.getZ() : loc2.getZ());

        for (int x = xi; x <= xa; x++) {
            for (int y = yi; y <= ya; y++) {
                for (int z = zi; z <= za; z++) {
                    locations.add(new Location(loc1.getWorld(), x, y, z));
                }
            }
        }

        return locations;
    }

    public static void fill(Location loc1, Location loc2, Material material) {
        var locations = getBlocksInsideCube(loc1, loc2);
        locations.forEach(loc -> {
            loc.getBlock().setType(material);
        });
    }

    public static ArmorStand getStand(String name) {
        var uuid = UUID.fromString(specialObjects.get(name));
        return (ArmorStand) Bukkit.getWorld("world").getEntity(uuid);
    }

    private static Integer parseInteger(String str) {
        return str.startsWith("-") ? -Integer.parseInt(str.replace("-", "")) : Integer.parseInt(str);
    }

    public static Location parseLocation(String loc, World world) {
        var positions = loc.split(";");
        var x = parseInteger(positions[0]);
        var y = parseInteger(positions[1]);
        var z = parseInteger(positions[2]);

        return new Location(world, x, y, z);
    }

    public static void playSoundDistance(Location loc, Integer distance, String sound, Float volume, Float pitch) {
        loc.getNearbyPlayers(distance).forEach(player -> {
            player.playSound(loc, sound, volume, pitch);
        });
    }

    public static ArmorStand getArmorStand(String stand){
        var uuid = UUID.fromString(specialObjects.get(stand));
        return (ArmorStand) Bukkit.getWorld("world").getEntity(uuid); 
    }

    public static Entity getEntity(String entity){
        var uuid = UUID.fromString(specialObjects.get(entity));
        return Bukkit.getWorld("world").getEntity(uuid); 
    }

    public static void setScreenValue(List<Location> locations, String str) {
        AnimationTools.playSoundDistance(locations.get(0), 100, "squid:sfx.main_board", 1f, 1f);
        var array = str.toCharArray();
        var count = 0;
        for (char c : array) {
            var loc = locations.get(count);
            setBlockValue(true, loc, String.valueOf(c));
            count++;
        }

    }

    public static void setTimerValue(List<Location> locations, String str) {
        var array = str.toCharArray();
        var count = 0;
        for (char c : array) {
            var loc = locations.get(count);
            setBlockValue(false, loc, String.valueOf(c));
            count++;
        }

    }

    public static void setBlockValue(Boolean mainMap, Location location, String note) {
        HashMap<String, NoteBlockData> map;
        if (mainMap) {
            map = noteBlocksMain;
        } else {
            map = noteBlocksCount;
        }
        var n = map.get(note);
        NoteBlock noteBlock = (NoteBlock) Material.NOTE_BLOCK.createBlockData();
        noteBlock.setNote(n.getNote());
        noteBlock.setInstrument(n.getInstrument());
        location.getBlock().setType(Material.NOTE_BLOCK);
        location.getBlock().setBlockData(noteBlock);

    }

    public static String getFormattedNumber(Integer i, Integer zeros) {
        var str = new StringBuilder();
        for (int j = 0; j < zeros; j++) {
            str.append("0");
        }
        str.append(i);
        return str.toString();
    }

    public static boolean isInCube(Location pos1, Location pos2, Location point) {

        var cX = pos1.getX() < pos2.getX();
        var cY = pos1.getY() < pos2.getY();
        var cZ = pos1.getZ() < pos2.getZ();

        var minX = cX ? pos1.getX() : pos2.getX();
        var maxX = cX ? pos2.getX() : pos1.getX();

        var minY = cY ? pos1.getY() : pos2.getY();
        var maxY = cY ? pos2.getY() : pos1.getY();

        var minZ = cZ ? pos1.getZ() : pos2.getZ();
        var maxZ = cZ ? pos2.getZ() : pos1.getZ();

        if (point.getX() < minX || point.getY() < minY || point.getZ() < minZ)
            return false;
        if (point.getX() > maxX || point.getY() > maxY || point.getZ() > maxZ)
            return false;

        return true;
    }

    public static List<Player> getPlayersInsideCube(Location pos1, Location pos2) {
        return Bukkit.getOnlinePlayers().stream().filter(p -> isInCube(pos1, pos2, p.getLocation()))
                .map(p -> (Player) p).toList();
    }

    public static List<Player> getPlayersAdventureInsideCube(Location pos1, Location pos2){
        return getPlayersInsideCube(pos1, pos2).stream().filter(player -> player.getGameMode() == GameMode.ADVENTURE).toList();
    }

    public static ArmorStand getFormattedStand(World world, Location loc) {
        var stand = (ArmorStand) world.spawnEntity(loc, EntityType.ARMOR_STAND);
        stand.setInvisible(true);
        stand.setArms(true);
        stand.setInvulnerable(true);
        stand.setBasePlate(false);
        stand.addDisabledSlots(EquipmentSlot.values());
        return stand;
    }

    public static ItemStack getModelItem(Material material, Integer model) {
        return new ItemBuilder(material).meta(meta -> meta.setCustomModelData(model)).build();
    }

    public static ArmorStand summonDeadBody(Player player, DeathReason deathReason, Projectile projectile) {
        var world = player.getWorld();
        var loc = player.getLocation();
        var uuid = player.getUniqueId();
        var head = new ItemBuilder(Material.PLAYER_HEAD)
                .meta(SkullMeta.class, meta -> meta.setOwningPlayer(Bukkit.getOfflinePlayer(uuid))).build();

        switch (deathReason) {
        case EXPLOSION -> {

            var part = AnimationTools.getFormattedStand(world, loc);
            var equip = part.getEquipment();
            equip.setItemInOffHand(getModelItem(Material.LEATHER, 1));
            part.setVelocity(getRandomVector(3).normalize());

            part = AnimationTools.getFormattedStand(world, loc);
            equip = part.getEquipment();
            equip.setItemInOffHand(getModelItem(Material.LEATHER, 2));
            part.setVelocity(getRandomVector(3).normalize());

            part = AnimationTools.getFormattedStand(world, loc);
            equip = part.getEquipment();
            equip.setItemInOffHand(getModelItem(Material.LEATHER, 3));
            part.setVelocity(getRandomVector(3).normalize());

            part = AnimationTools.getFormattedStand(world, loc);
            equip = part.getEquipment();
            equip.setItemInOffHand(getModelItem(Material.LEATHER, 4));
            part.setVelocity(getRandomVector(3).normalize());

            part = AnimationTools.getFormattedStand(world, loc);
            equip = part.getEquipment();
            equip.setItemInOffHand(getModelItem(Material.LEATHER, 5));
            part.setVelocity(getRandomVector(3).normalize());

            part = AnimationTools.getFormattedStand(world, loc);
            equip = part.getEquipment();
            equip.setItemInOffHand(head);
            part.setVelocity(getRandomVector(3).normalize());

            new ParticleBuilder(Particle.TOTEM).location(loc).receivers(300).force(true).count(1000).offset(1, 1, 1)
                    .extra(0.5).spawn();

        }
        case PROJECTILE -> {
            if (random.nextInt(7) < 2) {
                var body = new ItemBuilder(Material.LEATHER)
                        .meta(meta -> meta.setCustomModelData(random.nextInt(2)+11)).build();

                var stand = AnimationTools.getFormattedStand(world, loc);
                var equip = stand.getEquipment();
                equip.setItemInOffHand(body);
                equip.setItemInMainHand(head);

                var vector = projectile.getVelocity();
                vector.normalize();
                stand.setVelocity(vector);
                return stand;

            } else {
                var body = new ItemBuilder(Material.LEATHER)
                        .meta(meta -> meta.setCustomModelData(random.nextInt(4) + 6)).build();

                var stand = AnimationTools.getFormattedStand(world, loc);
                var equip = stand.getEquipment();
                equip.setItemInOffHand(head);
                equip.setItemInMainHand(body);

                var vector = projectile.getVelocity();
                vector.normalize();
                stand.setVelocity(vector);
                return stand;

            }

        }
        case NORMAL -> {
            if (random.nextInt(7) < 2) {
                var body = new ItemBuilder(Material.LEATHER).meta(meta -> meta.setCustomModelData(random.nextInt(2) + 11)).build();

                var stand = AnimationTools.getFormattedStand(world, loc);
                var equip = stand.getEquipment();
                equip.setItemInOffHand(body);
                equip.setItemInMainHand(head);
    
                stand.setVelocity(getRandomVector(1).normalize());
    
                return stand;
            } else {
                var body = new ItemBuilder(Material.LEATHER).meta(meta -> meta.setCustomModelData(random.nextInt(4) + 6)).build();

                var stand = AnimationTools.getFormattedStand(world, loc);
                var equip = stand.getEquipment();
                equip.setItemInOffHand(head);
                equip.setItemInMainHand(body);
    
                stand.setVelocity(getRandomVector(1).normalize());
    
                return stand;
            }

        }
        }

        return null;
    }

    public static Vector getRandomVector(Integer i) {
        var vector = new Vector(getRandomValue(i), random.nextInt(i), getRandomValue(i));
        return vector;

    }

    public static Float getRandomValue(Integer i) {
        var inte = random.nextInt(i);
        var value = inte + random.nextFloat();
        return random.nextBoolean() ? value : -value;
    }

    public static OffSet getOffSet(Location pos1, Location pos2) {
        var x1 = pos1.getX();
        var y1 = pos1.getY();
        var z1 = pos1.getZ();

        var x2 = pos2.getX();
        var y2 = pos2.getY();
        var z2 = pos2.getZ();

        double xOffSet = getDifference(x1, x2);
        double yOffSet = getDifference(y1, y2);
        double zOffSet = getDifference(z1, z2);

        return new OffSet(xOffSet, yOffSet, zOffSet);
    }

    public static double getDifference(double a, double b) {
        return b - a;
    }

}
