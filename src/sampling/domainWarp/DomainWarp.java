package sampling.domainWarp;

import sampling.Sampler;
import sampling.domainWarp.warper.AddWarper;
import util.vector.Vector;

public interface DomainWarp<V> extends AddWarper, Sampler<V> {
    default V get(double x, double y) {
        return get(new Vector(x, y));
    }
    V get(Vector point);
}
