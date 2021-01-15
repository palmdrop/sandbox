package sampling;

import util.vector.Vector;

public class BlurSampler<V> implements Sampler<V> {
    private static double[][] calculateKernel(double[] row) {
        double[][] kernel = new double[5][5];

        double sum = 0;
        for(int i = 0; i < row.length; i++) {
            for(int j = 0; j < row.length; j++) {
                kernel[i][j] = row[i] * row[j];
                sum += kernel[i][j];
            }
        }

        for(int i = 0; i < row.length; i++) {
            for(int j = 0; j < row.length; j++) {
                kernel[i][j] /= sum;
            }
        }

        return kernel;
    }

    public final static double[][] kernel5x5 = calculateKernel(new double[]{1, 4, 6, 4, 1});
    public final static double[][] kernel3x3 = calculateKernel(new double[]{1, 2, 1});

    public BlurSampler(Sampler<V> source, Combiner<V> combiner, double offset, double[][] kernel) {
        if(kernel == null) throw new IllegalArgumentException();
        for (double[] row : kernel) {
            if (kernel.length != row.length) throw new IllegalArgumentException();
        }

        this.source = source;
        this.combiner = combiner;
        this.offset = offset;
        this.kernel = kernel;
    }

    public interface Combiner<V> {
        V combine(V v1, double weight1, V v2, double weight2);
    }

    private final Sampler<V> source;
    private final Combiner<V> combiner;
    private final double offset;
    private final double[][] kernel;

    @Override
    public V get(Vector point) {
        int s = kernel.length / 2;

        V current = source.get(point);
        for(int i = -s; i <= s; i++) {
            for (int j = -s; j <= s; j++) {
                if(i == 0 && j == 0) continue;

                Vector pp = Vector.add(point, new Vector(i * offset, j * offset));
                V value = source.get(pp);
                double weight = kernel[s + i][s + j];

                double w2;
                if(i == -s && j == -s) {
                    w2 = kernel[s][s];
                } else {
                    w2 = 1.0;
                }

                current = combiner.combine(current, w2, value, weight);
            }
        }

        return current;
    }
}
