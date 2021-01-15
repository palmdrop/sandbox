package organic.generation.points.gaussian;

import organic.generation.AbstractSeededRandom;
import organic.generation.points.PointGenerator;
import organic.generation.points.limited.SetPointGenerator;
import sampling.Sampler;
import util.math.MathUtils;
import util.vector.Vector;

public class GaussianOffsetGenerator extends AbstractSeededRandom implements PointGenerator {
    private final PointGenerator baseGenerator;
    private final Sampler<Double> angleSampler;
    private final double angleVariation;
    private final double deviation;

    public GaussianOffsetGenerator(Vector point, double deviation) {
        this(new SetPointGenerator(point), deviation);
    }

    public GaussianOffsetGenerator(PointGenerator baseGenerator, double deviation) {
        this(baseGenerator, 0, Math.PI * 2, deviation);
    }

    public GaussianOffsetGenerator(PointGenerator baseGenerator, double angle, double angleVariation, double deviation) {
        this(baseGenerator, p -> angle, angleVariation, deviation);
    }

    public GaussianOffsetGenerator(PointGenerator baseGenerator, Sampler<Double> angleSampler, double angleVariation, double deviation) {
        this.baseGenerator = baseGenerator;
        this.angleSampler = angleSampler;
        this.angleVariation = angleVariation;
        this.deviation = deviation;
    }

    @Override
    public Vector get() {
        Vector basePoint = baseGenerator.get();

        double angle = angleSampler.get(basePoint);
        angle += MathUtils.random(0, angleVariation, random);

        double radius = deviation * random.nextGaussian();

        return Vector.fromAngle(angle).mult(radius).add(basePoint);
    }
}
