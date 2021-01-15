package sampling;

import util.vector.Vector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CombinedSampler<T> implements Sampler<T> {
    public interface SamplerCombiner<T> {
        T combine(List<Sampler<T>> samplers, Vector point);
    }
    public interface ValueCombiner<T> {
        T combine(List<T> samples);
        default SamplerCombiner<T> toSamplerCombiner() {
            return (l, p) -> this.combine(l.stream().map(s -> s.get(p)).collect(Collectors.toList()));
        }
    }

    private final List<Sampler<T>> samplers;
    private final SamplerCombiner<T> combiner;

    public CombinedSampler(ValueCombiner<T> combiner, Sampler<T> first, Sampler<T>... rest) {
        this(combiner.toSamplerCombiner(), first, rest);
    }

    public CombinedSampler(SamplerCombiner<T> combiner, Sampler<T> first, Sampler<T>... rest) {
        this.samplers = new ArrayList<>(1 + rest.length);
        samplers.add(first);
        samplers.addAll(Arrays.asList(rest));

        this.combiner = combiner;
    }

    public CombinedSampler(SamplerCombiner<T> combiner, List<Sampler<T>> samplers) {
        this.combiner = combiner;
        this.samplers = samplers;
    }

    @Override
    public T get(Vector point) {
        return combiner.combine(samplers, point);
    }
}
