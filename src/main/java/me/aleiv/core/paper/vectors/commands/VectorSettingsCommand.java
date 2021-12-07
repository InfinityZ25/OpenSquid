package me.aleiv.core.paper.vectors.commands;

import org.bukkit.entity.Player;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Subcommand;
import me.aleiv.core.paper.vectors.VectorsManager;
import net.kyori.adventure.text.minimessage.MiniMessage;

@CommandAlias("vsettings|vst")
public class VectorSettingsCommand extends BaseCommand {
    private VectorsManager vectorsManager;

    public VectorSettingsCommand(VectorsManager vectorsManager) {
        this.vectorsManager = vectorsManager;
    }

    @Subcommand("toggle title")
    public void changeTitleSettings(Player sender) {
        var optionalSettings = vectorsManager.getVectorSettings(sender);

        optionalSettings.ifPresent(settings -> {
            settings.setShowTitle(!settings.isShowTitle());
            sender.sendMessage(MiniMessage.get().parse((settings.isShowTitle() ? "<green>" : "<red>")
                    + "Title Vector is now " + (settings.isShowTitle() ? "enabled" : "disabled") + "."));
        });

    }

    @Subcommand("toggle particle")
    public void changeParticleSetting(Player sender) {
        var optionalSettings = vectorsManager.getVectorSettings(sender);

        optionalSettings.ifPresent(settings -> {
            settings.setShowParticle(!settings.isShowParticle());
            sender.sendMessage(MiniMessage.get().parse((settings.isShowParticle() ? "<green>" : "<red>")
                    + "Particle Vector is now " + (settings.isShowParticle() ? "enabled" : "disabled") + "."));
        });

    }

    @Subcommand("change ray-distance")
    public void changeRayCastDistance(Player sender, @Default("5") Double distance) {
        var optionalSettings = vectorsManager.getVectorSettings(sender);

        optionalSettings.ifPresent(settings -> {
            settings.setRayCastLength(distance);
            sender.sendMessage(MiniMessage.get().parse("<green>Ray-cast distance is now " + distance + "."));
        });

    }

}
