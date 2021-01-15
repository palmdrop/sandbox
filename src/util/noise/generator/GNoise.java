package util.noise.generator;

import util.math.MathUtils;
import util.noise.Noise;
import util.noise.type.ImprovedPerlinNoise;
import util.noise.type.OpenSimplexNoise;
import util.vector.Vector;

import java.util.Random;


public class GNoise implements Noise {
    private final Noise generator;
    private final Vector frequency;
    private final double min, max;

    private double pow = 1.0;

    private final Vector offset;

    public GNoise(Noise generator) {
        this(generator, new Vector(1, 1, 1));
    }

    public GNoise(Noise generator, double frequency) {
        this(generator, frequency, Vector.of(frequency, 3));
    }

    public GNoise(Noise generator, Vector frequency) {
        this(generator, frequency, new Vector(), 1);
    }

    public GNoise(Noise generator, double frequency, Vector offset) {
        this(generator, Vector.of(frequency, 3), offset, 1);
    }

    public GNoise(Noise generator, Vector frequency, double amplitude) {
        this(generator, frequency, new Vector(), amplitude);
    }


    public GNoise(Noise generator, double frequency, double amplitude) {
        this(generator, Vector.of(frequency, 3), new Vector(), amplitude);
    }

    public GNoise(Noise generator, double frequency, Vector offset, double amplitude) {
        this(generator, Vector.of(frequency, 3), offset, amplitude);
    }

    public GNoise(Noise generator, Vector frequency, Vector offset, double amplitude) {
        this.generator = generator;
        this.frequency = frequency;
        this.offset = offset;
        this.min = 0;
        this.max = amplitude;
    }

    public GNoise(Noise generator, double frequency, double min, double max) {
        this.generator = generator;
        this.frequency = Vector.of(frequency, 3);
        this.offset = Vector.random(3, 0, 1000);
        this.min = min;
        this.max = max;
    }

    public void setPow(double pow) {
        this.pow = pow;
    }


    @Override
    public Double get(double x) {
        double n = generator.get(x * frequency.getX() + offset.getX());
        double pn = Math.pow(n, pow);
        return MathUtils.map(pn, 0, 1, min, max);
    }

    @Override
    public Double get(double x, double y) {
        double n = generator.get(x * frequency.getX() + offset.getX(), y * frequency.getY() + offset.getY());
        double pn = Math.pow(n, pow);
        return MathUtils.map(pn, 0, 1, min, max);
    }

    @Override
    public Double get(double x, double y, double z) {
        double n = generator.get(x * frequency.getX() + offset.getX(),
                y * frequency.getY() + offset.getY(),
                z * frequency.getZ() + offset.getZ());
        double pn = Math.pow(n, pow);
        return MathUtils.map(pn, 0, 1, min, max);
    }

    public static Noise perlinNoise(double frequency, double amplitude, double pow) {
        return perlinNoise(Vector.of(frequency, 3), amplitude, pow);
    }

    public static Noise perlinNoise(Vector frequency, double amplitude, double pow) {
        long seed = random.nextLong();
        GNoise noise = new GNoise(new ImprovedPerlinNoise(seed), frequency, amplitude);
        noise.setPow(pow);
        return noise;
    }

    public static Noise simplexNoise(double frequency, double amplitude, double pow) {
        return simplexNoise(Vector.of(frequency, 3), amplitude, pow);
    }

    private static final Random random = new Random();

    public static Noise simplexNoise(Vector frequency, double amplitude, double pow) {
        long seed = random.nextLong();
        GNoise noise = new GNoise(new OpenSimplexNoise(seed), frequency, amplitude);
        noise.setPow(pow);
        return noise;
    }
}
