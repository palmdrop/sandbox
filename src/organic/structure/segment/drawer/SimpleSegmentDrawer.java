package organic.structure.segment.drawer;

import organic.structure.segment.Segment;
import processing.core.PGraphics;
import render.AbstractDrawer;
import util.geometry.Rectangle;
import util.vector.ReadVector;

import java.util.ArrayDeque;
import java.util.Queue;

public class SimpleSegmentDrawer extends AbstractDrawer {
    private final Segment<?> root;

    public SimpleSegmentDrawer(Segment<?> root, Rectangle bounds) {
        super(bounds);
        this.root = root;
    }

    @Override
    public PGraphics draw(PGraphics canvas, double frequency) {
        Queue<Segment<?>> toProcess = new ArrayDeque<>();
        toProcess.add(root);

        while(!toProcess.isEmpty()) {
            Segment<?> current = toProcess.remove();

            for(Segment<?> child : current.children()) {
                drawLine(canvas, current.getPosition(), child.getPosition());
                toProcess.add(child);
            }
        }

        return canvas;
    }

    private void drawLine(PGraphics canvas, ReadVector p1, ReadVector p2) {
        canvas.line((float)p1.getX(), (float)p1.getY(), (float)p2.getX(), (float)p2.getY());
    }

}
