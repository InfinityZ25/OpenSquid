package me.aleiv.core.paper.commands;

import org.bukkit.command.CommandSender;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Subcommand;
import lombok.NonNull;
import me.aleiv.core.paper.Core;
import net.md_5.bungee.api.ChatColor;

@CommandAlias("main")
@CommandPermission("admin.perm")
public class MainCMD extends BaseCommand {

    private @NonNull Core instance;

    public MainCMD(Core instance){
        this.instance = instance;
    }

    @Subcommand("lights")
    public void game(CommandSender sender, Boolean bool){
        sender.sendMessage(ChatColor.DARK_AQUA + "Lights " + bool);

        var tools = instance.getGame().getMainRoom();
        tools.lights(bool);
    }

    @Subcommand("pasiveLights")
    public void pasiveLights(CommandSender sender, Boolean bool){
        sender.sendMessage(ChatColor.DARK_AQUA + "Pasive Lights " + bool);

        var tools = instance.getGame().getMainRoom();
        tools.pasiveLights(bool);
    }

    @Subcommand("elevator")
    public void elevator(CommandSender sender, Boolean bool){
        sender.sendMessage(ChatColor.DARK_AQUA + "Main elevator " + bool);
        var tools = instance.getGame().getMainRoom();
        tools.mainElevator(bool);
    }

    @Subcommand("submarine-left")
    public void leftDoor(CommandSender sender, Boolean bool){
        sender.sendMessage(ChatColor.DARK_AQUA + "Main left door submarine " + bool);
        var tools = instance.getGame().getMainRoom();
        tools.mainLeftDoor(bool);
    }

    @Subcommand("submarine-right")
    public void rightDoor(CommandSender sender, Boolean bool){
        sender.sendMessage(ChatColor.DARK_AQUA + "Main right door submarine " + bool);
        var tools = instance.getGame().getMainRoom();
        tools.mainRightDoor(bool);
    }

    @Subcommand("tube")
    public void tube(CommandSender sender, Boolean bool){
        var tools = instance.getGame().getMainRoom();
        tools.moveTube(bool);
        sender.sendMessage(ChatColor.DARK_AQUA + "Tube move " + bool);
    }

    @Subcommand("screen-turn")
    public void screen(CommandSender sender, Boolean bool){
        var tools = instance.getGame().getMainRoom();
        tools.turnScreen(bool);
        sender.sendMessage(ChatColor.DARK_AQUA + "Screen turn " + bool);
    }

    @Subcommand("refresh-prize")
    public void refreshPrize(CommandSender sender, Integer newPrize, Integer delay, Integer value){
        var tools = instance.getGame().getMainRoom();
        tools.refreshPrize(newPrize, delay, value);
        sender.sendMessage(ChatColor.DARK_AQUA + "Refreshed prize " + newPrize + " " + delay);
    }

    @Subcommand("refresh-players")
    public void refreshPlayers(CommandSender sender, Integer newPrize, Integer delay, Integer value){
        var tools = instance.getGame().getMainRoom();
        tools.refreshPlayers(newPrize, delay, value);
        sender.sendMessage(ChatColor.DARK_AQUA + "Refreshed players " + newPrize + " " + delay);
    }




}
