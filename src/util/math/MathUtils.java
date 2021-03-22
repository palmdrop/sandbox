package util.math;

import util.vector.ReadVector;
import util.vector.Vector;

import java.util.Random;
import java.util.function.BinaryOperator;
import java.util.stream.LongStream;

public class MathUtils {
    public static double interpolate(double v1, double v2, double v) {
        return MathUtils.map(v, 0, 1, v1, v2);
    }

    public enum BinaryOpMode {
        ADD,
        MULT,
        POW,
        FIRST, // Returns first value in binary operation
        SECOND, // Returns second value in the binary operation
    }
    public static BinaryOperator<Double> getBinaryOperator(BinaryOpMode mode) {
        switch (mode) {
            case ADD: return Double::sum;
            case MULT: return (v1, v2) -> v1 * v2;
            case POW: return Math::pow;
            case FIRST: return (v1, v2) -> v1;
            case SECOND: //intentional fallthrough to default
            default: return (v1, v2) -> v2; //SECOND
        }
    }

    public enum EasingMode {
        EASE_IN,
        EASE_OUT,
        EASE_IN_OUT
    }

    private static final Random random = new Random();

    private MathUtils() {
    }

    public static double random(double[] minMax) {
        if(minMax.length != 2) throw new IllegalArgumentException();
        return random(minMax[0], minMax[1]);
    }

    public static double random(double min,  double max) {
        return random(min, max, random);
    }
    public static double random(double min,  double max, Random random) {
        return unitMap(random.nextDouble(), min, max);
    }

    public static double random(double max) {
        return random(0.0, max);
    }

    public static double randomGaussian(double deviation) {
        return randomGaussian(deviation, random);
    }

    public static double randomGaussian(double deviation, Random random) {
        return random.nextGaussian() * deviation;
    }

    public static double map(double oldValue, double oldMin, double oldMax, double newMin, double newMax) {
        double normalized = (oldValue - oldMin) / (oldMax - oldMin);
        return normalized * (newMax - newMin) + newMin;
    }

    public static double map(double v, double min1, double max1, double min2, double max2, EasingMode easingMode, double pow) {
        v = map(v, min1, max1, 0D, 1D);

        switch(easingMode) {
            case EASE_IN:   v = Math.pow(v, pow); break;
            case EASE_OUT:  v = (1 - Math.pow(1 - v, pow)); break;
            case EASE_IN_OUT: {
                if(v < 0.5) v = (Math.pow(v*2, pow) / 2);
                else        v = (1 - Math.pow((1 - v) * 2, pow) / 2);
            }
            default:
        }

        return map(v, 0D, 1D, min2, max2);
    }

    public static double unitMap(Double normalizedValue, Double min, Double max) {
        return map(normalizedValue, 0D, 1D, min, max);
    }

    public static boolean inRange(Double value, Double min, Double max) {
        return value >= min && value < max;
    }

    public static boolean isPointInCircle(final ReadVector point, final ReadVector center, Double radius) {
        double dX = center.getX() - point.getX();
        double dY = center.getY() - point.getY();
        return (square(dX) + square(dY)) < square(radius);
    }

    public static double square(Double v) {
        return v * v;
    }
    public static double square(final ReadVector a, final ReadVector b) {
        return Vector.dot(a, b);
    }

    public static Vector periodize(final ReadVector v, double width, double height) {
        Vector vc = new Vector(v);
        vc.setX(vc.getX() - width * Math.round(v.getX() / width));
        vc.setY(vc.getY() - height * Math.round(v.getY() / height));
        return vc;
    }

    public static Vector shortestRelativeVector(final ReadVector a, final ReadVector b, double width, double height) {
        // TODO: method correct?????? TEST SOMEHOW?
        Vector v = new Vector();

        double minX = Math.abs(b.getX() - a.getX());
        v.setX(b.getX() - a.getX());

        double dx = b.getX() - (a.getX() - width);
        double x = Math.abs(dx);
        if(x < minX) {
            minX = x;
            v.setX(dx);
        }

        dx = b.getX() - (a.getX() + width);
        x = Math.abs(dx);
        if(x < minX) {
            v.setX(dx);
        }

        double minY = Math.abs(b.getY() - a.getY());
        v.setY(b.getY() - a.getY());

        double dy = b.getY() - (a.getY() - height);
        double y = Math.abs(dy);
        if(y < minY) {
            minY = y;
            v.setY(dy);
        }

        dy = b.getY() - (a.getY() + height);
        y = Math.abs(dy);
        if(y < minY) {
            v.setY(dy);
        }

        return v;
    }

    public static boolean rectangleOverlap(Vector l1, Vector r1, Vector l2, Vector r2) {
        // If one rectangle is on left side of other
        if (l1.getX() > r2.getX() || l2.getX() > r1.getX()) {
            return false;
        }

        // If one rectangle is above other
        if (l1.getY() > r2.getY() || l2.getY() > r1.getY()) {
            return false;
        }

        return true;
    }

    public static boolean rectangleOverlap(Vector p1, double w1, double h1, Vector p2, double w2, double h2) {
        return rectangleOverlap(p1, Vector.add(p1, new Vector(w1, h1)), p2, Vector.add(p2, new Vector(w2, h2)));
    }

    public static double wrapAngleBetween(ReadVector v1, ReadVector v2) {
        double x1 = v1.getX();
        double y1 = v1.getY();
        double x2 = v2.getX();
        double y2 = v2.getY();

        double dot = x1*x2 + y1*y2;      // dot product
        double det = x1*y2 - y1*x2;      // determinant
        return Math.atan2((float)det, (float)dot);
    }

    public static double strictMap(double oldValue, double oldMin, double oldMax, double newMin, double newMax) {
        return limit(map(oldValue, oldMin, oldMax, newMin, newMax), newMin, newMax);
    }

    public static double unitMap(double normalizedValue, double min, double max) {
        return map(normalizedValue, 0, 1, min, max);
    }

    public static boolean inRange(double value, double min, double max) {
        return value >= min && value < max;
    }

    public static double limit(double value, double min, double max) {
        value = Math.max(min, value);
        value = Math.min(max, value);

        return value;
    }

    public static int limit(int value, int min, int max) {
        value = Math.max(min, value);
        value = Math.min(max, value);

        return value;
    }

    public static double lerp(double a, double b, double amount) {
        return a * (1 - amount) + b * amount;
    }

    public static double doubleMod(double v, double mod) {
        if(v < 0) return mod - (Math.abs(v) % mod);
        else      return v % mod;
    }

    public static long factorial(int n) {
        return LongStream.rangeClosed(1, n)
                .reduce(1, (long x, long y) -> x * y);
    }

    public static double triangleArea(ReadVector p1, ReadVector p2, ReadVector p3) {
        return (p1.getX()*p2.getY() + p2.getX()*p3.getY() + p3.getX()*p1.getY() - p1.getX()*p3.getY() - p2.getX()*p1.getY() - p3.getX()*p2.getY())/2;
    }

}
