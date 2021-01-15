package organic.leaf.shape;

import sampling.countour.Contour;
import sampling.countour.Contours;
import util.vector.Vector;

public class SimpleLeafShape implements LeafShape {
    private final Contour left, right;

    public SimpleLeafShape(Contour shape) {
        this(shape, shape);
    }

    public SimpleLeafShape(Contour left, Contour right) {
        this.left = left;
        this.right = right;
    }

    public static SimpleLeafShape generate(double smoothness, Vector... edgePoints) {
        Vector bottom = new Vector(0, 0);
        Vector top = new Vector(1, 0);

        Vector[] points = new Vector[2 + edgePoints.length];
        points[0] = bottom;
        System.arraycopy(edgePoints, 0, points, 1, edgePoints.length + 2 - 1);
        points[points.length - 1] = top;

        Contour c = Contours.simpleInterpolate(smoothness, points);

        return new SimpleLeafShape(c);
    }

    @Override
    public double getLeft(double value) {
        return left.get(value);
    }

    @Override
    public double getRight(double value) {
        return right.get(value);
    }
}
