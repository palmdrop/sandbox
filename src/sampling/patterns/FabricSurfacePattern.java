package sampling.patterns;

import sampling.Sampler;
import sampling.heightMap.HeightMap;
import sampling.heightMap.HeightMaps;
import sampling.heightMap.modified.WarpedHeightMap;
import util.geometry.Circle;
import util.math.MathUtils;
import util.noise.FractalHeightMap;
import util.noise.generator.GNoise;
import util.vector.Vector;

import java.util.function.Supplier;

public class FabricSurfacePattern extends AbstractPattern {
    private static Sampler<Double> createHeightmap(double baseFrequency, double threshold, double thinness, double lacunarity, double persistence, double warp) {
        Supplier<HeightMap> heightMapSupplier = () ->
                HeightMaps.pow(
                        HeightMaps.thresholdReverse(GNoise.simplexNoise(baseFrequency, 1.0, 1.0), threshold, false, true),
                        HeightMaps.constant(1.0 - thinness)
                );

        HeightMap base1 =
                new FractalHeightMap(1.0,
                        1.0,
                        lacunarity,
                        persistence,
                        heightMapSupplier,
                        9).setNormalize(true);

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

    public FabricSurfacePattern(double baseFrequency, double threshold, double thinness, double lacunarity, double persistence, double warp) {
        super(createHeightmap(baseFrequency, threshold, thinness, lacunarity, persistence, warp));
    }
}
