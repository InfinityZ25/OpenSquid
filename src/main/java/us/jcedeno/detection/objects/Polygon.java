package us.jcedeno.detection.objects;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.Vector;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor(staticName = "of")
public class Polygon {
    Vector lower;
    Vector upper;
    World world;

    /**
     * Checks if a given location is inside the polygon.
     * 
     * @param loc The location to check.
     * @return True if the location is inside the polygon, false otherwise.
     */
    public boolean isInside(Location loc) {
        return loc.getWorld() == this.world && this.inInside(loc.toVector());
    }

    /**
     * Checks if a given vector is inside the polygon.
     * 
     * @param vec The vector to check.
     * @return True if the vector is inside the polygon, false otherwise.
     */
    public boolean inInside(Vector vec) {

        double minX, minY, minZ, maxX, maxY, maxZ;

        if (lower.getX() < upper.getX()) {
            minX = lower.getX();
            maxX = upper.getX();
        } else {
            minX = upper.getX();
            maxX = lower.getX();
        }
        if (lower.getY() < upper.getY()) {
            minY = lower.getY();
            maxY = upper.getY();
        } else {
            minY = upper.getY();
            maxY = lower.getY();
        }
        if (lower.getZ() < upper.getZ()) {
            minZ = lower.getZ();
            maxZ = upper.getZ();
        } else {
            minZ = upper.getZ();
            maxZ = lower.getZ();
        }

        return !((vec.getX() < minX || vec.getY() < minY || vec.getZ() < minZ)
                || (vec.getX() > maxX || vec.getY() > maxY || vec.getZ() > maxZ));
    }

}
