package sketch.constructivism;

import color.colors.Color;
import color.colors.Colors;
import constructivism.generation.RecursiveGeometryGenerator;
import constructivism.shape.Ellipse;
import constructivism.shape.Shape;
import constructivism.shape.Triangle;
import organic.generation.points.PointGenerator;
import organic.generation.points.area.HeightMapPointGenerator;
import organic.generation.points.gaussian.GaussianOffsetGenerator;
import processing.core.PGraphics;
import render.AbstractDrawer;
import sampling.heightMap.HeightMap;
import sketch.Sketch;
import util.ArrayAndListTools;
import util.geometry.Rectangle;
import util.math.MathUtils;
import util.noise.generator.GNoise;
import util.vector.Vector;

import java.util.ArrayList;
import java.util.List;


public class ConstructivismSketch extends AbstractDrawer implements Sketch {

    public ConstructivismSketch(Rectangle bounds) {
        super(bounds);
    }

    @Override
    public PGraphics draw(PGraphics canvas, double frequency) {
        return draw(canvas, frequency, false);
    }

    public PGraphics draw(PGraphics canvas, double frequency, boolean superSampling) {
        canvas.beginDraw();
        canvas.background(Colors.BLACK);


        HeightMap base = GNoise.simplexNoise(0.005, 1.0, 2.0);
        PointGenerator basePointGenerator = new HeightMapPointGenerator(base, (int)bounds.width, (int)bounds.height);

        int numberOfGenerators = 300;
        double minDeviation = canvas.width / (frequency * 80);
        double maxDeviation = canvas.width / (frequency * 10);
        int baseShapes = 500;
        double shadowOffset = 20;
        double shadowOpacity = 0.97;

        List<PointGenerator> generators = new ArrayList<>(numberOfGenerators);
        for(int i = 0; i < numberOfGenerators; i++) {
            Vector p = basePointGenerator.get();
            double n = base.get(p);
            generators.add(new GaussianOffsetGenerator(p, MathUtils.map(n, 0, 1, minDeviation, maxDeviation)));
        }

        canvas.fill(Colors.WHITE);
        canvas.noStroke();

        RecursiveGeometryGenerator recursiveGeometryGenerator = new RecursiveGeometryGenerator(
                canvas,
                new RecursiveGeometryGenerator.Generator() {
                    @Override
                    public void visit(Ellipse ellipse) {
                        ellipse.draw(canvas);

                        List<Shape> next = new ArrayList<>(3);
                        for(int i = 0; i < 3; i++) {
                            Vector p = Vector.add(ellipse.center, Vector.randomWithLength(20));
                            next.add(new Ellipse(p, ellipse.width/2, ellipse.height/2));
                        }

                        setNext(next);
                    }

                    @Override
                    public void visit(Triangle triangle) {
                        // Fetch probability
                        double n = base.get(triangle.center);

                        // Create fill and shadow colors
                        Color c = Colors.HSB_SPACE.getColor((n + (Math.random() < 0.5 ? 0 : 0.5)) % 1.0, 0.2, MathUtils.randomGaussian(1.0) + 0.7);

                        // Draw shadow
                        canvas.fill(0, (float) (255.0f * shadowOpacity));
                        triangle.draw(canvas, new Vector(shadowOffset, shadowOffset));

                        // Draw triangle
                        canvas.fill(c.toRGB());
                        triangle.draw(canvas);

                        // Generate next
                        List<Shape> next = new ArrayList<>(3);
                        for(int i = 0; i < 3; i++) {
                            Vector p =
                                    triangle.center;
                            double s = Math.random() * 20;
                            next.add(new Ellipse(p, s, s));
                        }

                        setNext(next);
                    }
                },
                3
        );

        /*List<Shape> shapes = new ArrayList<>();
        shapes.add(new Triangle(new Vector(100, 100), new Vector(200, 200), new Vector(250, 400)));
        shapes.add(new Ellipse(new Vector(500, 500), 30, 30));

        for(Shape s : shapes) {
            recursiveGeometryGenerator.recursiveRender(s, 0);
        }*/

        for(int i = 0; i < baseShapes; i++) {
            PointGenerator generator = ArrayAndListTools.randomElement(generators);

            // Get triangle corners
            Triangle t = new Triangle(generator.get(), generator.get(), generator.get());

            // Calculate triangle data
            if(t.area < 500) { i--; continue; }

            recursiveGeometryGenerator.recursiveRender(t, 0);
        }

        canvas.endDraw();
        return canvas;
    }
}
