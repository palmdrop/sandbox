package sampling.heightMap;

import sampling.Sampler;
import util.geometry.Circle;
import util.geometry.Rectangle;
import util.math.MathUtils;
import util.vector.ReadVector;
import util.vector.Vector;

import java.util.Random;

public class HeightMaps {
    public interface FunctionOneArg {
        double apply(double v);
    }
    public interface FunctionTwoArgs {
        double apply(double x, double y);
    }

    private static boolean validRange(double v) {
        return v >= 0 && v <= 1.0;
    }

    public static HeightMap fromFunction(double xMultiplier, double yMultiplier, double pow, FunctionOneArg func) {
        return (x, y) -> Math.pow(func.apply(xMultiplier * x + yMultiplier * y), pow);
    }

    public static HeightMap fromFunction(double xMultiplier, double yMultiplier, double pow, FunctionTwoArgs func) {
        return (x, y) -> Math.pow(func.apply(xMultiplier * x, yMultiplier * y), pow);
    }

    public static HeightMap constant(double value) {
        return (x, y) -> value;
    }

    public static HeightMap sin(double xMultiplier, double yMultiplier, double offset, double pow) {
        return fromFunction(xMultiplier, yMultiplier, pow, v -> MathUtils.map(Math.sin(v + offset), -1, 1, 0, 1));
    }

    public static HeightMap cos(double xMultiplier, double yMultiplier, double offset, double pow) {
        return fromFunction(xMultiplier, yMultiplier, pow, v -> MathUtils.map(Math.cos(v + offset), -1, 1, 0, 1));
    }

    public static HeightMap tan(double xMultiplier, double yMultiplier, double offset, double pow) {
        return fromFunction(xMultiplier, yMultiplier, pow, v -> {
            v = MathUtils.map(Math.tan(v + offset), -1, 1, 0, 1);
            if(Double.isNaN(v)) {
                v = Double.MAX_VALUE;
            }
            return v;
        });
    }

    public static HeightMap random(double min, double max) {
        if(!validRange(min) || !validRange(max)) throw new IllegalArgumentException();
        return (x, y) -> MathUtils.map(Math.random(), 0, 1, min, max);
    }

    public static HeightMap randomGaussian(double mean, double deviation) {
        Random r = new Random();
        return (x, y) -> MathUtils.limit(r.nextGaussian() * deviation + mean, 0, 1);
    }

    public static HeightMap grid(double xMultiplier, double yMultiplier, double rotation, double slant, double softness, Vector offset) {
        Vector v1 = Vector.fromAngle(rotation);
        Vector v2 = Vector.fromAngle(rotation + slant);

        HeightMap hmCos = cos(xMultiplier * v2.getY(), xMultiplier * -v2.getX(), offset.getY(), softness);
        HeightMap hmSin = sin(yMultiplier * v1.getX(), yMultiplier * v1.getY(), offset.getX(), softness);

        return HeightMaps.mult(hmCos, hmSin, 0.5);
    }

    public static HeightMap saw(double xMultiplier, double yMultiplier, double offset, double pow) {
        return fromFunction(xMultiplier, yMultiplier, pow, v -> MathUtils.doubleMod(v + offset, 1.0));
    }

    public static HeightMap rect(Rectangle rect) {
        return fromFunction(1.0, 1.0, 1.0, (x, y) -> rect.isInside(new Vector(x, y)) ? 1 : 0);
    }

    public static HeightMap rects(double width, double height, double xMarginal, double yMarginal, ReadVector offset) {
        return fromFunction(1.0, 1.0, 1.0, (x, y) -> {
            x = MathUtils.doubleMod(x + offset.getX(), width + xMarginal);
            y = MathUtils.doubleMod(y + offset.getY(), height + yMarginal);
            return x < width && y < width ? 1.0 : 0.0;
        });
    }

    public static HeightMap circle(Circle circle) {
        return fromFunction(1.0, 1.0, 1.0, (x, y) -> circle.isInside(new Vector(x, y)) ? 1 : 0);
    }

