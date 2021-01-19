package sampling.patterns;

import sampling.Sampler;
import sampling.heightMap.HeightMaps;
import sampling.heightMap.modified.WarpedHeightMap;
import sampling.heightMap.modified.pixelated.DynamicPixelatedSampler;

public class PixelMorphPattern extends AbstractPattern {
    private static Sampler<Double> createHeightmap(double frequency, double variance, double pixelWidth, double pixelHeight, double threshold, double warpAmount, double baseRecursion) {
        Sampler<Double> base1 =
                new FractalPattern(frequency, frequency * variance, 1.7, 0.8, 0.3, 10);

        Sampler<Double> pix1
                = new DynamicPixelatedSampler<>(base1,
                pixelWidth,
                pixelHeight,
                base1,
                threshold,
                4,
                DynamicPixelatedSampler.Mode.CORNER);

        Sampler<Double> texture =
                pix1;

        double rotation = Math.random() * Math.PI * 2;
        for(int i = 0; i < baseRecursion; i++) {
            texture =
                    new WarpedHeightMap(base1).domainWarp(
                            HeightMaps.constant(rotation),
                            texture,
                            -0, warpAmount);
        }

        return texture;
    }

    public PixelMorphPattern(double frequency, double variance, double pixelWidth, double pixelHeight, double threshold, double warpAmount, double baseRecursion) {
        super(createHeightmap(frequency, variance, pixelWidth, pixelHeight, threshold, warpAmount, baseRecursion));
    }
}
