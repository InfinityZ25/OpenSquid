package us.jcedeno.skins;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * A data class that represents a player skin.
 * 
 * @author jcedeno
 */
@AllArgsConstructor(staticName = "of")
@Data
public class PlayerSkin {
    String name;
    String value;
    String signature;

    /**
     * @return where the skin has been signed and thus uploaded to Mojang or not.
     */
    public boolean isItSigned() {
        return signature != null && !signature.isEmpty();
    }

}
