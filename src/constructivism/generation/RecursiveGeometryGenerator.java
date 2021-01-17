package constructivism.generation;

import constructivism.ShapeVisitor;
import constructivism.shape.Ellipse;
import constructivism.shape.Triangle;
import processing.core.PGraphics;
import render.AbstractDrawer;
import constructivism.shape.Shape;
import util.geometry.Rectangle;

import java.util.HashMap;
import java.util.List;

public class RecursiveGeometryGenerator {
    public static abstract class Generator implements ShapeVisitor {
        private List<Shape> next;

        protected void setNext(List<Shape> next) {
            this.next = next;
        }

        List<Shape> getNext() {
            return next;
        }
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
        base.draw(canvas);

        base.accept(this.generator);
        List<Shape> generated = generator.getNext();

        if(generated == null) return;

        step++;
        if(step >= maxRecursion) return;

        for(Shape shape : generated) {
            recursiveRender(shape, step);
        }
    }
}
