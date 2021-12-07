package uk.lewdev.entitylib.utils;

import com.comphenix.protocol.wrappers.Vector3F;
import org.bukkit.util.EulerAngle;

public class AngleUtil {

    public static byte fromDegrees(float degrees) {
        return (byte) (degrees * 256.0F / 360.0F);
    }

    public static float fromRadians(double degressAngle) {
        return (float) (degressAngle * 180 / Math.PI);
    }

    public static Vector3F fromRadians(EulerAngle angle) {
        return new Vector3F(
          fromRadians(angle.getX()),
          fromRadians(angle.getY()),
          fromRadians(angle.getZ()));
    }
}