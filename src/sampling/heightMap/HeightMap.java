package sampling.heightMap;

import sampling.Sampler;
import sampling.heightMap.modified.ModdedHeightMap;
import sampling.heightMap.modified.WarpedHeightMap;
import util.vector.Vector;

public interface HeightMap extends Sampler<Double> {
    default Double get(Vector p) {
        return get(p.getX(), p.getY());
    }
    Double get(double x, double y);

    default ModdedHeightMap toModded() {
        return new ModdedHeightMap(this);
    }
    default WarpedHeightMap toDistorted() {
        return new WarpedHeightMap(this);
    }
}
