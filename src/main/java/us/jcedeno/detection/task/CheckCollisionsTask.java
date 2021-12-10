package us.jcedeno.detection.task;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import us.jcedeno.detection.CollisionManager;
import us.jcedeno.detection.events.PlayerEnterPolygonEvent;
import us.jcedeno.detection.events.PlayerExitPloygonEvent;
import us.jcedeno.detection.objects.Polygon;

public class CheckCollisionsTask implements Runnable {
    private CollisionManager collisionManager;

    public CheckCollisionsTask(CollisionManager collisionManager) {
        this.collisionManager = collisionManager;
    }

    @Override
    public void run() {
        // Check if any of the players online is inside of any of the polygons.
        for (Player player : Bukkit.getOnlinePlayers()) {
            // If player is contained on the map already, check if he is still inside of the
            // polygon.
            var playerPolygon = this.collisionManager.getPlayerPolygonMap().get(player.getUniqueId());
            // If player polygon is not null, then player might be entering another polygon
            // or leaving one.
            if (playerPolygon != null) {
                boolean matched = false;
                for (Polygon polygon : this.collisionManager.getPolygonList()) {
                    if (polygon.isInside(player.getLocation())) {
                        if (!playerPolygon.equals(polygon)) {
                            // Player is leaving the old polygon
                            callExitedPolygon(player, playerPolygon);
                        } else {
                            matched = true;
                            break;
                        }
                        // Player is entering a new polygon.
                        callEnteredPolygon(player, polygon);
                        this.collisionManager.getPlayerPolygonMap().put(player.getUniqueId(), polygon);
                        matched = true;
                        break;
                    }
                }
                if (!matched) {
                    // Player is no longer inside any polygon.
                    callExitedPolygon(player, playerPolygon);
                    this.collisionManager.getPlayerPolygonMap().remove(player.getUniqueId());
                }
            } else {
                // If player is not already inside a polygon, then they are only capable of
                // entering a new polygon.
                for (Polygon polygon : this.collisionManager.getPolygonList()) {
                    if (polygon.isInside(player.getLocation())) {
                        callEnteredPolygon(player, polygon);
                        this.collisionManager.getPlayerPolygonMap().put(player.getUniqueId(), polygon);
                        break;
                    }
                }
            }
        }
    }

    private void callExitedPolygon(Player player, Polygon polygon) {
        Bukkit.getPluginManager().callEvent(new PlayerExitPloygonEvent(player, polygon, !Bukkit.isPrimaryThread()));
    }

    private void callEnteredPolygon(Player player, Polygon polygon) {
        Bukkit.getPluginManager().callEvent(new PlayerEnterPolygonEvent(player, polygon, !Bukkit.isPrimaryThread()));
    }

}
