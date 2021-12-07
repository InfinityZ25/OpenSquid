package uk.lewdev.entitylib.utils.nms;

import uk.lewdev.entitylib.utils.EntityIdProvider;
import uk.lewdev.entitylib.utils.ReflectionUtil;

import java.lang.reflect.Field;

public class EntityIdProvider1_9 implements EntityIdProvider {

    private static Field entityCountField;

    static {
        try {
            entityCountField = ReflectionUtil.getNMSClass("Entity").getDeclaredField("entityCount");
            entityCountField.trySetAccessible();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int nextAndIncrement() {
        if (entityCountField == null) {
            return -1;
        }

        try {
            int nextId = this.getCurrentId() + 1;

            entityCountField.set(null, nextId);

            return nextId;
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        return -1;
    }

    @Override
    public int getCurrentId() {
        if (entityCountField == null) {
            return -1;
        }

        try {
            return entityCountField.getInt(null);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        return -1;
    }
}
