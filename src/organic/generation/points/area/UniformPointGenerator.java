package organic.generation.points.area;

import organic.generation.AbstractSeededRandom;
import organic.generation.points.PointGenerator;
import util.geometry.Rectangle;
import util.math.MathUtils;
import util.vector.Vector;

public class UniformPointGenerator extends AbstractSeededRandom implements PointGenerator {
    private final Rectangle bounds;

    public UniformPointGenerator(Rectangle bounds) {
        this.bounds = bounds;
    }

    public UniformPointGenerator(Rectangle bounds, long seed) {
        super(seed);
        this.bounds = bounds;
    }

    @Override
    public Vector get() {
        double x = MathUtils.random(bounds.x, bounds.x + bounds.width, random);
        double y = MathUtils.random(bounds.y, bounds.y + bounds.height, random);
        return new Vector(x, y);
    }
}
