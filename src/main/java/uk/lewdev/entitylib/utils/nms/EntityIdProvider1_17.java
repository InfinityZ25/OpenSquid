package uk.lewdev.entitylib.utils.nms;

import uk.lewdev.entitylib.utils.EntityIdProvider;
import uk.lewdev.entitylib.utils.ReflectionUtil;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.atomic.AtomicInteger;

public class EntityIdProvider1_17 implements EntityIdProvider {

    private static Field entityCountField;
    private static Method getMethod;
    private static Method incrementAndGetMethod;

    static {
        try {
            entityCountField = ReflectionUtil.getClass("net.minecraft.world.entity.Entity").getDeclaredField("b");
            entityCountField.trySetAccessible();

            Object count = entityCountField.get(null);

            getMethod = count.getClass().getMethod("get");
            incrementAndGetMethod = count.getClass().getMethod("incrementAndGet");
        } catch (NoSuchFieldException | NoSuchMethodException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int nextAndIncrement() {
        if (entityCountField == null) {
            return -1;
        }

        if (incrementAndGetMethod == null) {
            return -1;
        }

        try {
            Object idCounter = incrementAndGetMethod.invoke(entityCountField.get(null));

            if (idCounter instanceof AtomicInteger) { // 1.15
                return ((AtomicInteger) idCounter).get();
            } else {
                return (int) idCounter;
            }
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }

        return -1;
    }

    @Override
    public int getCurrentId() {
        if (entityCountField == null) {
            return -1;
        }

        if (incrementAndGetMethod == null) {
            return -1;
        }

        try {
            Object idCounter = getMethod.invoke(entityCountField.get(null));

            if (idCounter instanceof AtomicInteger) { // 1.15
                return ((AtomicInteger) idCounter).get();
            } else {
                return (int) idCounter;
            }
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }

        return -1;
    }
}
