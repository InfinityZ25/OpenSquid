package uk.lewdev.entitylib.utils.spectate;

import org.bukkit.entity.Player;

/**
 * @author Lewys Davies (Lew_)
 */
public class Spectate {

    public static void entity(Player player, int entityID) {
        WrapperPlayOutCamera packet = new WrapperPlayOutCamera();
        packet.setEntityID(entityID);
        
        packet.sendPacket(player);
    }
    
    public static void leave(Player player) {
        WrapperPlayOutCamera packet = new WrapperPlayOutCamera();
        packet.setEntityID(player.getEntityId());
        
        packet.sendPacket(player);
    }
}
