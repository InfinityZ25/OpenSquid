package me.aleiv.core.paper.commands;

import java.util.Arrays;

import com.google.common.collect.ImmutableList;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Subcommand;
import lombok.NonNull;
import me.aleiv.core.paper.Core;
import me.aleiv.core.paper.Games.Elevators.ElevatorType;
import net.md_5.bungee.api.ChatColor;

@CommandAlias("elevators")
@CommandPermission("admin.perm")
public class ElevatorsCMD extends BaseCommand {

    private @NonNull Core instance;

    public ElevatorsCMD(Core instance){
        this.instance = instance;

        var elevatorTypes = Arrays.asList(ElevatorType.values()).stream().map(e -> e.toString()).toList();
        var manager = instance.getCommandManager();
        manager.getCommandCompletions().registerAsyncCompletion("elevators", c -> {
            return elevatorTypes;
        });

        manager.getCommandCompletions().registerAsyncCompletion("bool", c -> {
            return ImmutableList.of("true", "false");
        });
    }

    @Subcommand("door")
    @CommandAlias("door")
    @CommandCompletion("@elevators @bool")
    public void dollElevator(CommandSender sender, ElevatorType elevatorType, Boolean bool){
        var tools = instance.getGame().getElevators();
        tools.elevatorDoor(elevatorType, bool);
        sender.sendMessage(ChatColor.DARK_AQUA + "Elevator " + elevatorType + " elevator " + bool);
    }

    @Subcommand("travel")
    @CommandAlias("travel")
    @CommandCompletion("@elevators @bool")
    public void travel(Player sender, ElevatorType elevator, Boolean bool){
        var tools = instance.getGame().getElevators();
        tools.elevatorTravel(sender, elevator, bool);
        sender.sendMessage(ChatColor.DARK_AQUA + "Elevator travel " + elevator + " to " + elevator);
    }

}
