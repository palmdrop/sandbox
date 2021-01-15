package organic.structure.segment.drawer;

import color.colors.Color;
import color.fade.ColorFade;
import organic.structure.segment.Segment;
import processing.core.PGraphics;
import render.AbstractDrawer;
import sampling.countour.Contour;
import sampling.countour.Contours;
import util.geometry.Rectangle;
import util.math.MathUtils;
import util.vector.ReadVector;

public class FadingSegmentDrawer extends AbstractDrawer {
    private final ColorFade colorFade;
    private final double minWidth;
    private final double maxWidth;
    private final Contour widthFade;

    private final Segment<?> root;

    public FadingSegmentDrawer(Segment<?> root, Rectangle bounds, ColorFade colorFade) {
        this(root, bounds, colorFade, -1, -1);
    }

    public FadingSegmentDrawer(Segment<?> root, Rectangle bounds, ColorFade colorFade, double minWidth, double maxWidth) {
        this(root, bounds, colorFade, minWidth, maxWidth, Contours.linear(0, 1));
    }

    public FadingSegmentDrawer(Segment<?> root, Rectangle bounds, ColorFade colorFade, double minWidth, double maxWidth, Contour widthFade) {
        super(bounds);
        this.root = root;
        this.colorFade = colorFade;

        this.minWidth = minWidth;
        this.maxWidth = maxWidth;
        this.widthFade = widthFade;
    }

    private int rootDepth;

    @Override
    public PGraphics draw(PGraphics canvas, double frequency) {
        rootDepth = root.getDepth();

        for(Segment<?> child : root.children()) {
            drawConnection(root, child, canvas, frequency);
        }
        return canvas;
    }

    private void drawConnection(Segment<?> from, Segment<?> to, PGraphics canvas, double frequency) {
        if(bounds.isInside(from.getPosition()) && bounds.isInside(to.getPosition())) {
            int depth = to.getDepth();
            double amount = MathUtils.map(depth, rootDepth, 1, 0, 1);

            double wf = widthFade.get(amount);
            //wf *= MathUtils.random(0.8, 1.2);
            double width = MathUtils.map(wf, 0, 1, maxWidth, minWidth);

            Color color = colorFade.get(wf);
            canvas.stroke(color.toRGB());
            if (width >= 0) canvas.strokeWeight((float) width);

            drawLine(canvas, from.getPosition(), to.getPosition());
        }

        for(Segment<?> child : to.children()) {
            drawConnection(to, child, canvas, frequency);
        }
    }

    private void drawLine(PGraphics canvas, ReadVector p1, ReadVector p2) {
        canvas.line((float)p1.getX(), (float)p1.getY(), (float)p2.getX(), (float)p2.getY());
    }

}
