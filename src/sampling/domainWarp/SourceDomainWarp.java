package sampling.domainWarp;

import sampling.Sampler;
import sampling.heightMap.HeightMap;
import util.math.MathUtils;

public class SourceDomainWarp<V> extends SimpleDomainWarp<V> {
    public interface SourceToValue<T> {
        double convert(double x, double y, T source);
    }

    public SourceDomainWarp(Sampler<V> sampler) {
        super(sampler);
    }

    public <T> SourceDomainWarp(Sampler<V> sampler, Sampler<T> source, SourceToValue<T> toRotation, SourceToValue<T> toAmount, double min, double max) {
        /*super(sampler);
        HeightMap rotation = (x, y) -> MathUtils.limit(toRotation.convert(x, y, source.get(x, y)), 0, 1);
        HeightMap amount   = (x, y) -> MathUtils.limit(toAmount.convert(x, y, source.get(x, y)), 0, 1);
        domainWarp(rotation, amount, min, max);*/
        this(sampler, source, source, toRotation, toAmount, min, max);
    }

    public <T1, T2> SourceDomainWarp(Sampler<V> sampler, Sampler<T1> rotationSource, Sampler<T2> amountSource,
                                     SourceToValue<T1> toRotation, SourceToValue<T2> toAmount, double min, double max) {
        super(sampler);
        HeightMap rotation = (x, y) -> MathUtils.limit(toRotation.convert(x, y, rotationSource.get(x, y)), 0, 1);
        HeightMap amount = (x, y) -> MathUtils.limit(toAmount.convert(x, y, amountSource.get(x, y)), 0, 1);
        domainWarp(rotation, amount, min, max);
    }
}
