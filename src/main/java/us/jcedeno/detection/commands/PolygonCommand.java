package us.jcedeno.detection.commands;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Subcommand;
import me.aleiv.core.paper.Core;
import us.jcedeno.detection.CollisionManager;
import us.jcedeno.detection.objects.Polygon;

@CommandAlias("polygon")
public class PolygonCommand extends BaseCommand {
    private CollisionManager collisionManager;
    private Map<UUID, Polygon> polygonsInProgress;

    public PolygonCommand(CollisionManager collisionManager) {
        this.collisionManager = collisionManager;
        this.polygonsInProgress = new HashMap<>();
    }

    @Default
    public void createPolygon(Player player) {

        var id = player.getUniqueId();
        // Get the block the player is looking at
        var block = player.getTargetBlock(null, 5);
        if (block == null) {
            player.sendMessage(Core.getMiniMessage().parse("<red>Block can't be null"));
            return;
        }
        var vector = block.getLocation().add(0.5, 0.5, 0.5).toVector();
        if (!polygonsInProgress.containsKey(id)) {
            // If not contain, add the vector
            var polygonEntry = Polygon.of(vector, null, player.getWorld());
            polygonsInProgress.put(id, polygonEntry);
            player.sendMessage(
                    Core.getMiniMessage().parse("<green>Set the lower polygon at <white>" + getCoordinates(block)));
        } else {
            // If contained check if the both the entry and value are contained.
            var entry = polygonsInProgress.get(id);
            // Check if targetVector is not present, if not set it
            if (entry.getUpper() == null) {
                entry.setUpper(vector);
                player.sendMessage(
                        Core.getMiniMessage().parse("<green>Set the upper polygon at <white>" + getCoordinates(block)));
            } else {
                // Destroy the previous entries
                polygonsInProgress.remove(id);
                // Tell the player
                player.sendMessage(Core.getMiniMessage().parse("<red>Destroyed the previous polygon."));
            }
        }

    }

    @Subcommand("commit")
    public void commitPolygon(Player player) {
        var id = player.getUniqueId();
        if (polygonsInProgress.containsKey(id)) {
            var polygon = polygonsInProgress.get(id);
            if (polygon == null) {
                player.sendMessage(Core.getMiniMessage().parse("<red>Polygon can't be null"));
                return;
            }
            if (polygon.getLower() == null) {
                player.sendMessage(Core.getMiniMessage().parse("<red>You need to set the lower polygon first."));
                return;
            }
            if (polygon.getUpper() == null) {
                player.sendMessage(Core.getMiniMessage().parse("<red>You need to set the upper polygon first."));
                return;
            }
            polygonsInProgress.remove(id);

            collisionManager.getPolygonList().add(polygon);
            player.sendMessage(Core.getMiniMessage().parse("<green>Committed the polygon."));
        } else {
            player.sendMessage(Core.getMiniMessage().parse("<red>You need to set a polygon first."));
        }
    }

    /**
     * The format to stringify the coordinates of a block.
     */
    private static final String STRING_BLOCK_FORMAT = "(%s, %s, %s)";

    /**
     * Stringifies a block
     * 
     * @param block The block to be stringified
     * @return
     */
    static String getCoordinates(Block block) {
        return String.format(STRING_BLOCK_FORMAT, block.getX(), block.getY(), block.getZ());
    }

}
