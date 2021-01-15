package util.space.region;

import util.vector.ReadVector;

public interface Region {
    default boolean isInside(ReadVector p) {
        return isInside(p.getX(), p.getY());
    }
    boolean isInside(double x, double y);
}
