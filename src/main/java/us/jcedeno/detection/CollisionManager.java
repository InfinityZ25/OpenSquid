package us.jcedeno.detection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;

import lombok.Getter;
import me.aleiv.core.paper.Core;
import us.jcedeno.detection.commands.PolygonCommand;
import us.jcedeno.detection.listeners.PolygonListener;
import us.jcedeno.detection.objects.Polygon;
import us.jcedeno.detection.task.CheckCollisionsTask;

public class CollisionManager {
    private Core instance;
    private @Getter List<Polygon> polygonList;
    private @Getter Map<UUID, Polygon> playerPolygonMap;

    public CollisionManager(Core instance) {
        this.instance = instance;
        this.playerPolygonMap = new HashMap<>();
        this.polygonList = new ArrayList<>();
        this.instance.getCommandManager().registerCommand(new PolygonCommand(this));
        Bukkit.getScheduler().runTaskTimerAsynchronously(instance, new CheckCollisionsTask(this), 0L, 1L);
        Bukkit.getPluginManager().registerEvents(new PolygonListener(), instance);

    }

}
