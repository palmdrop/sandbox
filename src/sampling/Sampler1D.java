package sampling;

import util.math.MathUtils;

import java.lang.reflect.Array;

public interface Sampler1D<V> {
    V get(double amount);

    @SuppressWarnings("unchecked")
    static <V> V[] toArray(Sampler1D<V> sampler, double from, double to, int samples) {
        V[] arr = (V[]) Array.newInstance(sampler.get(0.0).getClass(), samples);
        for(int i = 0; i < samples; i++) {
            double v = MathUtils.map(i, 0, samples - 1, from, to);
            arr[i] = sampler.get(v);
        }
        return arr;
    }
}
