package shapes.generation;

import processing.core.PGraphics;
import render.AbstractDrawer;
import shapes.geometry.Shape;
import util.geometry.Rectangle;

import java.util.List;

public class RecursiveGeometryGenerator extends AbstractDrawer {
    public interface GeneratorRule {
        <T extends Shape> List<? extends Shape> generate(PGraphics canvas, T base, int step);
    }

    private final int maxRecursion;

    public RecursiveGeometryGenerator(Rectangle bounds, int maxRecursion) {
        super(bounds);
        this.maxRecursion = maxRecursion;
        //TODO define rules for each geometry?
        //TODO implement visitor pattern for each type of geometry!?
    }


    private void recursiveRender(PGraphics canvas, Shape base, GeneratorRule generator, int step) {
        List<? extends Shape> generated = generator.generate(canvas, base, step);
        if(generated == null) return;

        step++;
        if(step >= maxRecursion) return;

        for(Shape shape : generated) {
            recursiveRender(canvas, shape, generator, step);
        }
    }

    @Override
    public PGraphics draw(PGraphics canvas, double frequency) {




        return null;
    }
}
