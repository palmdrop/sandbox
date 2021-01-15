package organic.generation.points.combined;

import organic.generation.AbstractSeededRandom;
import organic.generation.points.PointGenerator;
import util.math.WeightedRandomList;
import util.vector.Vector;

import java.util.ArrayList;
import java.util.List;

public class WeightedPointGenerator extends AbstractSeededRandom implements PointGenerator {
    public static class Builder {
        private List<PointGenerator> generators;
        private List<Double> weights;

        public Builder() {
            generators = new ArrayList<>();
            weights = new ArrayList<>();
        }

        public Builder add(double weight, PointGenerator generator) {
            weights.add(weight);
            generators.add(generator);
            return this;
        }

        public WeightedPointGenerator build() {
            if(weights.size() == 0) throw new IllegalStateException();
            return new WeightedPointGenerator(weights, generators);
        }
    }

    private WeightedRandomList<PointGenerator> generators;

    private WeightedPointGenerator(List<Double> weights, List<PointGenerator> generators) {
        this.generators = new WeightedRandomList<>(weights.size());
        for(int i = 0; i < weights.size(); i++) {
            this.generators.set(i, weights.get(i), generators.get(i));
        }
        this.generators.calculateSearchMap();
    }

    @Override
    public Vector get() {
        return generators.getRandomElement().get();
    }


}
