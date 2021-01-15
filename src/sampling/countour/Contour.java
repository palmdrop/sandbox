package sampling.countour;

import sampling.Sampler1D;
import util.math.MathUtils;

public interface Contour extends Sampler1D<Double> {

    default Contour remap(double min, double max, double newMin, double newMax) {
        return amount -> MathUtils.map(this.get(amount), min, max, newMin, newMax);
    }
    default Contour offset(double x, double y) {
        return amount -> this.get(amount - x) + y;
    }

    default Contour pow(double pow) { return amount -> Math.pow(this.get(amount), pow); }
}
