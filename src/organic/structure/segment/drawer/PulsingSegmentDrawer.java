package organic.structure.segment.drawer;

import color.colors.Color;
import color.fade.ColorFade;
import organic.structure.segment.Segment;
import processing.core.PGraphics;
import render.AbstractDrawer;
import sampling.countour.Contour;
import sampling.countour.Contours;
import sampling.heightMap.HeightMap;
import util.geometry.Rectangle;
import util.math.MathUtils;
import util.vector.ReadVector;
import util.vector.Vector;

public class PulsingSegmentDrawer extends AbstractDrawer {
    private final ColorFade colorFade;
    private final HeightMap controller;

    private final double minWidth;
    private final double maxWidth;
    private final Contour widthFade;

    private final Segment<?> root;


    public PulsingSegmentDrawer(Segment<?> root, Rectangle bounds, ColorFade colorFade, double minWidth, double maxWidth, Contour widthFade, HeightMap controller) {
        super(bounds);
        this.root = root;
        this.colorFade = colorFade;
        this.controller = controller;

        this.minWidth = minWidth;
        this.maxWidth = maxWidth;
        this.widthFade = widthFade;
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
            //TODO querying depth must traverse large portion of tree... draw depth first and save depth as arugment!!!!
            double amount = MathUtils.map(depth, rootDepth, 1, 1, 0);
            double wf = widthFade.get(amount);

            double width = MathUtils.map(wf * controller.get(to.getPosition()), 0, 1.0, minWidth, maxWidth);
            Color color = colorFade.get(wf * controller.get(to.getPosition()));

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
