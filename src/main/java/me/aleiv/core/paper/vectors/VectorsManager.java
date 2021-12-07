package me.aleiv.core.paper.vectors;

import java.time.Duration;
import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import lombok.Getter;
import me.aleiv.core.paper.Core;
import me.aleiv.core.paper.vectors.commands.VectorSettingsCommand;
import me.aleiv.core.paper.vectors.commands.VectorsCommand;
import me.aleiv.core.paper.vectors.listener.SettingsListener;
import me.aleiv.core.paper.vectors.objects.VectorSettings;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.title.Title;

/**
 * 
 * A class to manage vectors
 * 
 * @author jcedeno
 */
public class VectorsManager {

    public Core instance;
    private VectorsCommand vectorsCommand;
    private VectorSettingsCommand vectorSettingsCommand;
    private SettingsListener settingsListener;
    private @Getter HashMap<UUID, VectorSettings> vectorsSettings;

    public VectorsManager(Core instance) {
        this.instance = instance;
        this.vectorsSettings = new HashMap<>();
        this.vectorsCommand = new VectorsCommand();
        this.vectorSettingsCommand = new VectorSettingsCommand(this);
        this.settingsListener = new SettingsListener(this);
        // register the commands
        this.instance.getCommandManager().registerCommand(this.vectorsCommand);
        this.instance.getCommandManager().registerCommand(this.vectorSettingsCommand);
        // register listeners
        Bukkit.getPluginManager().registerEvents(this.settingsListener, instance);

        // Give a default vector settings to any online players
        for (Player player : Bukkit.getOnlinePlayers()) {
            this.vectorsSettings.put(player.getUniqueId(), VectorSettings.of(true, false, 5));
        }

        // Start the raycaster thread
        Bukkit.getScheduler().runTaskTimerAsynchronously(instance,
                () -> Bukkit.getOnlinePlayers().forEach(this::sendVector), 0L, 1L);
    }

    /**
     * Get the vector settings of a player. Check {@link Optional#isPresent()} when
     * using this method before using {@link Optional#get()}.
     * 
     * @param player the player to get the vector settings of.
     * @return An optional containing the vector settings of the player, if present.
     */
    public Optional<VectorSettings> getVectorSettings(Player player) {
        return Optional.ofNullable(this.vectorsSettings.get(player.getUniqueId()));
    }

    /**
     * Send a title and particle of a vector to a player
     * 
     * @param player the player to send the vector to
     */
    public void sendVector(Player player) {
        final var settings = this.vectorsSettings.get(player.getUniqueId());
        // Return if no setting are found or if disabled
        if (settings == null || settings.fullyDisabled())
            return;

        final var vec = player.rayTraceBlocks(30);
        if (vec == null)
            return;

        final var hitVector = vec.getHitPosition();

        if (settings.isShowParticle())
            Bukkit.getScheduler().runTask(instance, () -> player.spawnParticle(Particle.WATER_DROP,
                    hitVector.toLocation(player.getWorld()).add(0.0, 0.1, 0.0), 1));
        if (settings.isShowTitle())
            player.showTitle(getPrettyTitle(hitVector));
    }

    /**
     * Get a title that represents the vector.
     * 
     * @param vec The vector to display.
     * @return The title.
     */
    Title getPrettyTitle(Vector vec) {
        final var stringified = prettyPrint(vec);
        final var subtitle = MiniMessage.get().parse("<rainbow>" + stringified);
        final var times = Title.Times.of(Duration.ofSeconds(0), Duration.ofSeconds(1), Duration.ofSeconds(2));

        return Title.title(Component.empty(), subtitle, times);
    }

    /**
     * Pretty prints a vector
     * 
     * @param vector The vector to pretty print
     * @return The pretty printed vector
     */
    String prettyPrint(Vector vector) {
        return String.format("(%.2f, %.2f, %.2f,)", vector.getX(), vector.getY(), vector.getZ());
    }

}
