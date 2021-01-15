package sampling.domainWarp.warper;

import util.vector.Vector;

public interface Warper {
    default Vector warp(double x, double y) {
        return warp(new Vector(x, y));
    }
    Vector warp(Vector point);
}
