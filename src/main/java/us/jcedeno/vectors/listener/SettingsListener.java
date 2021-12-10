package us.jcedeno.vectors.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import us.jcedeno.vectors.VectorsManager;
import us.jcedeno.vectors.objects.VectorSettings;

public class SettingsListener implements Listener {
    private VectorsManager vectorsManager;

    public SettingsListener(VectorsManager vectorsManager) {
        this.vectorsManager = vectorsManager;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        var id = e.getPlayer().getUniqueId();
        if (!vectorsManager.getVectorsSettings().containsKey(id)) {
            vectorsManager.getVectorsSettings().put(id, VectorSettings.of(false, false, 5));
        }

    }
}
