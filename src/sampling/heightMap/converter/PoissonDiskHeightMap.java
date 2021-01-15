package sampling.heightMap.converter;

import organic.generation.points.PoissonDiskSampling;
import sampling.heightMap.HeightMap;
import util.geometry.Circle;
import util.geometry.Rectangle;
import util.math.MathUtils;
import util.space.tree.QuadTree;
import util.vector.Vector;

import java.util.List;

public class PoissonDiskHeightMap extends CircleHeightMap implements HeightMap {
    private final int width, height;

    public PoissonDiskHeightMap(int width, int height, HeightMap rg, double minRadius, double maxRadius, double fade, double fadePow) {
        super(new QuadTree<>(new Rectangle(width, height), 4), maxRadius, fade, fadePow);
        this.width = width;
        this.height = height;
        generate(rg, minRadius, maxRadius);
    }

    private void generate(HeightMap rg, double minRadius, double maxRadius) {
        List<Vector> points = PoissonDiskSampling.generate(width, height, 30, rg, minRadius, maxRadius, false);
        for(Vector p : points) {
            Circle c = new Circle(p, MathUtils.map(rg.get(p), 0, 1, minRadius, maxRadius)/2);
            circles.insert(p, c);
        }
    }
}
