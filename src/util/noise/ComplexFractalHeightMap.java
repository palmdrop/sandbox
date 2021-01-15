package util.noise;

import sampling.Sampler;
import sampling.heightMap.HeightMap;

import java.util.function.Supplier;

public class ComplexFractalHeightMap extends FractalHeightMap {
    private final HeightMap lacunarityControl;
    private final HeightMap persistenceControl;

    public ComplexFractalHeightMap(double frequency, double amplitude, double lacunarity, HeightMap lacunarityControl, double persistence, HeightMap persistenceControl, Type type, int numberOfOctaves, long seed) {
        super(frequency, amplitude, lacunarity, persistence, type, numberOfOctaves, seed);
        this.lacunarityControl = lacunarityControl;
        this.persistenceControl = persistenceControl;
    }

    public ComplexFractalHeightMap(double frequency, double amplitude, double lacunarity, HeightMap lacunarityControl, double persistence, HeightMap persistenceControl, Supplier<? extends Sampler<Double>> supplier, int numberOfOctaves) {
        super(frequency, amplitude, lacunarity, persistence, supplier, numberOfOctaves);
        this.lacunarityControl = lacunarityControl;
        this.persistenceControl = persistenceControl;
    }

    @Override
    public Double get(double x, double y) {
        double lacuranityFactor = lacunarityControl.get(x, y);
        double persistenceFactor = persistenceControl.get(x, y);

        double n = 0.0;
        for(int i = 0; i < octaves.size(); i++) {
            double freq = frequency * Math.pow(lacuranityFactor * lacunarity, i);
            double amp = amplitude * Math.pow(persistenceFactor * persistence, i);
            n += amp * octaves.get(i).get(x * freq, y * freq);
        }

        if(normalize) n /= divider;

        return n;
    }
}
