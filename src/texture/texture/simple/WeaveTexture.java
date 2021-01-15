package texture.texture.simple;

import sampling.heightMap.HeightMap;
import sampling.heightMap.HeightMaps;
import util.math.MathUtils;
import util.noise.generator.GNoise;
import util.vector.Vector;

public class WeaveTexture extends SimpleTexture {
    private static HeightMap getTexture(double frequency, double stretch, double stretchVariation, double rotationVariation) {
        double r1 = Math.PI * 2 * Math.random();
        double r2 = r1 + MathUtils.randomGaussian(rotationVariation);
        return getTexture(frequency, stretch, stretchVariation, r1, r2);
    }

    private static HeightMap getTexture(double frequency, double stretch1, double stretchVariation, double rotation1, double rotation2) {
        double stretch2 = stretch1 + MathUtils.randomGaussian(stretchVariation);

        HeightMap hm1 = GNoise.simplexNoise(frequency, 1, 1);
        hm1 = HeightMaps.stretch(hm1, stretch1, 1)
                .toDistorted()
                .rotate(new Vector(), rotation1);

        HeightMap hm2 = GNoise.simplexNoise(frequency, 1, 1);
        hm2 = HeightMaps.stretch(hm2, stretch2, 1)
                .toDistorted()
                .rotate(new Vector(), rotation2);

        return HeightMaps.mult(hm1, hm2, 0.5)
                .toModded().addReverse();
    }

    public WeaveTexture(double frequency, double stretch, double stretchVariation, double rotationVariation) {
        super(getTexture(frequency, stretch, stretchVariation, rotationVariation));
    }

    public WeaveTexture(double frequency, double stretch, double stretchVariation, double rotation1, double rotation2) {
        super(getTexture(frequency, stretch, stretchVariation, rotation1, rotation2));
    }

    public SimpleTexture selfWarp(double amount) {
        return new SimpleTexture(getHeightMap().toDistorted().domainWarp(getHeightMap(), getHeightMap(), amount));
    };
}
