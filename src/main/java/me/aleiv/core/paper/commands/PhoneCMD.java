package me.aleiv.core.paper.commands;

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
import net.md_5.bungee.api.ChatColor;

@CommandAlias("phone")
@CommandPermission("admin.perm")
public class PhoneCMD extends BaseCommand {

    private @NonNull Core instance;

    public PhoneCMD(Core instance){
        this.instance = instance;
    }

    @Subcommand("target")
    public void target(CommandSender sender, @Name("target") @Optional @Flags("other") Player target){
        //var tools = instance.getGame().getPhoneGame();
        
        sender.sendMessage(ChatColor.DARK_AQUA + "Target " + target);
    }
}
