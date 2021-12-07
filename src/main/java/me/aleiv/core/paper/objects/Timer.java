package me.aleiv.core.paper.objects;

import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;

import lombok.Getter;
import lombok.Setter;
import me.aleiv.core.paper.AnimationTools;
import me.aleiv.core.paper.Core;
import me.aleiv.core.paper.Game.TimerType;

public class Timer {

    Core instance;

    int startTime;
    int seconds;

    @Getter
    BossBar bossBar;

    String time = "";

    @Setter
    @Getter
    boolean isActive = false;

    int currentClock = 0;

    @Getter HashMap<TimerType, List<Location>> timerLocations = new HashMap<>();

    public Timer(Core instance, int currentTime) {
        this.instance = instance;
        this.seconds = 0;
        this.startTime = (int) currentTime;
        this.bossBar = Bukkit.createBossBar(new NamespacedKey(instance, "TIMER"), "", BarColor.WHITE, BarStyle.SOLID);
        bossBar.setVisible(false);

    }

    private static String timeConvert(int t) {
        int hours = t / 3600;

        int minutes = (t % 3600) / 60;
        int seconds = t % 60;

        return (hours > 0 ? String.format("%02d:%02d:%02d", hours, minutes, seconds)
                : String.format("%02d:%02d", minutes, seconds));
    }

    public int getTime(int currentTime) {
        return (startTime + seconds) - currentTime;
    }

    public void setPreStart(int time) {
        this.time = timeConvert(time);
        this.getBossBar().setVisible(true);
        bossBar.setTitle(this.time);

        if(timerLocations.isEmpty()){
            registerTimers();
        }
        refreshTimer(this.time);
    }

    public void refreshTime(int currentTime) {
        var time = (startTime + seconds) - currentTime;

        if (time < 0) {
            this.time = "00:00";

        } else {
            this.time = timeConvert((int) time);
            bossBar.setTitle(this.time);
            Bukkit.getOnlinePlayers().forEach(player -> {
                player.playSound(player.getLocation(), "squid:sfx.tic", 1, 1);
            });

        }

        if (time < -5) {
            delete();
            setActive(false);
        }

        refreshTimer(this.time);

    }

    public void delete() {
        bossBar.setVisible(false);

    }

    public void start(int seconds, int startTime) {
        this.time = timeConvert(seconds);
        this.seconds = seconds;
        this.startTime = (int) instance.getGame().getGameTime();
        this.isActive = true;
        bossBar.setVisible(true);
    }

    public void refreshTimer(String str){
        
        var timerType = instance.getGame().getTimerType();
        switch (timerType) {
            case RED_GREEN ->{  AnimationTools.setTimerValue(timerLocations.get(TimerType.RED_GREEN), str); }
            case GLASS ->{ AnimationTools.setTimerValue(timerLocations.get(TimerType.GLASS), str); }
            case COOKIE ->{ AnimationTools.setTimerValue(timerLocations.get(TimerType.COOKIE), str); }
            case HIDE_SEEK ->{ AnimationTools.setTimerValue(timerLocations.get(TimerType.HIDE_SEEK), str); }
            case POTATO ->{ AnimationTools.setTimerValue(timerLocations.get(TimerType.POTATO), str); }
        }

    }

    private void registerTimers() {

        var world = Bukkit.getWorld("world");
        var specialObjects = AnimationTools.specialObjects;
        var b1 = AnimationTools.parseLocation(specialObjects.get("TIMER_RED_GREEN_1"), world);
        var b2 = AnimationTools.parseLocation(specialObjects.get("TIMER_RED_GREEN_2"), world);
        var b3 = AnimationTools.parseLocation(specialObjects.get("TIMER_RED_GREEN_3"), world);
        var b4 = AnimationTools.parseLocation(specialObjects.get("TIMER_RED_GREEN_4"), world);
        var b5 = AnimationTools.parseLocation(specialObjects.get("TIMER_RED_GREEN_5"), world);
        List<Location> locations = List.of(b1, b2, b3, b4, b5);

        timerLocations.put(TimerType.RED_GREEN, locations);

        b1 = AnimationTools.parseLocation(specialObjects.get("TIMER_GLASS_1"), world);
        b2 = AnimationTools.parseLocation(specialObjects.get("TIMER_GLASS_2"), world);
        b3 = AnimationTools.parseLocation(specialObjects.get("TIMER_GLASS_3"), world);
        b4 = AnimationTools.parseLocation(specialObjects.get("TIMER_GLASS_4"), world);
        b5 = AnimationTools.parseLocation(specialObjects.get("TIMER_GLASS_5"), world);
        locations = List.of(b1, b2, b3, b4, b5);

        timerLocations.put(TimerType.GLASS, locations);

        b1 = AnimationTools.parseLocation(specialObjects.get("TIMER_HIDE_SEEK_1"), world);
        b2 = AnimationTools.parseLocation(specialObjects.get("TIMER_HIDE_SEEK_2"), world);
        b3 = AnimationTools.parseLocation(specialObjects.get("TIMER_HIDE_SEEK_3"), world);
        b4 = AnimationTools.parseLocation(specialObjects.get("TIMER_HIDE_SEEK_4"), world);
        b5 = AnimationTools.parseLocation(specialObjects.get("TIMER_HIDE_SEEK_5"), world);
        locations = List.of(b1, b2, b3, b4, b5);

        timerLocations.put(TimerType.HIDE_SEEK, locations);

        b1 = AnimationTools.parseLocation(specialObjects.get("TIMER_COOKIE_1"), world);
        b2 = AnimationTools.parseLocation(specialObjects.get("TIMER_COOKIE_2"), world);
        b3 = AnimationTools.parseLocation(specialObjects.get("TIMER_COOKIE_3"), world);
        b4 = AnimationTools.parseLocation(specialObjects.get("TIMER_COOKIE_4"), world);
        b5 = AnimationTools.parseLocation(specialObjects.get("TIMER_COOKIE_5"), world);
        locations = List.of(b1, b2, b3, b4, b5);

        timerLocations.put(TimerType.COOKIE, locations);

        b1 = AnimationTools.parseLocation(specialObjects.get("TIMER_POTATO_1"), world);
        b2 = AnimationTools.parseLocation(specialObjects.get("TIMER_POTATO_2"), world);
        b3 = AnimationTools.parseLocation(specialObjects.get("TIMER_POTATO_3"), world);
        b4 = AnimationTools.parseLocation(specialObjects.get("TIMER_POTATO_4"), world);
        b5 = AnimationTools.parseLocation(specialObjects.get("TIMER_POTATO_5"), world);
        locations = List.of(b1, b2, b3, b4, b5);

        timerLocations.put(TimerType.POTATO, locations);
    }

}
