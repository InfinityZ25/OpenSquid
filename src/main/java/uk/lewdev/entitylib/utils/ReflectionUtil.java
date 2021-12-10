package uk.lewdev.entitylib.utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class ReflectionUtil {

    /*
     * The server version string to location NMS & OBC classes
     */
    private static String versionString;
    /*
     * Cache of NMS classes that we've searched for
     */
    private static final Map<String, Class<?>> loadedClasses = new HashMap<String, Class<?>>();

    /*
     * Cache of methods that we've found in particular classes
     */
    private static final Map<Class<?>, Map<String, Method>> loadedMethods = new HashMap<Class<?>, Map<String, Method>>();
    /*
     * Cache of fields that we've found in particular classes
     */
    private static final Map<Class<?>, Map<String, Field>> loadedFields = new HashMap<Class<?>, Map<String, Field>>();

    /**
     * Get an NMS Class
     *
     * @param nmsClassName The name of the class
     * @return The class
     */
    public static Class<?> getNMSClass(String nmsClassName) {
        String clazzName = "net.minecraft.server." + getVersion() + nmsClassName;
        return getClass(clazzName);
    }

    /**
     * Get a class from the org.bukkit.craftbukkit package
     *
     * @param obcClassName the path to the class
     * @return the found class at the specified path
     */
    public synchronized static Class<?> getOBCClass(String obcClassName) {
        String clazzName = "org.bukkit.craftbukkit." + getVersion() + obcClassName;
        return getClass(clazzName);
    }

    /**
     * Get a class from the org.bukkit.craftbukkit package
     *
     * @param obcClassName the path to the class
     * @return the found class at the specified path
     */
    public synchronized static Class<?> getClass(String clazzName) {
        if (loadedClasses.containsKey(clazzName)) {
            return loadedClasses.get(clazzName);
        }

        Class<?> clazz;
        try {
            clazz = Class.forName(clazzName);
        } catch (Throwable t) {
            t.printStackTrace();
            loadedClasses.put(clazzName, null);
            return null;
        }

        loadedClasses.put(clazzName, clazz);
        return clazz;
    }

    /**
     * Get a classes constructor
     *
     * @param clazz  The constructor class
     * @param params The parameters in the constructor
     * @return The constructor object
     */
    public static Constructor<?> getConstructor(Class<?> clazz, Class<?>... params)
            throws NoSuchMethodException, SecurityException {
        return clazz.getConstructor(params);
    }

    /**
     * Get a field with a particular name from a class
     *
     * @param clazz     The class
     * @param fieldName The name of the field
     * @return The field object
     */
    public static Field getField(Class<?> clazz, String fieldName) throws NoSuchFieldException, SecurityException {
        if (!loadedFields.containsKey(clazz)) {
            loadedFields.put(clazz, new HashMap<String, Field>());
        }
        Map<String, Field> fields = loadedFields.get(clazz);
        if (fields.containsKey(fieldName)) {
            return fields.get(fieldName);
        }
        Field field = clazz.getField(fieldName);
        fields.put(fieldName, field);
        loadedFields.put(clazz, fields);
        return field;
    }

    /**
     * Set a field with the particular value.
     *
     * @param obj       The Object of the class
     * @param fieldName The name of the field
     * @param value     The value to set the field to.
     */
    public static void setField(Object obj, String fieldName, Object value)
            throws NoSuchFieldException, SecurityException, IllegalArgumentException,
            IllegalAccessException {
        getField(obj.getClass(), fieldName).set(obj, value);
    }

    /**
     * Get a declared field with a particular name from a class
     *
     * @param clazz     The class
     * @param fieldName The name of the field
     * @return The field object
     */
    public static Field getDeclaredField(Class<?> clazz, String fieldName)
            throws NoSuchFieldException, SecurityException {
        Field field = clazz.getDeclaredField(fieldName);
        field.setAccessible(true);
        return field;
    }

    /**
     * Set a field with the particular value.
     *
     * @param obj       The Object of the class
     * @param fieldName The name of the field
     * @param value     The value to set the field to.
     */
    public static void setDeclaredField(Object obj, String fieldName, Object value)
            throws NoSuchFieldException, SecurityException, IllegalArgumentException,
            IllegalAccessException {
        getDeclaredField(obj.getClass(), fieldName).set(obj, value);
    }

    /**
     * Get a method from a class that has the specific paramaters
     *
     * @param clazz      The class we are searching
     * @param methodName The name of the method
     * @param params     Any parameters that the method has
     * @return The method with appropriate paramaters
     */
    public static Method getMethod(Class<?> clazz, String methodName, Class<?>... params)
            throws NoSuchMethodException, SecurityException {
        if (!loadedMethods.containsKey(clazz)) {
            loadedMethods.put(clazz, new HashMap<String, Method>());
        }
        Map<String, Method> methods = loadedMethods.get(clazz);
        if (methods.containsKey(methodName)) {
            return methods.get(methodName);
        }
        Method method = clazz.getMethod(methodName, params);
        methods.put(methodName, method);
        loadedMethods.put(clazz, methods);
        return method;
    }

    /**
     * Get a declared method from a class that has the specific paramaters
     *
     * @param clazz      The class we are searching
     * @param methodName The name of the method
     * @param params     Any parameters that the method has
     * @return The method with appropriate paramaters
     */
    public static Method getDeclaredMethod(Class<?> clazz, String methodName, Class<?>... params)
            throws NoSuchMethodException, SecurityException {
        Method method = clazz.getDeclaredMethod(methodName, params);
        method.setAccessible(true);
        return method;
    }

    /**
     * @see PacketUtils#getConnection(Player)
     */
    @Deprecated
    public static Object getConnection(Player player)
            throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchFieldException,
            SecurityException, NoSuchMethodException {
        Method getHandleMethod = getMethod(player.getClass(), "getHandle");
        Object nmsPlayer = getHandleMethod.invoke(player);
        Field playerConField = getField(nmsPlayer.getClass(), "playerConnection");
        return playerConField.get(nmsPlayer);
    }

    /**
     * Also @see PacketUtils#sendPacket(Player, Object)
     */
    public static void sendPacket(Player p, Object packet) {
        try {
            Object nmsPlayer = getMethod(p.getClass(), "getHandle").invoke(p);
            Object plrConnection = getField(nmsPlayer.getClass(), "playerConnection").get(nmsPlayer);
            plrConnection.getClass().getMethod("sendPacket", getNMSClass("Packet")).invoke(plrConnection, packet);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Gets the version string for NMS & OBC class paths
     *
     * @return The version string of OBC and NMS packages
     */
    private static String getVersion() {
        if (versionString == null) {
            String name = Bukkit.getServer().getClass().getPackage().getName();
            versionString = name.substring(name.lastIndexOf('.') + 1) + ".";
        }
        return versionString;
    }

    /**
     * Copied from DarkBlade12's ReflectionUtils.
     */
    public enum PackageType {// Just ignore this for now, I'll make it useful
        // someday.
        MINECRAFT_SERVER("net.minecraft.server." + getVersion()), CRAFTBUKKIT("org.bukkit.craftbukkit." + getVersion()),
        CRAFTBUKKIT_BLOCK(CRAFTBUKKIT, "block"), CRAFTBUKKIT_CHUNKIO(
                CRAFTBUKKIT, "chunkio"),
        CRAFTBUKKIT_COMMAND(CRAFTBUKKIT, "command"), CRAFTBUKKIT_CONVERSATIONS(CRAFTBUKKIT, "conversations"),
        CRAFTBUKKIT_ENCHANTMENS(
                CRAFTBUKKIT, "enchantments"),
        CRAFTBUKKIT_ENTITY(CRAFTBUKKIT, "entity"), CRAFTBUKKIT_EVENT(CRAFTBUKKIT, "event"), CRAFTBUKKIT_GENERATOR(
                CRAFTBUKKIT, "generator"),
        CRAFTBUKKIT_HELP(CRAFTBUKKIT, "help"), CRAFTBUKKIT_INVENTORY(CRAFTBUKKIT, "inventory"),
        CRAFTBUKKIT_MAP(CRAFTBUKKIT,
                "map"),
        CRAFTBUKKIT_METADATA(CRAFTBUKKIT, "metadata"), CRAFTBUKKIT_POTION(CRAFTBUKKIT, "potion"),
        CRAFTBUKKIT_PROJECTILES(CRAFTBUKKIT,
                "projectiles"),
        CRAFTBUKKIT_SCHEDULER(CRAFTBUKKIT, "scheduler"), CRAFTBUKKIT_SCOREBOARD(CRAFTBUKKIT, "scoreboard"),
        CRAFTBUKKIT_UPDATER(
                CRAFTBUKKIT, "updater"),
        CRAFTBUKKIT_UTIL(CRAFTBUKKIT, "util");

        private final String path;

        /**
         * Construct a new package type
         *
         * @param path Path of the package
         */
        PackageType(String path) {
            this.path = path;
        }

        /**
         * Construct a new package type
         *
         * @param parent Parent package of the package
         * @param path   Path of the package
         */
        PackageType(PackageType parent, String path) {
            this(parent + "." + path);
        }

        /**
         * Returns the path of this package type
         *
         * @return The path
         */
        public String getPath() {
            return path;
        }

        /**
         * Returns the class with the given name
         *
         * @param className Name of the desired class
         * @return The class with the specified name
         * @throws ClassNotFoundException If the desired class with the specified name
         *                                and package
         *                                cannot be found
         */
        public Class<?> getClass(String className) throws ClassNotFoundException {
            return Class.forName(this + "." + className);
        }

        // Override for convenience
        @Override
        public String toString() {
            return path;
        }
    }
}