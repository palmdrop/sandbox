package organic.generation.points.area;

import organic.generation.points.PointGenerator;
import sampling.heightMap.HeightMap;
import util.space.WeightedRandomMap;
import util.vector.Vector;

public class HeightMapPointGenerator implements PointGenerator {
    private final WeightedRandomMap map;

    public HeightMapPointGenerator(HeightMap heightMap, int width, int height) {
        map = WeightedRandomMap.mapFromNoise(width, height, heightMap);
    }

    @Override
    public Vector get() {
        return map.getRandom();
    }
}
