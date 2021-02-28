package organic.structure.segment.drawer;

import color.colors.Color;
import color.fade.ColorFade;
import organic.structure.segment.Segment;
import processing.core.PGraphics;
import render.AbstractDrawer;
import sampling.countour.Contour;
import sampling.heightMap.HeightMap;
import util.geometry.Rectangle;
import util.math.MathUtils;
import util.vector.ReadVector;

public class PulsingSegmentDrawer extends AbstractDrawer {
    private final ColorFade colorFade;
    private final HeightMap fadeController;
    private final double fadeDepthInfluence;

    private final double minWidth;
    private final double maxWidth;
    private final Contour depthFade;
    private final HeightMap widthController;
    private final double widthDepthInfluence;

    private final Segment<?> root;


    public PulsingSegmentDrawer(Segment<?> root, Rectangle bounds, ColorFade colorFade, HeightMap fadeController, double fadeDepthInfluence, double minWidth, double maxWidth, HeightMap widthController, double widthDepthInfluence, Contour depthFade) {
        super(bounds);
        this.root = root;
        this.colorFade = colorFade;
        this.fadeController = fadeController;
        this.fadeDepthInfluence = fadeDepthInfluence;

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

    private int drawConnection(Segment<?> from, Segment<?> to, PGraphics canvas) {
        int depth = 0;
        for (Segment<?> child : to.children()) {
            depth = Math.max(depth, drawConnection(to, child, canvas));
        }

        if (bounds.isInside(from.getPosition()) && bounds.isInside(to.getPosition())) {
            double amount = depthFade.get(MathUtils.map(depth, 1, rootDepth, 0, 1));

            double wd = Math.pow(amount, widthDepthInfluence);
            double fd = Math.pow(amount, fadeDepthInfluence);

            double width = MathUtils.map(wd * widthController.get(to.getPosition()), 0, 1.0, minWidth, maxWidth);
            Color color = colorFade.get(fd * fadeController.get(to.getPosition()));

            canvas.stroke(color.toRGB());
            canvas.strokeWeight((float)width);

            drawLine(canvas, from.getPosition(), to.getPosition());
        }

        return 1 + depth;
    }

    private void drawPoint(PGraphics canvas, ReadVector p) {
        canvas.point((float)(p.getX()),(float)(p.getY()));
    }

    private void drawLine(PGraphics canvas, ReadVector p1, ReadVector p2) {
        canvas.line((float)p1.getX(), (float)p1.getY(), (float)p2.getX(), (float)p2.getY());
    }

}
