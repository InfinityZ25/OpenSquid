package us.jcedeno.vectors.objects;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor(staticName = "of")
public class VectorSettings {
    boolean showTitle;
    boolean showParticle;
    double rayCastLength;

    /**
     * @return the default settings
     */
    public boolean fullyDisabled() {
        return !showTitle && !showParticle;
    }

}
