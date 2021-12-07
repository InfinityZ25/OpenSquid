package me.aleiv.core.paper.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Flags;
import co.aikar.commands.annotation.Name;
import co.aikar.commands.annotation.Optional;
import co.aikar.commands.annotation.Subcommand;
import lombok.NonNull;
import me.aleiv.core.paper.Core;
import me.aleiv.core.paper.Game.Role;
import net.md_5.bungee.api.ChatColor;

@CommandAlias("utils")
@CommandPermission("admin.perm")
public class UtilsCMD extends BaseCommand {

    private @NonNull Core instance;

    public UtilsCMD(Core instance){
        this.instance = instance;
    }

    @Subcommand("make-all-sleep")
    public void makeAllSleep(CommandSender sender){
        sender.sendMessage(ChatColor.DARK_AQUA + "Make all sleep");
        var game = instance.getGame();
        var tools = game.getGlobalGame();
        var players = Bukkit.getOnlinePlayers().stream().filter(player -> game.isPlayer(player)).map(player -> (Player) player).toList();
        tools.makeAllSleep(players);
    }

    @Subcommand("sleep")
    public void shoot(Player sender, @Name("target") @Optional @Flags("other") Player target) {
        var loc = sender.getTargetBlock(5);
        if(loc != null){
            sender.sendMessage(ChatColor.DARK_AQUA + "Sleep " + target.getName() + " sleep");
            var tools = instance.getGame().getGlobalGame();
            tools.sleep(target, loc.getLocation());
        }

    }

    @Subcommand("set-spawn")
    public void setSpawn(Player sender){
        sender.sendMessage(ChatColor.DARK_AQUA + "Set Spawn");
        var tools = instance.getGame();
        tools.setCity(sender.getLocation());
    }

    @Subcommand("gas")
    public void sendGas(CommandSender sender){
        var tools = instance.getGame();
        tools.getGlobalGame().applyGas(Bukkit.getOnlinePlayers().stream().map(p -> (Player) p).toList());
    }

    @Subcommand("countdown")
    public void sendCount(CommandSender sender){
        var tools = instance.getGame();
        tools.getGlobalGame().sendCountDown(Bukkit.getOnlinePlayers().stream().map(p -> (Player) p).toList());
    }

    @Subcommand("play-start")
    public void start(CommandSender sender){
        var tools = instance.getGame();
        tools.getGlobalGame().playSquidGameStart();
    }

    @Subcommand("role-global")
    public void roleGlobal(CommandSender sender, Role role) {
        var game = instance.getGame();
        var roles = game.getRoles();
        
        Bukkit.getOnlinePlayers().forEach(player ->{
            var uuid = player.getUniqueId().toString();
            roles.put(uuid, role);
        });
        
        sender.sendMessage(ChatColor.DARK_AQUA + "Global role set to " + role.toString().toLowerCase());
    }

    @Subcommand("title-black")
    public void title(CommandSender sender, Integer fadeIn, Integer fadeOut) {
        Bukkit.getOnlinePlayers().forEach(player ->{
            instance.showTitle(player, Character.toString('\u3400'), "", fadeIn, 20, fadeOut);
        });

    }

    


}