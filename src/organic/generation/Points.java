package organic.generation;

import util.geometry.Rectangle;
import util.vector.ReadVector;

import java.util.List;

public class Points {

    public static Rectangle getBounds(List<? extends ReadVector> points) {
        double minX = Double.MAX_VALUE;
        double maxX = Double.MIN_VALUE;
        double minY = Double.MAX_VALUE;
        double maxY = Double.MIN_VALUE;

        for(ReadVector p : points) {
            minX = Math.min(p.getX(), minX);
            maxX = Math.max(p.getX(), maxX);
            minY = Math.min(p.getY(), minY);
            maxY = Math.max(p.getY(), maxY);
        }

        return Rectangle.range(minX, maxX, minY, maxY);
    }


}
