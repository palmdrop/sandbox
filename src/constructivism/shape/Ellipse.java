package constructivism.shape;

import constructivism.ShapeVisitor;
import processing.core.PGraphics;
import util.vector.Vector;

public class Ellipse extends Shape {
    public final double width, height;

    public Ellipse(Vector position, double width, double height) {
        super(position, Math.PI * width * height / 4);
        this.width = width;
        this.height = height;
    }

    @Override
    public void draw(PGraphics canvas, Vector position) {
        canvas.ellipse((float)(center.getX() + position.getX()), (float)(position.getY() + center.getY()), (float)width, (float)height);
    }

    @Override
    public void accept(ShapeVisitor visitor) {
        visitor.visit(this);
    }
}
