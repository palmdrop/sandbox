package sampling.patterns;

import sampling.Sampler;
import sampling.heightMap.HeightMap;
import sampling.heightMap.modified.WarpedHeightMap;

public class AbstractPattern implements Pattern {
    private Sampler<Double> base;
    private Sampler<Double> recursivePattern;

    public AbstractPattern(Sampler<Double> base) {
        this.base = base;
        setRecursion(1, 0);
    }

    @Override
    public Pattern setRecursion(int numberOfIterations, double warpAmount) {
        recursivePattern = base;
        for(int i = 0; i < numberOfIterations; i++) {
            recursivePattern = new WarpedHeightMap(recursivePattern).domainWarp(
                            recursivePattern,
                            recursivePattern,
                            -warpAmount, warpAmount);
        }
        return this;
    }

    @Override
    public Double get(double x, double y) {
        return recursivePattern.get(x, y);
    }
}
