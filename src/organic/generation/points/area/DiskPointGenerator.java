package organic.generation.points.area;

import organic.generation.AbstractSeededRandom;
import organic.generation.points.PointGenerator;
import util.vector.ReadVector;
import util.vector.Vector;

public class DiskPointGenerator extends AbstractSeededRandom implements PointGenerator {
    private final double radius;
    private final ReadVector center;

    public DiskPointGenerator(double radius, ReadVector center) {
        super();
        this.radius = radius;
        this.center = center;
    }

    public DiskPointGenerator(double radius, ReadVector center, long seed) {
        super(seed);
        this.radius = radius;
        this.center = center;
    }

    @Override
    public Vector get() {
        double angle = random.nextDouble() * Math.PI * 2;
        double u = random.nextDouble() + random .nextDouble();
        double r = (u > 1 ? 2 - u : u) * radius;
        return Vector.fromAngle(angle).mult(r).add(center);
    }
}
