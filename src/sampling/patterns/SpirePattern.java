package sampling.patterns;

import sampling.Sampler;
import sampling.heightMap.HeightMap;
import sampling.heightMap.HeightMaps;
import sampling.heightMap.modified.WarpedHeightMap;
import util.noise.ComplexFractalHeightMap;
import util.noise.generator.GNoise;

import java.util.function.Supplier;

public class SpirePattern extends AbstractPattern {
    private static Sampler<Double> createHeightmap(double baseFrequency, double threshold, double thinness, double lacunarity, double persistence, int warp, int recursionSteps) {
        Supplier<HeightMap> heightMapSupplier = () ->
                HeightMaps.pow(
                        HeightMaps.thresholdReverse(GNoise.simplexNoise(baseFrequency, 1.0, 1.0), threshold, false, true),
                        HeightMaps.constant(1.0 - thinness)
                );

        HeightMap controller = heightMapSupplier.get();

        HeightMap base1 = new ComplexFractalHeightMap(1.0,
                1.0,
                lacunarity,
                HeightMaps.constant(1.0),
                persistence,
                controller,
                heightMapSupplier,
                8).setNormalize(true);


        Sampler<Double> texture =
                base1;

        for(int i = 0; i < recursionSteps; i++) {
            texture =
                    new WarpedHeightMap(base1).domainWarp(
                            texture,
                            HeightMaps.constant(1),
                            0, warp);
        }

        return texture;
    }

    public SpirePattern(double baseFrequency, double threshold, double thinness, double lacunarity, double persistence, int warp, int recursionSteps) {
        super(createHeightmap(baseFrequency, threshold, thinness, lacunarity, persistence, warp, recursionSteps));
    }
}
