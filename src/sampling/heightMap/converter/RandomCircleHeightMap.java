package sampling.heightMap.converter;

import util.geometry.Circle;
import util.geometry.Rectangle;
import util.math.MathUtils;
import util.space.tree.QuadTree;
import util.vector.Vector;

public class RandomCircleHeightMap extends CircleHeightMap {
    public RandomCircleHeightMap(double width, double height, int numberOfCircles, double minRadius, double maxRadius, double fade, double fadePow) {
        super(new QuadTree<>(new Rectangle(width, height), 4), maxRadius, fade, fadePow);
        generate(numberOfCircles, minRadius, maxRadius);
    }

    private void generate(int numberOfCircles, double minRadius, double maxRadius) {
        for(int i = 0; i < numberOfCircles; i++) {
            Vector p = Vector.random(circles.getWidth(), circles.getHeight());
            Circle c = new Circle(p, MathUtils.random(minRadius, maxRadius));
            circles.insert(p, c);
        }
    }
}
