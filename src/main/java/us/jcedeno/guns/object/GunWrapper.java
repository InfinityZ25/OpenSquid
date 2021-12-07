package us.jcedeno.guns.object;

import org.bukkit.inventory.ItemStack;

import lombok.Data;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * An object used to wrap a {@link ItemStack} gun and its metadata, such as how
 * many bullets are in the gun, how the gun should fire, etc.
 * 
 * @author jcedeno
 */
@RequiredArgsConstructor(staticName = "of")
@Data
public class GunWrapper {
    private @NonNull final ItemStack gun;
    private @NonNull final GunType type;
    private @NonNull final Integer roundsPerMaganize;
    private @Getter int roundsLeft;

    /**
     * This is a consequence of using lombok, implement something like this later
     * https://stackoverflow.com/a/48336560/17280644
     * 
     * For now, this must be called after initializing the object.
     * 
     */
    public void init() {
        // For now "reload" the gun so that it has rounds in the magazine
        reload();
    }

    /**
     * Reloads the gun, setting the roundsLeft to the roundsPerMaganize.
     */
    public void reload() {
        this.roundsLeft = this.roundsPerMaganize;
        // TODO: Call the event
    }

    /**
     */
    public void fire() {
        this.roundsLeft--;

        // TODO: Call the event
    }

}
