package organic.generation.points.limited;

import organic.generation.AbstractSeededRandom;
import organic.generation.SeededRandom;
import organic.generation.points.PointGenerator;
import util.vector.ReadVector;
import util.vector.Vector;

public class UniformLinePointGenerator extends AbstractSeededRandom implements PointGenerator {
    private final ReadVector start, stop;

    public UniformLinePointGenerator(ReadVector start, ReadVector stop) {
        super();
        this.start = start;
        this.stop = stop;
    }
    public UniformLinePointGenerator(ReadVector start, ReadVector stop, long seed) {
        super(seed);
        this.start = start;
        this.stop = stop;
    }

    @Override
    public Vector get() {
        double r = random.nextDouble();
        return Vector.interpolate(start, stop, r);
    }
}
