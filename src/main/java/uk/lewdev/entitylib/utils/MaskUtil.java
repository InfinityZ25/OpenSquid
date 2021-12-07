package uk.lewdev.entitylib.utils;

public class MaskUtil {

    public static byte setBit(byte bytee, int index, boolean on) {
        if (index < 0 || index > 7) {
            throw new IllegalArgumentException("Index must be in the inclusive range 0-7. Bytes only contain 8 bits");
        }

        if (on) {
            return (byte) (bytee | (1 << index));
        } else {
            return (byte) (bytee & ~(1 << index));
        }
    }

    public static boolean getBit(byte bytee, int index) {
        if (index < 0 || index > 7) {
            throw new IllegalArgumentException("Index must be in the inclusive range 0-7. Bytes only contain 8 bits");
        }

        return (bytee & (1 << index)) != 0;
    }
}
