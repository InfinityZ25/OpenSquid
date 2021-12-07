package me.aleiv.core.paper.Games;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;

import lombok.Getter;
import me.aleiv.core.paper.AnimationTools;
import me.aleiv.core.paper.Core;

public class ChairGame {
    Core instance;

    @Getter
    boolean music = false;

    @Getter
    List<Location> discoNotes = new ArrayList<>();

    public ChairGame(Core instance) {
        this.instance = instance;

    }

    public void turnMusic(String music, Boolean bool) {

        if (discoNotes.isEmpty()) {
            discoNotes = AnimationTools.findLocations("DISCO_NOTES");
        }
        this.music = bool;

        var stereo = discoNotes.get(0);
        if (bool) {

            Bukkit.getOnlinePlayers().forEach(player -> {
                var loc = player.getLocation();
                player.playSound(loc, music, 1, 1);
            });
        } else {

            Bukkit.getOnlinePlayers().forEach(player -> {
                player.stopSound(music);

            });
            AnimationTools.playSoundDistance(stereo, 50, "squid:sfx.music_stop", 1f, 1f);
        }

    }

}
