package shapes.geometry;

import processing.core.PGraphics;
import util.vector.Vector;

public abstract class Shape {
    public final Vector center;
    public final double area;
    public Shape(Vector center, double area) {
        this.center = center;
        this.area = area;
    }

    public void draw(PGraphics canvas) {
        this.draw(canvas, new Vector());
    }

    public abstract void draw(PGraphics canvas, Vector position);
}

