package us.jcedeno.vectors.commands;

import java.util.HashMap;
import java.util.UUID;

import com.destroystokyo.paper.ParticleBuilder;

import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.util.BlockVector;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.Subcommand;
import me.aleiv.core.paper.Core;

/**
 * A command design to play around with vectors in minecraft.
 * 
 * @author jcedeno
 */
@CommandAlias("vectors|vec")
public class VectorsCommand extends BaseCommand {

    private HashMap<UUID, LineVector> lineVectorMap = new HashMap<>();

    @CommandCompletion("/command1 & /commandn+1 ")
    @CommandAlias("chain0x|c0x")
    @Subcommand("chain-cmd|ccmd")
    public void rotate(Player sender, String args) {
        // Split the commands to be executed by && and execute them
        String[] cmds = args.split("&");
        for (String cmd : cmds) {
            var cleanedUpCommand = cmd.trim().substring(1);
            sender.sendMessage(Core.getMiniMessage()
                    .parse(String.format("<green>Executing command <white>%s ", cleanedUpCommand)));
            Bukkit.dispatchCommand(sender, cleanedUpCommand);

        }

    }

    @Subcommand("line-vector")
    public void buildLineVector(Player player) {
        var id = player.getUniqueId();
        // Get the block the player is looking at
        var block = player.getTargetBlock(null, 5);
        if (block == null) {
            player.sendMessage(Core.getMiniMessage().parse("<red>Block can't be null"));
            return;
        }
        var vector = block.getLocation().add(0.5, 0.5, 0.5).toVector();
        if (!lineVectorMap.containsKey(id)) {
            // If not contain, add the vector
            var vectorEntry = LineVector.of(vector, null);
            lineVectorMap.put(id, vectorEntry);
            player.sendMessage(
                    Core.getMiniMessage().parse("<green>Set the origin vector <white>" + getCoordinates(block)));
        } else {
            // If contained check if the both the entry and value are contained.
            var entry = lineVectorMap.get(id);
            // Check if targetVector is not present, if not set it
            if (entry.getV() == null) {
                entry.setV(vector);
                // Send a message to the player
                player.sendMessage(
                        Core.getMiniMessage().parse("<green>Set the target vector <white>" + getCoordinates(block)));
            } else {
                // Destroy the previous entries
                lineVectorMap.remove(id);
                // Tell the player
                player.sendMessage(Core.getMiniMessage().parse("<red>Destroyed the previous vectors."));
            }
        }
    }

    @Subcommand("trace-vector")
    @CommandCompletion("<material>")
    public void traceVector(Player sender) {
        var lineVector = lineVectorMap.get(sender.getUniqueId());
        var origin = lineVector.getU();
        var target = lineVector.getV();
        sender.sendMessage(Core.getMiniMessage().parse("<green>Going from vector U -> V. " + origin + " -> " + target));

        // Send error message to player if either is null and specify which one is it
        if (origin == null || target == null) {
            if (origin == null) {
                sender.sendMessage(Core.getMiniMessage().parse("<red>Origin vector is null"));
            }
            if (target == null) {
                sender.sendMessage(Core.getMiniMessage().parse("<red>Target vector is null"));
            }
            return;
        }

        var vectors = LineVector.of(origin, target).getPointsInBetween();
        vectors.forEach(all -> {
            if (all != null) {
                // sender.sendMessage(getClickableVector(all));
                new ParticleBuilder(Particle.BARRIER).location(all.toLocation(sender.getWorld())).receivers(20)
                        .force(true).count(1).spawn();
            }
        });

    }

    /**
     * Stringifies a block
     * 
     * @param block The block to be stringified
     * @return
     */
    static String getCoordinates(Block block) {
        return String.format("(%s, %s, %s)", block.getX(), block.getY(), block.getZ());
    }

    /**
     * Transform a BlockVector into an actual block object
     * 
     * @param vectorBlock the vector to transform
     * @param world       the world to get the block from
     * @return the block object
     */
    static Block toBlock(BlockVector vectorBlock, World world) {
        return world.getBlockAt(vectorBlock.getBlockX(), vectorBlock.getBlockY(), vectorBlock.getBlockZ());
    }

}
