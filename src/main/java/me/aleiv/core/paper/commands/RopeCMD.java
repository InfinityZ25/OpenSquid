package me.aleiv.core.paper.commands;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Subcommand;
import lombok.NonNull;
import me.aleiv.core.paper.AnimationTools;
import me.aleiv.core.paper.Core;
import net.md_5.bungee.api.ChatColor;

@CommandAlias("rope")
@CommandPermission("admin.perm")
public class RopeCMD extends BaseCommand {

    private @NonNull Core instance;

    public RopeCMD(Core instance){
        this.instance = instance;
    }

    @Subcommand("guillotine")
    public void guillotine(CommandSender sender, Boolean bool){
        var tools = instance.getGame().getRopeGame();
        var task = tools.moveGuillotine(bool);
        sender.sendMessage(ChatColor.DARK_AQUA + "Guillotine move " + bool);

        if(!bool){
            task.thenAccept(action ->{
                Bukkit.getScheduler().runTask(instance, tk ->{
                    var ropeEntities1 = tools.getLeftRope();
                    ropeEntities1.forEach(stand -> {
                    AnimationTools.setStandModel(stand, Material.BRICK, 38);

                    });

                    var ropeEntities2 = tools.getRightRope();
                    ropeEntities2.forEach(stand -> {
                    AnimationTools.setStandModel(stand, Material.BRICK, 38);
                    });

                });
            });
        }
    }

    @Subcommand("boolMode")
    public void boolMode(CommandSender sender, Boolean bool){
        var tools = instance.getGame().getRopeGame();
        tools.setBoolMode(bool);
        sender.sendMessage(ChatColor.DARK_AQUA + "BoolMode move " + bool);
        
    }

    @Subcommand("gate")
    public void gate(CommandSender sender, Boolean bool){
        sender.sendMessage(ChatColor.DARK_AQUA + "Rope gate " + bool);
        var tools = instance.getGame().getRopeGame();
        tools.ropeGate(bool);
    }

    @Subcommand("ingame")
    public void play(CommandSender sender, Boolean bool){
        sender.sendMessage(ChatColor.DARK_AQUA + "Rope ingame " + bool);
        var tools = instance.getGame().getRopeGame();
        tools.setInGame(bool);
    }

    @Subcommand("bossbar")
    public void bossbar(CommandSender sender, Boolean bool){
        sender.sendMessage(ChatColor.DARK_AQUA + "Bossbar rope " + bool);
        var tools = instance.getGame().getRopeGame();
        tools.enableRope(bool);

        if(tools.getRightRope().isEmpty() || tools.getLeftRope().isEmpty()){
            tools.initRope();
        }
    }

    @Subcommand("points")
    public void points(CommandSender sender, Integer i){
        sender.sendMessage(ChatColor.DARK_AQUA + "Bossbar points rope " + i);
        var tools = instance.getGame().getRopeGame();
        tools.setPoints(i);
        tools.updateBossBar();
    }

    @Subcommand("multiplier")
    public void multiplier(CommandSender sender, Integer i){
        sender.sendMessage(ChatColor.DARK_AQUA + "Bossbar multiplier points rope " + i);
        var tools = instance.getGame().getRopeGame();
        tools.setMultiplier(i);
    }

    @Subcommand("right-elevator")
    public void rightElevator(CommandSender sender, Boolean bool){
        sender.sendMessage(ChatColor.DARK_AQUA + "Right elevator " + bool);
        var tools = instance.getGame().getRopeGame();
        tools.rightElevator(bool);
    }

    @Subcommand("left-elevator")
    public void leftElevator(CommandSender sender, Boolean bool){
        sender.sendMessage(ChatColor.DARK_AQUA + "Left elevator " + bool);
        var tools = instance.getGame().getRopeGame();
        tools.leftElevator(bool);
    }

}
