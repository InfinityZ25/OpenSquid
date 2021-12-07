package us.jcedeno.packets;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;

import org.bukkit.plugin.Plugin;

import lombok.Getter;
import me.aleiv.core.paper.Core;
import uk.lewdev.entitylib.FakeEntityPlugin;
import us.jcedeno.packets.commands.PacketCommands;

/**
 * A class designed to help with the monitoring of packets.
 * 
 * @author jcedeno
 */
public class PacketToolManager {

    private Plugin plugin;

    private @Getter ConcurrentHashMap<UUID, PacketRecorder> packetRecording = new ConcurrentHashMap<>();

    public PacketToolManager(Core plugin) {
        this.plugin = plugin;

        plugin.getCommandManager().registerCommand(new PacketCommands(this));

        ProtocolLibrary.getProtocolManager().addPacketListener(
                new PacketAdapter(plugin, ListenerPriority.MONITOR, PacketType.Play.Server.ENTITY_EQUIPMENT) {

                    @Override
                    public void onPacketReceiving(PacketEvent event) {
                        final var player = event.getPlayer();
                        var recorder = packetRecording.computeIfAbsent(player.getUniqueId(),
                                (u) -> PacketRecorder.of(false, new HashMap<>()));

                        // If player has packet recording enabled, record packets to ram.
                        if (recorder.getRecord()) {
                            recorder.sequences.computeIfAbsent(System.currentTimeMillis(),
                                    (t) -> (new LinkedList<PacketContainer>())).add(event.getPacket());
                        }
                    }

                    @Override
                    public void onPacketSending(PacketEvent event) {

                        final var player = event.getPlayer();
                        var recorder = packetRecording.computeIfAbsent(player.getUniqueId(),
                                (u) -> PacketRecorder.ofDefaults());

                        // If player has packet recording enabled, record packets to ram.
                        if (recorder.getRecord()) {
                            recorder.sequences.computeIfAbsent(System.currentTimeMillis(),
                                    (t) -> (new LinkedList<PacketContainer>())).add(event.getPacket());
                        }

                    }

                });
    }

}
