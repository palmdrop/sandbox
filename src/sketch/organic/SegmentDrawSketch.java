package sketch.organic;

import organic.Component;
import organic.structure.segment.Segment;
import organic.structure.segment.SimpleSegment;
import organic.structure.segment.drawer.CircleSegmentDrawer;
import processing.core.PApplet;
import processing.core.PGraphics;
import render.Drawer;
import sketch.Sketch;
import util.geometry.Rectangle;
import util.vector.Vector;

public class SegmentDrawSketch implements Sketch {
    private final Segment<Component> segment;
    private Rectangle bounds;

    public SegmentDrawSketch(Rectangle bounds, PApplet p) {
        this.bounds = bounds;
        segment = new SimpleSegment<>(new Vector(400, 400), new Vector());
        segment.children().add(new SimpleSegment<>(new Vector(600, 600), new Vector()));
        segment.children().get(0).children().add(new SimpleSegment<>(new Vector(500, 3000), new Vector()));
    }

    @Override
    public PGraphics draw(PGraphics canvas, double frequency) {
        canvas.beginDraw();

        canvas.strokeWeight(20);
        canvas.fill(0, 0);
        canvas.stroke(  255, 255);


        Drawer drawer = new CircleSegmentDrawer(segment, bounds, 1, 1, 10);
        drawer.draw(canvas, frequency);

        canvas.endDraw();
        return canvas;
    }

    @Override
    public Rectangle getBounds() {
        return bounds;
    }

    @Override
    public void setBounds(Rectangle bounds) {
        this.bounds = bounds;
    }
}
