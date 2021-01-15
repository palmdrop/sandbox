package sampling.heightMap.converter;

import sampling.heightMap.HeightMap;
import util.geometry.Circle;
import util.math.MathUtils;
import util.space.tree.QuadTree;
import util.vector.Vector;

import java.util.List;

public abstract class CircleHeightMap implements HeightMap {
    protected final QuadTree<Circle> circles;
    protected final double maxRadius;
    protected final double fade;
    protected final double fadePow;

    protected CircleHeightMap(QuadTree<Circle> circles, double maxRadius, double fade, double fadePow) {
        this.maxRadius = maxRadius;
        this.fade = fade;
        this.fadePow = fadePow;
        this.circles = circles;
    }

    @Override
    public Double get(double x, double y) {
        Vector p = new Vector(x, y);
        List<Circle> closeCircles = circles.query(p, maxRadius);

        double v = 0.0;
        for(Circle c : closeCircles) {
            double r = c.getRadius();
            double dSq = c.getPos().distSq(p);

            if(dSq < r * r) {
                double d = Math.sqrt(dSq);
                double n = 1 - d / r;
                if(n < fade) {
                    n = MathUtils.map(n, fade, 0.0, 1, 0, MathUtils.EasingMode.EASE_IN, fadePow);
                    v = Math.max(v, n);
                } else {
                    v = 1.0;
                }
            }
        }
        return v;
    }
}
