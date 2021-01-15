package util.space.region;

import util.math.MathUtils;

public class Range {
    public final double min, max;
    public final double length;

    public Range() {
        min = max = 0;
        length = 0;
    }

    public Range(double min, double max) {
        if(max < min) throw new IllegalArgumentException("max < min");
        this.min = min;
        this.max = max;

        length = max - min;
    }

    public static Range build(double v) {
        return build(v, v);
    }

    public static Range build(double v1, double v2) {
        double min = Math.min(v1, v2);
        double max = Math.max(v1, v2);
        return new Range(min, max);
    }

    public double interpolate(double d) {
        return MathUtils.map(d, 0D, 1D, min, max);
    }
    public double randomInRange() { return MathUtils.random(min, max); }
    public boolean isInRange(double d) { return min <= d && d < max; }
}
