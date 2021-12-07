package us.jcedeno.packets.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.bukkit.entity.Player;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Subcommand;
import me.aleiv.core.paper.Core;
import us.jcedeno.packets.PacketRecorder;
import us.jcedeno.packets.PacketToolManager;

@CommandPermission("admin.packets.cmd")
@CommandAlias("packets")
public class PacketCommands extends BaseCommand {
    private PacketToolManager manager;
    private Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public PacketCommands(PacketToolManager manager) {
        this.manager = manager;
    }

    // A function that saves the packets of a current file to a flat file
    @Subcommand("save")
    public void saveCommand(Player sender) {
        manager.getPacketRecording().get(sender.getUniqueId());

    }

    @Subcommand("record")
    public void toggleRecord(Player sender) {
        if (manager.getPacketRecording().containsKey(sender.getUniqueId())) {
            var newSetting = manager.getPacketRecording().get(sender.getUniqueId()).toggleRecord();
            sender.sendMessage(
                    Core.getMiniMessage().parse("<green>Changed packet recording settings to<white> " + newSetting));
        } else {
            sender.sendMessage(
                    Core.getMiniMessage().parse("<red>You do not have a recording profile. \n<green>Creating one..."));
            manager.getPacketRecording().put(sender.getUniqueId(), PacketRecorder.of(false, new HashMap<>()));

        }
    }

    /**
     * A function that shows the lastIndex - n packet sequence of a player.
     */
    @Subcommand("last")
    public void lastCommand(Player sender, @Default("0") Integer n) {
        if (manager.getPacketRecording().containsKey(sender.getUniqueId())) {
            var last = new ArrayList<>(manager.getPacketRecording().get(sender.getUniqueId()).getSequences().values());
            Collections.reverse(last);
            sender.sendMessage(Core.getMiniMessage().parse("<green>Last " + n + " packets:"));
            last.get(n).forEach(packet -> sender.sendMessage(gson.toJson(packet)));
        } else {
            sender.sendMessage(Core.getMiniMessage().parse("<red>You do not have a recording profile."));
        }
    }
}
