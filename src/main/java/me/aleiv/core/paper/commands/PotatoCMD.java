package me.aleiv.core.paper.commands;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Subcommand;
import lombok.NonNull;
import me.aleiv.core.paper.Core;

@CommandAlias("potato")
@CommandPermission("admin.perm")
public class PotatoCMD extends BaseCommand {

    private @NonNull Core instance;

    public PotatoCMD(Core instance){
        this.instance = instance;
    }

    @Subcommand("explode")
    public void explode(CommandSender sender){
        Bukkit.getOnlinePlayers().forEach(player ->{
            var loc = player.getLocation();
           
            player.playSound(loc, "squid:sfx.potato_explode", 11, 1);
            var inv = player.getInventory();

            if(inv.contains(Material.RABBIT_FOOT)){
                Bukkit.getScheduler().runTaskLater(instance, task->{
                    inv.clear();
                    player.getLocation().createExplosion(2, false, false);
                }, 20*10);
            }
        });
    }

}
