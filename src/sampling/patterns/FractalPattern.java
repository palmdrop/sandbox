package sampling.patterns;

import sampling.Sampler;
import sampling.heightMap.HeightMap;
import sampling.heightMap.HeightMaps;
import sampling.heightMap.modified.WarpedHeightMap;
import util.noise.ComplexFractalHeightMap;
import util.noise.FractalHeightMap;
import util.noise.generator.GNoise;

public class FractalPattern extends AbstractPattern {
    public FractalPattern(double baseFrequency, double controlFrequency, double lacunarity, double persistence, double effect, int numberOfOctaves) {
         super(
                 new ComplexFractalHeightMap(baseFrequency,
                        1.0,
                        lacunarity,
                        HeightMaps.constant(1.0),
                        persistence,
                        GNoise.simplexNoise(controlFrequency, 1.0, 1.0).toModded().addRemap(0.0, 1.0, 1.0 - effect, 1.0),
                        FractalHeightMap.Type.SIMPLEX, numberOfOctaves, System.nanoTime()
                ).setNormalize(true)
         );
    }
}
