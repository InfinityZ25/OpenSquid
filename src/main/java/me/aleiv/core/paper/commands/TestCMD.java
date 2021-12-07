package me.aleiv.core.paper.commands;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Name;
import co.aikar.commands.annotation.Subcommand;
import lombok.NonNull;
import me.aleiv.core.paper.AnimationTools;
import me.aleiv.core.paper.Core;
import me.aleiv.core.paper.utilities.LineVector;

@CommandAlias("test")
@CommandPermission("admin.perm")
public class TestCMD extends BaseCommand {

    private @NonNull Core instance;

    public TestCMD(Core instance) {
        this.instance = instance;
    }

    @Subcommand("particle-distance")
    public void greenLight(CommandSender sender, @Name("distance") Double distance) {
        LineVector.interval = distance;
        sender.sendMessage("Changed the particle distance to " + LineVector.interval);

    }

    @Subcommand("note-block")
    public void test(Player sender, Boolean bool, String note) {
        var block = sender.getTargetBlock(6);
        if (block != null) {
            AnimationTools.setBlockValue(bool, block.getLocation(), note);
        }

    }

    @Subcommand("playsound")
    public void test(Player sender, String str) {
        AnimationTools.playSoundDistance(sender.getLocation(), 10, str, 1f, 1f);

    }

    @Subcommand("anim")
    public void testFade(Player sender, Integer from, Integer until, Integer fadeIn, Integer fadeOut) {
        instance.getGame().getGlobalGame().playAnimation(List.of(sender), from, until, fadeIn, fadeOut);

    }

    @Subcommand("inside")
    public void inside(Player sender, String pos1, String pos2) {
        var specialObjects = AnimationTools.specialObjects;
        var world = Bukkit.getWorld("world");
        var loc1 = AnimationTools.parseLocation(specialObjects.get(pos1), world);
        var loc2 = AnimationTools.parseLocation(specialObjects.get(pos2), world);

        var players = AnimationTools.getPlayersInsideCube(loc1, loc2);
        Core.getInstance().broadcastMessage(players.toString());
    }

}
