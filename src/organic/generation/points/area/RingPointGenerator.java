package organic.generation.points.area;

import organic.generation.AbstractSeededRandom;
import organic.generation.points.PointGenerator;
import util.vector.ReadVector;
import util.vector.Vector;

public class RingPointGenerator extends AbstractSeededRandom implements PointGenerator {
    private final double radius;
    private final ReadVector center;

    public RingPointGenerator(double radius, ReadVector center) {
        this.radius = radius;
        this.center = center;
    }

    @Override
    public Vector get() {
        double angle = Math.PI * 2 * random.nextDouble();
        return Vector.fromAngle(angle).mult(radius).add(center);
    }
}
