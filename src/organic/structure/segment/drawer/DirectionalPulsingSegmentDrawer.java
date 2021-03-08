package organic.structure.segment.drawer;

import color.colors.Colors;
import organic.structure.segment.Segment;
import processing.core.PGraphics;
import render.AbstractDrawer;
import sampling.countour.Contour;
import sampling.heightMap.HeightMap;
import util.geometry.Rectangle;
import util.math.MathUtils;
import util.vector.ReadVector;
import util.vector.Vector;

public class DirectionalPulsingSegmentDrawer extends AbstractDrawer {
    private final HeightMap briController;
    private final double briDepthInfluence;

    private final double minWidth;
    private final double maxWidth;
    private final Contour depthFade;
    private final HeightMap widthController;
    private final double widthDepthInfluence;

    private final Segment<?> root;


    public DirectionalPulsingSegmentDrawer(Segment<?> root, Rectangle bounds, HeightMap briController, double briDepthInfluence, double minWidth, double maxWidth, HeightMap widthController, double widthDepthInfluence, Contour depthFade) {
        super(bounds);
        this.root = root;
        this.briController = briController;
        this.briDepthInfluence = briDepthInfluence;

        this.minWidth = minWidth;
        this.maxWidth = maxWidth;
        this.depthFade = depthFade;
        this.widthController = widthController;
        this.widthDepthInfluence = widthDepthInfluence;
    }

    private int rootDepth;

    @Override
    public PGraphics draw(PGraphics canvas, double frequency) {
        rootDepth = root.getDepth();

        for (Segment<?> child : root.children()) {
            drawConnection(root, child, canvas);
        }
        return canvas;
    }

    private double getHue(ReadVector p1, ReadVector p2) {
        return Vector.sub(p2, p1).angle() / (2 * Math.PI);
                //Vector.angleBetween(p1, p2);
    }

    private int drawConnection(Segment<?> from, Segment<?> to, PGraphics canvas) {
        int depth = 0;
        for (Segment<?> child : to.children()) {
            depth = Math.max(depth, drawConnection(to, child, canvas));
        }

        if (bounds.isInside(from.getPosition()) && bounds.isInside(to.getPosition())) {
            double amount = depthFade.get(MathUtils.map(depth, 1, rootDepth, 0, 1));

            double wd = Math.pow(amount, widthDepthInfluence);
            double fd = Math.pow(amount, briDepthInfluence);

            double width = MathUtils.map(wd * widthController.get(to.getPosition()), 0, 1.0, minWidth, maxWidth);

            //TODO: hue will be converted to a 0-255 int, we can have more precision if sat is also used!
            double hue = getHue(from.getPosition(), to.getPosition());
            double sat = 1.0;
            double bri = fd * briController.get(from.getPosition());

            canvas.stroke(Colors.HSB_SPACE.getRGB(hue, sat, bri));
            canvas.strokeWeight((float)width);

            drawLine(canvas, from.getPosition(), to.getPosition());
        }

        return 1 + depth;
    }

    private void drawLine(PGraphics canvas, ReadVector p1, ReadVector p2) {
        canvas.line((float)p1.getX(), (float)p1.getY(), (float)p2.getX(), (float)p2.getY());
    }

}
