package sampling.heightMap.modified.pixelated;

import sampling.Sampler;
import util.vector.Vector;
import java.util.Random;

public class DynamicPixelatedSampler<T> implements Sampler<T> {
    public enum Mode {
        RANDOM,
        CORNER,
    }

    private final Sampler<T> source;

    private final double width, height;
    //private final double pow;
    private final double max;
    private final double threshold;

    private final Mode mode;

    private final Sampler<Double> control;

    public DynamicPixelatedSampler(Sampler<T> source, double width, double height, Sampler<Double> control, double threshold, double max, /*double pow,*/ Mode mode) {
        this.source = source;
        this.control = control;

        this.width = width;
        this.height = height;
        //this.pow = pow;
        this.max = max;
        this.threshold = threshold;

        this.mode = mode;

        s = (long) (Math.random() * 10000000);
    }

    private final long s;

    public T calc(double x, double y) {
        double n;
        double w = width;
        double h = height;
        double cx, cy;

        int i = 0;
        while(true) {
            double dx = x % w;
            double dy = y % h;

            cx = x + (x >= 0 ? -dx : 1 - w - dx);
            cy = y + (y >= 0 ? -dy : 1 - h - dy);

            n = control.get(cx, cy);
            i++;

            if(n > threshold && i < max) {
                w /= 2;
                h /= 2;
            } else {
                break;
            }
        }

        switch (mode) {
            case CORNER:
                return source.get(cx, cy);
            case RANDOM:
                long a = (long)(cx * w * 1000);
                long b = (long)(cy * h * 1000);
                long seed = (long)(0.5 * (a+b)*(a+b+1)+b);

                Random r = new Random(seed + s);

                x += cx + r.nextDouble() * w;
                y += cy + r.nextDouble() * h;
                return source.get(x, y);
        }
        return null;
    }

    @Override
    public T get(Vector point) {
        return calc(point.getX(), point.getY());
    }

}
