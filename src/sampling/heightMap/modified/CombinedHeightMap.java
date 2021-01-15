package sampling.heightMap.modified;

import sampling.Sampler;
import util.math.MathUtils;
import sampling.heightMap.HeightMap;

import java.util.ArrayList;
import java.util.List;

public class CombinedHeightMap implements HeightMap {
    private final Sampler<Double> controller;
    private final List<Sampler<Double>> layers;

    public CombinedHeightMap(Sampler<Double> controller, Sampler<Double> first, Sampler<Double>... rest) {
        this.controller = controller;
        this.layers = new ArrayList<>();
        layers.add(first);
        layers.addAll(List.of(rest));
    }

    @Override
    public Double get(double x, double y) {
        Sampler<Double> hm = getLayer(x, y);
        return hm.get(x, y);
    }

    private Sampler<Double> getLayer(double x, double y) {
        double n = controller.get(x, y);
        int index = (int) (n * layers.size());
        index = MathUtils.limit(index, 0, layers.size() - 1);
        return layers.get(index);
    }

    public void addHeightMap(Sampler<Double> layer) {
        layers.add(layer);
    }
}
