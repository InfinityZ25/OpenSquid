package me.aleiv.core.paper.commands;

import org.bukkit.command.CommandSender;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Subcommand;
import lombok.NonNull;
import me.aleiv.core.paper.Core;
import net.md_5.bungee.api.ChatColor;

@CommandAlias("chair")
@CommandPermission("admin.perm")
public class ChairCMD extends BaseCommand {

    private @NonNull Core instance;

    public ChairCMD(Core instance){
        this.instance = instance;
    }

    @Subcommand("music")
    public void door(CommandSender sender, String music, Boolean bool){
        var tools = instance.getGame().getChairGame();
        tools.turnMusic(music, bool);
        sender.sendMessage(ChatColor.DARK_AQUA + "MUSIC TURN " + bool);
    }
}
