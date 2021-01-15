package sampling.patterns;

import sampling.Sampler;
import sampling.heightMap.HeightMap;
import sampling.heightMap.HeightMaps;
import util.noise.ComplexFractalHeightMap;
import util.noise.FractalHeightMap;

public class CurlWavePattern extends AbstractPattern {
    public CurlWavePattern(double baseFrequency, double waveFrequency, double waveAngle, double lacunarity, double persistence, double pow, int numberOfOctaves) {
        super(new ComplexFractalHeightMap(baseFrequency,
                        1.0,
                        lacunarity,
                        HeightMaps.constant(1.0),
                        persistence,
                        HeightMaps.sin(waveFrequency * Math.cos(waveAngle), waveFrequency * Math.sin(waveAngle), 1.0, pow),
                        FractalHeightMap.Type.SIMPLEX, numberOfOctaves, System.nanoTime()
                ).setNormalize(true));
    }
}
