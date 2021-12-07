package uk.lewdev.entitylib.entity;

import org.bukkit.entity.EntityType;

/**
 * Generated using {@link uk.lewdev.datagen.EntityEnumGenerator}
 *
 * @version 1.17.1 (INCOMPLETE)
 */
public enum EntityId {
    MINECART_TNT(56),
    CHICKEN(10),
    OCELOT(59),
    PIGLIN_BRUTE(66),
    SKELETON_HORSE(79),
    SALMON(73),
    ENDER_SIGNAL(26),
    STRAY(87),
    IRON_GOLEM(40),
    VEX(97),
    SMALL_FIREBALL(81),
    TRIDENT(93),
    EVOKER_FANGS(24),
    HUSK(38),
    ITEM_FRAME(42),
    ENDER_PEARL(90),
    PUFFERFISH(70),
    TURTLE(96),
    SHEEP(74),
    TROPICAL_FISH(95),
    SLIME(80),
    BLAZE(6),
    BAT(4),
    FIREWORK(28),
    SPECTRAL_ARROW(84),
    ENDER_DRAGON(20),
    SNOWMAN(82),
    VILLAGER(98),
    LLAMA_SPIT(47),
    CAT(8),
    DROPPED_ITEM(41),
    SNOWBALL(83),
    PIG(64),
    EGG(89),
    TRADER_LLAMA(94),
    MINECART_CHEST(51),
    CREEPER(13),
    WITHER(102),
    ENDER_CRYSTAL(19),
    EXPERIENCE_ORB(25),
    LIGHTNING(45),
    HORSE(37),
    ZOMBIE_HORSE(108),
    ILLUSIONER(39),
    ZOMBIE_VILLAGER(109),
    ENDERMITE(22),
    SKELETON(78),
    EVOKER(23),
    THROWN_EXP_BOTTLE(91),
    WITHER_SKELETON(103),
    MINECART_HOPPER(54),
    PAINTING(60),
    PANDA(61),
    GIANT(31),
    COD(11),
    WANDERING_TRADER(100),
    DROWNED(17),
    FISHING_HOOK(112),
    SQUID(86),
    MULE(57),
    SPIDER(85),
    GUARDIAN(35),
    PHANTOM(63),
    ENDERMAN(21),
    VINDICATOR(99),
    ZOMBIFIED_PIGLIN(110),
    MAGMA_CUBE(48),
    MUSHROOM_COW(58),
    ZOGLIN(106),
    FALLING_BLOCK(27),
    POLAR_BEAR(68),
    PILLAGER(67),
    PARROT(62),
    ZOMBIE(107),
    COW(12),
    FOX(29),
    BEE(5),
    FIREBALL(43),
    SHULKER_BULLET(76),
    GHAST(30),
    DOLPHIN(14),
    BOAT(7),
    STRIDER(88),
    AREA_EFFECT_CLOUD(0),
    LEASH_HITCH(44),
    RAVAGER(72),
    MINECART_MOB_SPAWNER(55),
    SHULKER(75),
    MINECART_COMMAND(52),
    MINECART_FURNACE(53),
    SPLASH_POTION(92),
    DRAGON_FIREBALL(16),
    WOLF(105),
    ELDER_GUARDIAN(18),
    DONKEY(15),
    WITCH(101),
    WITHER_SKULL(104),
    PLAYER(111),
    SILVERFISH(77),
    ARMOR_STAND(1),
    CAVE_SPIDER(9),
    LLAMA(46),
    RABBIT(71),
    ARROW(2),
    HOGLIN(36),
    MINECART(50),
    PIGLIN(65),
    PRIMED_TNT(69);

    private final int id;

    EntityId(int id) {
        this.id = id;
    }

    public static int fromType(EntityType type) {
        return EntityId.valueOf(type.name()).getId();
    }

    public int getId() {
        return this.id;
    }
}
