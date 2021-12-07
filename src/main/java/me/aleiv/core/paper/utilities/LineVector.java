package me.aleiv.core.paper.utilities;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.util.Vector;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Helper class that represent a line in 3D space.
 */
public @Data @AllArgsConstructor(staticName = "of") class LineVector {
    Vector u;
    Vector v;

    public static Double interval = 0.05;

    /**
     * @return The distance between the two points. This also represents the length
     *         of the line connecting u to v.
     */
    public double distance() {
        return u.distance(v);
    }

    /**
     * Returns the vector VU, that is the vector that takes you v -> u.
     * 
     * @return A new VU Vector.
     */
    public Vector getVUVector() {
        return v.clone().subtract(u);
    }

    /**
     * Returns the vector UV, that is the vector that takes you u -> v.
     * 
     * @return A new UV Vector.
     */
    public Vector getUVVector() {
        return u.clone().subtract(v);
    }

    /**
     * Returns all the points that are on the line vector UV.
     * 
     * @return A list of points.
     */
    public List<Vector> getPointsInBetween() {
        // Calculate the uv vector
        var uv = getVUVector().normalize();
        // Create a vector array of the length of the distance between the two points
        var points = new ArrayList<Vector>();

        var distance = Math.ceil(u.distance(v));

        // Get all points from u to v
        for (double i = 0; i < distance; i += (interval)) {
            points.add(u.clone().add(uv.clone().multiply(i)));
        }

        return points;
    }

}