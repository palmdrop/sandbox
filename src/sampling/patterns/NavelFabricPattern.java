package sampling.patterns;

import sampling.Sampler;
import sampling.heightMap.HeightMap;
import sampling.heightMap.HeightMaps;
import sampling.heightMap.modified.WarpedHeightMap;
import sampling.heightMap.modified.DynamicFractalHeightMap;
import util.noise.generator.GNoise;

import java.util.function.Supplier;

public class NavelFabricPattern extends AbstractPattern {
    private static Sampler<Double> createHeightmap(double baseFrequency, double threshold, double thinness, double lacunarity, double persistence, int warp, boolean stretch) {
        Supplier<HeightMap> heightMapSupplier = () ->
                HeightMaps.pow(
                        HeightMaps.thresholdReverse(GNoise.simplexNoise(baseFrequency, 1.0, 1.0), threshold, false, true),
                        HeightMaps.constant(1.0 - thinness)
                );

        HeightMap base1 = new DynamicFractalHeightMap(1.0,
                1.0,
                lacunarity,
                stretch ? heightMapSupplier.get() : HeightMaps.constant(1.0),
                persistence,
                heightMapSupplier.get(),
                heightMapSupplier,
                8).setNormalize(true);


        Sampler<Double> texture =
                base1;

        int recursionSteps = 3;
        for(int i = 0; i < recursionSteps; i++) {
            texture =
                    new WarpedHeightMap(base1).domainWarp(
                            texture,
                            texture,
                            0, warp);
        }

        return texture;
    }

    public NavelFabricPattern(double baseFrequency, double threshold, double thinness, double lacunarity, double persistence, int warp, boolean stretch) {
        super(createHeightmap(baseFrequency, threshold, thinness, lacunarity, persistence, warp, stretch));
    }
}
