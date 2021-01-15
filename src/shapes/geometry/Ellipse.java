package shapes.geometry;

import processing.core.PGraphics;
import util.vector.Vector;

public class Ellipse extends Shape {
    public final double width, height;

    private Ellipse(Vector position, double width, double height) {
        super(position, Math.PI * width * height / 4);
        this.width = width;
        this.height = height;
    }

    @Override
    public void draw(PGraphics canvas, Vector position) {
        canvas.ellipse((float)position.getX(), (float)position.getY(), (float)width, (float)height);
    }
}
