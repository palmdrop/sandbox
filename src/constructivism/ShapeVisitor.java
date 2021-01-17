package constructivism;

import constructivism.shape.Ellipse;
import constructivism.shape.Triangle;

public interface ShapeVisitor {
    void visit(Ellipse ellipse);
    void visit(Triangle triangle);
}
