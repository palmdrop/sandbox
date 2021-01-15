package sampling.heightMap.modified.pixelated;

import sampling.Sampler;
import sampling.heightMap.HeightMaps;
import util.math.MathUtils;
import util.vector.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PixelatedSampler<T> implements Sampler<T> {
    public interface Average<T> {
        T average(List<T> values);
    }

    public enum Mode {
        CORNER,
        AVERAGE,
        RANDOM,
    }

    private final Sampler<T> source;
    private final double minPixelWidth;
    private final double minPixelHeight;
    private final double maxPixelWidth;
    private final double maxPixelHeight;
    private final Mode mode;
    private final int precision;

    private final Average<T> average;
    private final Sampler<Double> control;

    public static PixelatedSampler<Double> pixelatedHeightmap(Sampler<Double> source, double pixelWidth, double pixelHeight, Mode mode, int precision) {
       return pixelatedHeightmap(source, pixelWidth, pixelHeight, pixelWidth, pixelHeight, mode, precision, HeightMaps.constant(1.0));
    }
    public static PixelatedSampler<Double> pixelatedHeightmap(Sampler<Double> source, double minPixelWidth, double minPixelHeight, double maxPixelWidth, double maxPixelHeight, Mode mode, int precision, Sampler<Double> control) {
        return new PixelatedSampler<>(source, minPixelWidth, minPixelHeight, maxPixelWidth, maxPixelHeight, mode, precision, l -> l.stream().mapToDouble(d -> d).average().getAsDouble(), control);
    }

    public PixelatedSampler(Sampler<T> source, double minPixelWidth, double minPixelHeight, double maxPixelWidth, double maxPixelHeight, Mode mode, int precision, Average<T> average, Sampler<Double> control) {
        //TODO: dynamicly change pixel density dependent on map? on source itself?
        this.source = source;
        this.minPixelWidth = minPixelWidth;
        this.minPixelHeight = minPixelHeight;
        this.maxPixelWidth = maxPixelWidth;
        this.maxPixelHeight = maxPixelHeight;
        this.mode = mode;
        this.precision = precision;
        this.average = average;
        this.control = control;

        s = (long) (Math.random() * 10000000);
    }

    private final long s;

    public T calc(double x, double y) {
        double n = control.get(x, y);
        double pixelWidth = MathUtils.map(n, 0, 1, minPixelWidth, maxPixelWidth);
        double pixelHeight = MathUtils.map(n, 0, 1, minPixelHeight, maxPixelHeight);

        double dx = x % pixelWidth;
        double dy = y % pixelHeight;

        double cx = x + (x >= 0 ? -dx : 1 - pixelWidth - dx);
        double cy = y + (y >= 0 ? -dy : 1 - pixelHeight - dy);

        switch (mode) {
            case CORNER:
                return source.get(cx, cy);
            case AVERAGE:
                double ddx = pixelWidth / precision;
                double ddy = pixelHeight / precision;
                List<T> values = new ArrayList<>(precision * precision);
                for(x = cx; x < cx + pixelWidth; x += ddx) for(y = cy; y < cy + pixelHeight; y += ddy) {
                    values.add(source.get(x, y));
                }
                return average.average(values);
            case RANDOM:
                long a = (long)(cx * pixelWidth * 1000);
                long b = (long)(cy * pixelHeight * 1000);
                long seed = (long)(0.5 * (a+b)*(a+b+1)+b);

                Random r = new Random(seed + s);

                x += cx + r.nextDouble() * pixelWidth;
                y += cy + r.nextDouble() * pixelHeight;
                return source.get(x, y);
        }
        return null;
    }

    @Override
    public T get(Vector point) {
        return calc(point.getX(), point.getY());
    }

}
