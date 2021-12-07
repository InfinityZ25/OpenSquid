package me.aleiv.core.paper.commands;

import org.bukkit.command.CommandSender;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Subcommand;
import lombok.NonNull;
import me.aleiv.core.paper.Core;
import net.md_5.bungee.api.ChatColor;

@CommandAlias("glass")
@CommandPermission("admin.perm")
public class GlassCMD extends BaseCommand {

    private @NonNull Core instance;

    public GlassCMD(Core instance){
        this.instance = instance;
    }

    @Subcommand("break-all")
    public void glass(CommandSender sender){
        var tools = instance.getGame().getGlassGame();
        tools.breakAll();
        sender.sendMessage(ChatColor.DARK_AQUA + "BREAK ALL GLASS");
    }

}