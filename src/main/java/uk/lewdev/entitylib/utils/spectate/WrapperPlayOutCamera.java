package uk.lewdev.entitylib.utils.spectate;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;

import uk.lewdev.entitylib.entity.protocol.wrappers.AbstractPacket;

public class WrapperPlayOutCamera extends AbstractPacket {
    public static final PacketType TYPE = PacketType.Play.Server.CAMERA;

    public WrapperPlayOutCamera() {
        super(new PacketContainer(TYPE), TYPE);
        handle.getModifier().writeDefaults();
    }

    public WrapperPlayOutCamera(PacketContainer packet) {
        super(packet, TYPE);
    }

    public int getEntityID() {
        return handle.getIntegers().read(0);
    }

    public void setEntityID(int value) {
        handle.getIntegers().write(0, value);
    }
}