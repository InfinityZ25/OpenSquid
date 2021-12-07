package me.aleiv.core.paper.commands;

import org.bukkit.command.CommandSender;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Subcommand;
import lombok.NonNull;
import me.aleiv.core.paper.Core;
import net.md_5.bungee.api.ChatColor;

@CommandAlias("hideSeek")
@CommandPermission("admin.perm")
public class HideSeekCMD extends BaseCommand {

    private @NonNull Core instance;

    public HideSeekCMD(Core instance){
        this.instance = instance;
    }

    @Subcommand("door")
    public void door(CommandSender sender, Boolean bool){
        var tools = instance.getGame().getHideSeekGame();
        tools.door(bool);
        sender.sendMessage(ChatColor.DARK_AQUA + "Hide Seek door " + bool);
    }

}