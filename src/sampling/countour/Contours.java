package sampling.countour;

import sampling.Sampler;
import sampling.Sampler1D;
import util.math.MathUtils;
import util.vector.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class Contours {
    public static double[] toArray(Sampler1D<Double> contour, int samples) {
        return toArray(contour, 0, 1, samples);
    }

    public static double[] toArray(Sampler1D<Double> contour, double from, double to, int samples) {
        if(samples == 1) return new double[]{1.0};
        double[] arr = new double[samples];
        for(int i = 0; i < samples; i++) {
            double v = MathUtils.map(i, 0, samples - 1, from, to);
            arr[i] = contour.get(v);
        }
        return arr;
    }
    //EASING FUNCTIONS
    public static Contour linear(double min, double max) {
        return easing(min, max, MathUtils.EasingMode.EASE_IN, 1.0);
    }

    public static Contour easing(MathUtils.EasingMode easingMode, double pow) {
       return easing(0, 1, easingMode, pow);
    }

    public static Contour easing(double min, double max, MathUtils.EasingMode easingMode, double pow) {
        return easing(0, 1, min, max, easingMode, pow);
    }
    public static Contour easing(double start, double stop, double min, double max, MathUtils.EasingMode easingMode, double pow) {
        return amount -> {
            amount = MathUtils.limit(amount, start, stop);
            return MathUtils.map(amount, start, stop, min, max, easingMode, pow);
        };
    }

    // INTERPOLATE
    public static Contour simpleInterpolate(double pow, Vector... points) {
        //TODO: make more smart!?!?!?
        if(points.length < 2) throw new IllegalArgumentException();

        List<Contour> contours = new ArrayList<>(points.length - 1);
        for(int i = 0; i < points.length - 1; i++) {
            Vector p1 = points[i];
            Vector p2 = points[i + 1];

            Contour c =
                    easing(p1.getX(), p2.getX(), p1.getY(), p2.getY(), MathUtils.EasingMode.EASE_IN_OUT, pow);
            contours.add(c);
        }

        return amount -> {
            double v = points[0].getX();
            int i;

            for(i = 1; i <= contours.size() && v <= amount; i++) {
                v = points[i].getX();
            }
            return contours.get(i - 2).get(amount);
        };
    }

    //CONSTANT
    public static Contour constant(double value) {
        return amount -> value;
    }

    // FUNCTION
    public static Contour sin(double frequency, double amplitude) {
        return fromFunction(Math::sin, frequency, amplitude);
    }

    public static Contour cos(double frequency, double amplitude) {
        return fromFunction(Math::cos, frequency, amplitude);
    }

    public static Contour saw(double frequency, double amplitude) {
        return fromFunction(v -> v % 1.0, frequency, amplitude);
    }

    // CONVERT
    public static Contour fromSampler1D(Sampler1D<Double> sampler1D) {
        return sampler1D::get;
    }

    public static Contour fromFunction(Function<Double, Double> function) {
        return fromFunction(function, 1.0, 1.0);
    }


    public static Contour fromFunction(Function<Double, Double> function, double frequency, double amplitude) {
        return amount -> amplitude * function.apply(amount * frequency);
    }

    public static Contour fromMap(Sampler<Double> map, double angle) {
        return fromMap(map, angle, 1.0);
    }

    public static Contour fromMap(Sampler<Double> map, double angle, double frequency) {
        return amount -> map.get(Vector.fromAngle(angle).mult(amount * frequency));
    }

    public static Contour fromMap(Sampler<Double> map, Sampler1D<Vector> samplePoints, double frequency) {
        return amount -> map.get(samplePoints.get(frequency * amount));
    }

    public static Sampler1D<Vector> toPositionSampler1D(Sampler1D<Double> x, Sampler1D<Double> y) {
        return amount -> new Vector(x.get(amount), y.get(amount));
    }

}
