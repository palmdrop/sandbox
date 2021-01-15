package organic.structure.segment.drawer;

import organic.structure.segment.Segment;
import processing.core.PGraphics;
import render.AbstractDrawer;
import util.geometry.Rectangle;
import util.math.MathUtils;
import util.vector.ReadVector;
import util.vector.Vector;

import java.util.ArrayDeque;
import java.util.Queue;

public class CircleSegmentDrawer extends AbstractDrawer {
    private final Segment<?> root;
    private final double lengthPow;
    private final double radius;
    private final double gaussianOffset;

    public CircleSegmentDrawer(Segment<?> root, Rectangle bounds, double lengthPow, double radius, double gaussianOffset) {
        super(bounds);
        this.root = root;
        this.lengthPow = lengthPow;
        this.radius = radius;
        this.gaussianOffset = gaussianOffset;
    }

    @Override
    public PGraphics draw(PGraphics canvas, double frequency) {
        Queue<Segment<?>> toProcess = new ArrayDeque<>();
        toProcess.add(root);

        while(!toProcess.isEmpty()) {
            Segment<?> current = toProcess.remove();

            for(Segment<?> child : current.children()) {
                double r;
                if(lengthPow != 0) {
                    double length = Vector.dist(current.getPosition(), child.getPosition());
                    double l = Math.pow(length, lengthPow);
                    r = l * radius;
                } else {
                    r = radius;
                }

                Vector pos = Vector.add(child.getPosition(),
                    Vector.randomWithLength(MathUtils.randomGaussian(gaussianOffset)));

                drawCircle(canvas, pos, r);
                toProcess.add(child);
            }
        }

        return canvas;
    }

    private void drawCircle(PGraphics canvas, ReadVector p1, double radius) {
        canvas.ellipse((float)p1.getX(), (float)p1.getY(), (float)(2*radius), (float)(2*radius));
    }

}
