package us.jcedeno.cookie.objects;

import java.awt.image.BufferedImage;

import org.bukkit.entity.Player;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;
import org.jetbrains.annotations.NotNull;

import lombok.Getter;

/**
 * Custom renderer used to quickly paint an image onto a minecraft map canvas.
 * 
 * @author jcedeno
 */
public class CustomRender extends MapRenderer {
    private @Getter MapCanvas canvas;
    private @Getter BufferedImage image;
    private boolean hasRender = false;

    public CustomRender(CookieEnum cookie) {
        this.image = cookie.getBufferedImage();
    }

    @Override
    public void render(@NotNull MapView map, @NotNull MapCanvas canvas, @NotNull Player player) {
        if (!hasRender) {
            try {
                canvas.drawImage(0, 0, image);
                this.hasRender = true;
                this.canvas = canvas;
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    /**
     * Static constructor for the Custom Renderer class.
     * 
     * @param fileName The name of the image file to be drawn.
     * @return A new instance of the CustomRender class.
     */
    public static CustomRender fromFile(CookieEnum cookie) {
        return new CustomRender(cookie);
    }

}
