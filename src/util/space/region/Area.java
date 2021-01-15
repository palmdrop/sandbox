package util.space.region;

import util.vector.ReadVector;
import util.vector.StaticVector;

public class Area implements Region {
    public final Range xRange, yRange;
    public final double size;

    public Area(double x, double y, double width, double height) {
        this(new Range(x, x + width), new Range(y ,y + height));
    }

    public Area(final Range xRange, final Range yRange) {
        this.xRange = xRange;
        this.yRange = yRange;
        size = xRange.length * yRange.length;
    }

    public static Area build(double x, double y, double width, double height) {
        return new Area(x, y, width, height);
    }

    public static Area build(final Range xRange, final Range yRange) {
        return new Area(xRange, yRange);
    }

    public static Area build(double width, double height) {
        return new Area(0, 0, width, height);
    }

    public ReadVector interpolate(double dx, double dy) {
        double x = xRange.interpolate(dx);
        double y = yRange.interpolate(dy);
        return new StaticVector(x, y);
    }

    public ReadVector randomPointInArea() {
        double x = xRange.randomInRange();
        double y = yRange.randomInRange();
        return new StaticVector(x, y);
    }

    @Override
    public boolean isInside(double x, double y) {
        return xRange.isInRange(x) && yRange.isInRange(y);
    }
}
