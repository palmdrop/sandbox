package organic.structure.branch.drawer;

import organic.structure.branch.Branch;
import processing.core.PGraphics;
import render.AbstractDrawer;
import util.geometry.Rectangle;
import util.vector.ReadVector;
import util.vector.Vector;

public class SimpleBranchDrawer extends AbstractDrawer {
    private final ReadVector origin;
    private final Branch<?> root;

    public SimpleBranchDrawer(ReadVector origin, Branch<?> root, Rectangle bounds) {
        super(bounds);
        this.origin = origin;
        this.root = root;
    }

    @Override
    public PGraphics draw(PGraphics canvas, double frequency) {
        drawConnection(origin, 0, root, canvas);
        return canvas;
    }

    private void drawConnection(ReadVector from, double baseAngle, Branch<?> branch, PGraphics canvas) {
        double angle = baseAngle + branch.getAngle();

        ReadVector to = Vector.add(from, Vector.fromAngle(angle).mult(branch.getLength()));
        drawLine(canvas, from, to);

        for(Branch<?> child : branch.children()) {
            drawConnection(to, angle, child, canvas);
        }
    }

    private void drawLine(PGraphics canvas, ReadVector p1, ReadVector p2) {
        canvas.line((float)p1.getX(), (float)p1.getY(), (float)p2.getX(), (float)p2.getY());
    }
}
