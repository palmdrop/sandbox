package constructivism.shape;

import constructivism.ShapeVisitor;
import processing.core.PGraphics;
import util.math.MathUtils;
import util.vector.Vector;

public class Triangle extends Shape {
        public final Vector p1, p2, p3;
        public Triangle(Vector p1, Vector p2, Vector p3) {
            super(Vector.add(p1, p2).add(p3).div((3)), MathUtils.triangleArea(p1, p2, p3));
            this.p1 = p1;
            this.p2 = p2;
            this.p3 = p3;
        }

        @Override
        public void draw(PGraphics canvas, Vector position) {
            canvas.triangle(
                    (float)(p1.getX() + position.getX()),
                    (float)(p1.getY() + position.getY()),

                    (float)(p2.getX() + position.getX()),
                    (float)(p2.getY() + position.getY()),

                    (float)(p3.getX() + position.getX()),
                    (float)(p3.getY() + position.getY()));
        }

    @Override
    public void accept(ShapeVisitor visitor) {
        visitor.visit(this);
    }
}

