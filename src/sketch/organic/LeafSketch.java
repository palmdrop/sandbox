package sketch.organic;

import processing.core.PGraphics;
import sketch.Sketch;
import util.geometry.Rectangle;

public class LeafSketch implements Sketch {
    private final Rectangle bounds;

    public LeafSketch(Rectangle bounds) {
        this.bounds = bounds;
    }

    @Override
    public PGraphics draw(PGraphics canvas, double frequency) {
        return null;
    }

    @Override
    public Rectangle getBounds() {
        return null;
    }

    @Override
    public void setBounds(Rectangle bounds) {

    }
}
