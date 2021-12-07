package us.jcedeno.packets;

import java.util.HashMap;
import java.util.Map;
import java.util.Queue;

import com.comphenix.protocol.events.PacketContainer;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * A data object that represent the recording setting of an user and all the
 * packet sequences that the user has recorded. TODO Improve efficiency.
 * 
 * @author jcedeno
 */
@AllArgsConstructor(staticName = "of")
@Data
public class PacketRecorder {
    Boolean record = false;
    Map<Long, Queue<PacketContainer>> sequences = new HashMap<>();

    /**
     * @return whether the user is recording or not.
     */
    public boolean toggleRecord() {
        this.record = !this.record;
        return this.record;
    }

    /**
     * @return A new instance of Packet Recorder with the same settings.
     */
    public static PacketRecorder ofDefaults(){
        return new PacketRecorder(false, new HashMap<>());
    }
}
