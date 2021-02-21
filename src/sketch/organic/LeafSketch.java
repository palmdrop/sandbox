package sketch.organic;

import organic.Component;
import organic.leaf.LeafSkeletonGenerator;
import organic.structure.branch.Branch;
import organic.structure.branch.drawer.SimpleBranchDrawer;
import processing.core.PGraphics;
import sketch.Sketch;
import util.geometry.Rectangle;
import util.vector.Vector;

public class LeafSketch implements Sketch {
    private final Rectangle bounds;

    public LeafSketch(Rectangle bounds) {
        this.bounds = bounds;
    }

    @Override
    public PGraphics draw(PGraphics canvas, double frequency) {
        canvas.background(0, 0, 0);

        LeafSkeletonGenerator generator = new LeafSkeletonGenerator(0.02, -0.001, 10, 59, 6, 20, 0.95, Math.PI/3, 0.02);
        Branch<Component> base = generator.generate();

        SimpleBranchDrawer drawer = new SimpleBranchDrawer(new Vector(canvas.width/2, canvas.height - 200), base, bounds);

        canvas.fill(255);
        canvas.stroke(255);
        drawer.draw(canvas);

        return canvas;
    }

    @Override
    public Rectangle getBounds() {
        return null;
    }

    @Override
    public void setBounds(Rectangle bounds) {
    }
}
