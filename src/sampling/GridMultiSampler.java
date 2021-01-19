package sampling;

import util.vector.Vector;

import java.util.List;

public class GridMultiSampler<V> implements Sampler<V> {
    private final int rows;
    private final double xStep, yStep;
    private final List<Sampler<V>> samplers;

    public GridMultiSampler(int rows, int columns, int width, int height, List<Sampler<V>> samplers) {
        if(rows < 1 || columns < 1 || width < 0 || height < 0 || samplers == null || samplers.size() == 0) {
            throw new IllegalArgumentException();
        }

        this.rows = rows;
        this.samplers = samplers;

        xStep = (double)width / columns;
        yStep = (double)height / rows;
    }

    @Override
    public V get(Vector point) {
        int ix = (int) (point.getX() / xStep);
        int iy = (int) (point.getY() / yStep);

        int index = (ix + iy * rows) % samplers.size();
        Sampler<V> sampler = samplers.get(index);

        double sx = point.getX() % xStep;
        double sy = point.getY() % yStep;

        return sampler.get(sx, sy);
    }
}
