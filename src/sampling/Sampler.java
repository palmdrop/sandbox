package sampling;

import util.vector.Vector;

public interface Sampler<V> {
    default V get(double x, double y) {
        return get(new Vector(x, y));
    }
    V get(Vector point);
}
