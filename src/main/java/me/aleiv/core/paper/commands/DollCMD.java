package me.aleiv.core.paper.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Flags;
import co.aikar.commands.annotation.Name;
import co.aikar.commands.annotation.Optional;
import co.aikar.commands.annotation.Subcommand;
import lombok.NonNull;
import me.aleiv.core.paper.AnimationTools;
import me.aleiv.core.paper.Core;
import net.md_5.bungee.api.ChatColor;

@CommandAlias("doll")
@CommandPermission("admin.perm")
public class DollCMD extends BaseCommand {

    private @NonNull Core instance;

    public DollCMD(Core instance){
        this.instance = instance;
    }

    @Subcommand("greenLight")
    @CommandAlias("greenLight")
    public void greenLight(Player sender, @Name("target") @Optional @Flags("other") Player target) {
        var game = instance.getGame();
        game.getDollGame().getGreenLightPanel().open(sender);

    }

    @Subcommand("runLight")
    @CommandAlias("runLight")
    public void runLight(Player sender, Boolean bool) {
        var game = instance.getGame();
        game.getDollGame().runLight(bool);

    }

    
    @Subcommand("shoot")
    @CommandAlias("shoot")
    @CommandCompletion("@players")
    public void shoot(Player sender, @Name("target") @Optional @Flags("other") Player target) {
        AnimationTools.shootLocation(target.getLocation());

    }

    @Subcommand("door")
    public void dollDoor(CommandSender sender, Integer i, Boolean bool){
        sender.sendMessage(ChatColor.DARK_AQUA + "Doll door " + i + " " + bool);
        var tools = instance.getGame().getDollGame();
        switch (i) {
            case 1: tools.dollDoor1(bool); break;
            case 2: tools.dollDoor2(bool); break;
            case 3: tools.dollDoor3(bool); break;
        
            default:
                break;
        }
    }

    @Subcommand("line")
    public void makeAllSleep(CommandSender sender, Integer i, Boolean bool){
        sender.sendMessage(ChatColor.DARK_AQUA + "Doll line " + i + " " + bool);
        var tools = instance.getGame().getDollGame();
        switch (i) {
            case 1: tools.dollLine1(bool); break;
            case 2: tools.dollLine2(bool); break;
        
            default:
                break;
        }
    }

    @Subcommand("body")
    public void doll(CommandSender sender, Boolean bool){
        sender.sendMessage(ChatColor.DARK_AQUA + "Doll " + bool);
        var tools = instance.getGame().getDollGame();
        tools.dollRotate(bool);
    }

    @Subcommand("head")
    public void dollHead(CommandSender sender, Boolean bool){
        sender.sendMessage(ChatColor.DARK_AQUA + "Doll head" + bool);
        var tools = instance.getGame().getDollGame();
        tools.dollHead(bool);
    }
}