    public static HeightMap circle(Circle circle, double fade, MathUtils.EasingMode easingMode) {
        return fromFunction(1.0, 1.0, 1.0, (x, y) -> {
            double distSq = circle.getPos().distSq(new Vector(x, y));

            if(distSq > circle.getRadius() * circle.getRadius()) {
                return 0;
            } else if(fade == 0)  {
                return 1.0;
            } else {
                return MathUtils.map(distSq, 0,  circle.getRadius() * circle.getRadius(), 1, 0, easingMode, fade);
            }
        });
    }

    public static HeightMap circles(double width, double height, double xMarginal, double yMarginal, ReadVector offset, double fade) {
        return circles(width, height, xMarginal, yMarginal, offset, fade, MathUtils.EasingMode.EASE_OUT);
    }
    public static HeightMap circles(double width, double height, double xMarginal, double yMarginal, ReadVector offset, double fade, MathUtils.EasingMode easingMode) {
        Vector center = new Vector(width/2, height/2);
        double radius = Math.min(width/2, height/2);

        return fromFunction(1.0, 1.0, 1.0, (x, y) -> {
            x = MathUtils.doubleMod(x + offset.getX(), width + xMarginal);
            y = MathUtils.doubleMod(y + offset.getY(), height + yMarginal);

            double distSq = center.distSq(new Vector(x, y));

            if(distSq > radius * radius) {
                return 0;
            } else if(fade == 0)  {
                return 1.0;
            } else {
                return MathUtils.map(distSq, 0, radius * radius, 1, 0, easingMode, fade);
            }
        });
    }

    public static HeightMap checkers(double xWidth, double yWidth, double low, double high) {
        return (x, y) -> {
            x = MathUtils.doubleMod(x, xWidth * 2);
            y = MathUtils.doubleMod(y, yWidth * 2);

            double mx = x % (2*xWidth);
            double my = y % (2*yWidth);

            int s1 = mx < xWidth ? 0 : 1;
            int s2 = my < yWidth ? 0 : 1;

            if((s1 + s2) % 2 == 1) {
                return low;
            } else {
                return high;
            }
        };
    }

    public static HeightMap fade(Vector source, double distPow) {
        return fade(source, 0.0, 1.0, distPow);
    }

    public static HeightMap fade(Vector source, double from, double to, double distPow) {
        if(!validRange(from) || !validRange(to)) throw new IllegalArgumentException();
        return (x, y) -> {
            double divFactor = source.distSq(new Vector(x, y));
            if(distPow != 2) {
                divFactor = Math.pow(divFactor, distPow / 2);
            }
            divFactor = Math.max(divFactor, 1.0);
            return MathUtils.map(1.0 / divFactor, 0, 1, from, to);
        };
    }

    public static HeightMap fade(double xMultiplier, double yMultiplier, double offset, double left, double right, double size, double pow) {
        double mid = (left + right) / 2;

        return fromFunction(xMultiplier, yMultiplier, 1.0, (x, y) -> {
            double v = x + y + offset;
            if(v <= -size) return left;
            if(v >=  size) return right;

            if(v < 0) {
                return MathUtils.map(v, 0, size, mid, right, MathUtils.EasingMode.EASE_IN_OUT, pow);
            } else {
                return MathUtils.map(v, 0, -size, mid, left, MathUtils.EasingMode.EASE_IN_OUT, pow);
            }
        });
    }

    public static HeightMap clip(HeightMap hm, double limit, double standard, boolean above) {
        if(above) {
            return (x, y) -> hm.get(x, y) >= limit ? hm.get(x, y) : standard;
        } else {
            return (x, y) -> hm.get(x, y) < limit ? hm.get(x, y) : standard;
        }
    }

    // Heightmap operations
    public static HeightMap remap(HeightMap hm, double oldMin, double oldMax, double newMin, double newMax) {
        if(!validRange(newMin) || !validRange(newMax)) throw new IllegalArgumentException();
        return (x, y) -> MathUtils.map(hm.get(x, y), oldMin, oldMax, newMin, newMax);
    }

