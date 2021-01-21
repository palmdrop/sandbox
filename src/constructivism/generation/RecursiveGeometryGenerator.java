package constructivism.generation;

import constructivism.ShapeVisitor;
import constructivism.shape.Shape;
import processing.core.PGraphics;

import java.util.List;

public class RecursiveGeometryGenerator {
    public static abstract class Generator implements ShapeVisitor {
        private Shape parent = null;
        private List<Shape> next;

        protected void setNext(List<Shape> next) {
            this.next = next;
        }

        List<Shape> getNext() {
            return next;
        }
        Shape getParent() { return parent; }
    }

    private final PGraphics canvas;
    private final int maxRecursion;
    private final Generator generator;

    public RecursiveGeometryGenerator(PGraphics canvas, Generator generator, int maxRecursion) {
        this.canvas = canvas;
        this.maxRecursion = maxRecursion;
        this.generator = generator;
    }


    public void recursiveRender(Shape base, int step) {
        //base.draw(canvas);
        base.accept(this.generator);
        List<Shape> generated = generator.getNext();

        if(generated == null) return;

        step++;
        if(step >= maxRecursion) return;

        for(Shape shape : generated) {
            generator.parent = base;
            recursiveRender(shape, step);
        }
    }
}
