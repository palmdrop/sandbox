package sampling.heightMap.modified;

import sampling.Sampler;
import sampling.heightMap.HeightMap;
import sampling.heightMap.HeightMaps;
import util.math.MathUtils;

public class ModdedHeightMap implements HeightMap {
    public interface Modification {
        double apply(double x, double y, double value);
    }

    private final Sampler<Double> heightMap;
    private final Modification modification;

    public ModdedHeightMap(Sampler<Double> heightMap, Modification modification) {
        this.heightMap = heightMap;
        this.modification = modification;
    }

    public ModdedHeightMap(Sampler<Double> heightMap) {
        this(heightMap, (x, y, value) -> value);
    }

    @Override
    public Double get(double x, double y) {
        return modification.apply(x, y, heightMap.get(x, y));
    }

    public ModdedHeightMap addMod(double multiplier) {
        return ModdedHeightMap.mod(this, multiplier);
    }

    public static ModdedHeightMap mod(HeightMap baseMap, double multiplier) {
        return new ModdedHeightMap(baseMap, (x, y, n) -> (multiplier * n % 1.0));
    }

    public ModdedHeightMap addSmoothMod(double multiplier) {
        return ModdedHeightMap.smoothMod(this, multiplier);
    }

    public static ModdedHeightMap smoothMod(HeightMap baseMap, double multiplier) {
        return new ModdedHeightMap(baseMap, (x, y, n) -> {
            n = 2 * n * multiplier;
            n %= 2.0;
            if(n > 1.0) n = 2 - n;
            return n;
        });
    }

    public ModdedHeightMap addThreshold(double threshold) {
        return ModdedHeightMap.addThreshold(this, threshold);
    }

    public static ModdedHeightMap addThreshold(HeightMap baseMap, double threshold) {
        return new ModdedHeightMap(baseMap, (x, y, n) -> n < threshold ? 0.0 : 1.0);
    }

    public ModdedHeightMap addSoftThreshold(double threshold, double softness, double pow) {
        return ModdedHeightMap.softThreshold(this, threshold, softness, pow);
    }

    public static ModdedHeightMap softThreshold(HeightMap baseMap, double threshold, double softness, double pow) {
        return new ModdedHeightMap(baseMap, (x, y, n) -> {
            if(n < threshold - softness/2) return 0;
            else if(n > threshold + softness/2) return 1;
            return MathUtils.map(n, threshold - softness/2, threshold + softness/2, 0, 1, MathUtils.EasingMode.EASE_IN_OUT, pow);
        });
    }

    public ModdedHeightMap addReverse() {
        return ModdedHeightMap.reverse(this);
    }

    public static ModdedHeightMap reverse(HeightMap baseMap) {
        return new ModdedHeightMap(baseMap, (x, y, n) -> 1 - n);
    }

    public ModdedHeightMap changeBrightness(double amount) {
        return ModdedHeightMap.brightness(this, amount);
    }

    public static ModdedHeightMap brightness(HeightMap baseMap, double amount) {
        return new ModdedHeightMap(baseMap, (x, y, n) -> pointBrightness(n, amount));
    }

    private static double pointBrightness(double current, double amount) {
        return MathUtils.limit(current + amount, 0, 1);
    }

    public ModdedHeightMap changeContrast(double amount) {
        return ModdedHeightMap.contrast(this, amount);
    }

    public static ModdedHeightMap contrast(HeightMap baseMap, double amount) {
        if(amount < 0) throw new IllegalArgumentException();
        return new ModdedHeightMap(baseMap, (x, y, n) -> pointBrightness(n * amount, 0.5 * (1 - amount)));
    }

    public ModdedHeightMap setContrastAndBrightness(double contrast, double brightness) {
        return contrastAndBrightness(this, contrast, brightness);
    }

    public static ModdedHeightMap contrastAndBrightness(HeightMap baseMap, double contrast, double brightness) {
        return brightness(contrast(baseMap, contrast), brightness);
    }

    public static ModdedHeightMap remap(HeightMap baseMap, double min, double max, double newMin, double newMax) {
        return new ModdedHeightMap(HeightMaps.remap(baseMap, min, max, newMin, newMax));
    }

    public ModdedHeightMap addRemap(double min, double max, double newMin, double newMax) {
        return ModdedHeightMap.remap(this, min, max, newMin, newMax);
    }

    public static ModdedHeightMap offset(HeightMap baseMap, double offset, boolean wrap) {
        return ModdedHeightMap.offset(baseMap, HeightMaps.constant(offset), wrap);
    }

    public static ModdedHeightMap offset(HeightMap baseMap, HeightMap offset, boolean wrap) {
        return new ModdedHeightMap(baseMap, (x, y, n) -> {
            double dn = n + offset.get(x, y);
            if(wrap) {
                while(dn < 0)   dn += 1.0;
                while(dn > 1.0) dn -= 1.0;
            } else {
                dn = MathUtils.limit(dn, 0, 1);
            }
            return dn;
        });
    }

    public ModdedHeightMap addOffset(double offset, boolean wrap) {
        return ModdedHeightMap.offset(this, offset, wrap);
    }

    public ModdedHeightMap addOffset(HeightMap baseMap, boolean wrap) {
        return ModdedHeightMap.offset(this, baseMap, wrap);
    }
}