    public static HeightMap offset(HeightMap hm, double xOffset, double yOffset) {
        return (x, y) -> hm.get(x + xOffset, y + yOffset);
    }

    public static HeightMap limit(HeightMap hm, double min, double max) {
        if(!validRange(min) || !validRange(max)) throw new IllegalArgumentException();
        return (x, y) -> MathUtils.limit(hm.get(x, y), min, max);
    }

    public static HeightMap mult(Sampler<Double> hm1, Sampler<Double> hm2, double weight) {
        if(!validRange(weight)) throw new IllegalArgumentException();

        return (x, y) -> {
            double v1 = hm1.get(x, y);
            double v2 = hm2.get(x, y);

            double v1Pow = weight < 0.5 ? 1 : MathUtils.map(weight, 0.5, 1, 1, 0);
            double v2Pow = weight > 0.5 ? 1 : MathUtils.map(weight, 0, 0.5, 0, 1);

            return Math.pow(v1, v1Pow) * Math.pow(v2, v2Pow);
        };
    }

    public static HeightMap mult(Sampler<Double> hm, double value) {
        return HeightMaps.mult(hm, HeightMaps.constant(value), 0.5);
    }

    public static HeightMap stretch(HeightMap hm, double xStretch, double yStretch) {
        return (x, y) -> hm.get(x*xStretch, y*yStretch);
    }

    public static HeightMap thresholdReverse(Sampler<Double> hm, double threshold, boolean clip, boolean normalize) {
        return (x, y) -> {
            double v = hm.get(x, y);

            while(v > threshold || v < 0) {
                if(v > threshold) {
                    v = threshold - (v - threshold);
                }
                if(clip && v < 0) {
                    v = 0;
                } else {
                    v = -v;
                }
            }

            if(normalize) {
                return MathUtils.map(v, 0, threshold, 0, 1);
            } else {
                return v;
            }
        };
    }

    // Average
    public static HeightMap average(HeightMap hm1, HeightMap hm2) {
        return average(hm1, hm2, 0.5);
    }

    public static HeightMap average(HeightMap hm1, HeightMap hm2, double weight) {
        if(!validRange(weight)) throw new IllegalArgumentException();
        return (x, y) -> {
            double v1 = hm1.get(x, y);
            double v2 = hm2.get(x, y);
            return (1 - weight) * v1 + weight * v2;
        };
    }

    public static HeightMap average(HeightMap first, HeightMap... rest) {
        double div = 1 + rest.length;
        return (x, y) -> {
            double sum = first.get(x, y);
            for(HeightMap hm : rest) {
                sum += hm.get(x, y);
            }
            return sum / div;
        };
    }

    public static HeightMap add(Sampler<Double> hm1, Sampler<Double> hm2) {
        return (x, y) -> MathUtils.limit(hm1.get(x, y) + hm2.get(x, y), 0, 1);
    }

    public static HeightMap add(HeightMap first, HeightMap... rest) {
        return (x, y) -> {
            double sum = first.get(x, y);
            for(HeightMap hm : rest) {
                sum += hm.get(x, y);
            }
            return MathUtils.limit(sum, 0, 1.0);
        };
    }

    public static HeightMap sub(Sampler<Double> hm1, Sampler<Double> hm2) {
        return add(hm1, mult(hm2, -1));
    }

    public static HeightMap pow(Sampler<Double> baseMap, Sampler<Double> power) {
        return (x, y) -> Math.pow(baseMap.get(x, y), power.get(x, y));
    }

    public static HeightMap approach(HeightMap baseMap, double value, HeightMap amount) {
        return HeightMaps.approach(baseMap, HeightMaps.constant(value), amount);
    }

    public static HeightMap approach(HeightMap baseMap, HeightMap goal, HeightMap amount) {
        return (x,y) -> MathUtils.map(amount.get(x, y), 0, 1, baseMap.get(x, y), goal.get(x, y));
    }

    public static HeightMap limitTo(HeightMap baseMap, HeightMap amount) {
        return HeightMaps.approach(baseMap, 1.0, amount);
    }
}
