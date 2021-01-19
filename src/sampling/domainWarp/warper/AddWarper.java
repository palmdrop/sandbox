package sampling.domainWarp.warper;

import processing.core.PApplet;
import sampling.Sampler;
import sampling.heightMap.HeightMap;
import util.math.MathUtils;
import util.vector.Vector;

public interface AddWarper extends Warper {
    AddWarper add(Warper warper);

    default AddWarper rotate(Vector center, double amount, double distPow) {
        return add(p -> {
            Vector dir = Vector.sub(p, center);
            if(dir.isZero()) return p;
            double length = dir.length();
            double angle = -dir.angle() + amount * Math.pow(length, distPow) + PApplet.PI / 2;
            return Vector.fromAngle(angle).mult(length).add(center);
        });
    }

    default AddWarper rotate(Vector center, double angle){
        return add(p -> {
            double rotatedX = Math.cos(angle) * (p.getX() - center.getX()) - Math.sin(angle) * (p.getY() - center.getY()) + center.getX();
            double rotatedY = Math.sin(angle) * (p.getX() - center.getX()) + Math.cos(angle) * (p.getY() - center.getY()) + center.getY();
            return new Vector(rotatedX, rotatedY);
        });
    }

    default AddWarper zoom(Vector center, double amount, double distPow) {
        return add(p -> {
            Vector dir = Vector.sub(p, center);
            if(dir.isZero()) return p;
            double length = dir.length();
            double angle = -dir.angle() + PApplet.PI / 2;
            double newLength = amount * Math.pow(length, distPow);
            return Vector.fromAngle(angle).mult(newLength).add(center);
        });
    }

    interface TrigFunc {
        double apply(double v);
    }

    default AddWarper trigFunc(double offsetAngle, double xMultiplier, double yMultiplier, double amplitude, Warp.TrigFunc trigFunc) {
        Vector dir = Vector.fromAngle(offsetAngle).mult(amplitude);
        return add(
                p -> {
                    double compoundXY = p.getX() * xMultiplier + p.getY() * yMultiplier;
                    double v = trigFunc.apply(compoundXY);
                    return Vector.add(p, Vector.mult(dir, v));
                }
        );
    }
    default AddWarper sin(double offsetAngle, double xMultiplier, double yMultiplier, double amplitude) {
        return trigFunc(offsetAngle, xMultiplier, yMultiplier, amplitude, Math::sin);
    }
    default AddWarper cos(double offsetAngle, double xMultiplier, double yMultiplier, double amplitude) {
        return trigFunc(offsetAngle, xMultiplier, yMultiplier, amplitude, Math::cos);
    }

    default AddWarper frequencyModulation(HeightMap modulation, double min, double max) {
        return add(
                p -> {
                    double frequency = MathUtils.map(modulation.get(p), 0, 1, min, max);
                    return Vector.mult(p, frequency);
                }
        );
    }

    default AddWarper domainWarp(Sampler<Double> rotation, double offset) {
        return domainWarp(rotation, p -> 1.0, offset);
    }

    default AddWarper domainWarp(Sampler<Double> rotation, Sampler<Double> offset, double amount) {
        return domainWarp(rotation, offset, amount, amount);
    }

    default AddWarper domainWarp(Sampler<Double> rotation, Sampler<Double> offset, double min, double max) {
        return add(p -> {
            Vector r = Vector.fromAngle(rotation.get(p) * Math.PI * 2);
            double amount = MathUtils.map(offset.get(p), 0, 1, min, max);
            r.mult(amount);
            return Vector.add(p, r);
        });
    }
}
