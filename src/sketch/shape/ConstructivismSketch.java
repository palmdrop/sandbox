package sketch.shape;

import color.colors.Color;
import color.colors.Colors;
import organic.generation.points.PointGenerator;
import organic.generation.points.area.HeightMapPointGenerator;
import organic.generation.points.gaussian.GaussianOffsetGenerator;
import processing.core.PGraphics;
import render.AbstractDrawer;
import sampling.heightMap.HeightMap;
import shapes.geometry.Shape;
import shapes.geometry.Triangle;
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
        double maxDeviation = canvas.width / (frequency * 8);
        int baseShapes = 200;
        double shadowOffset = 20;
        double shadowOpacity = 0.95;

        List<PointGenerator> generators = new ArrayList<>(numberOfGenerators);
        for(int i = 0; i < numberOfGenerators; i++) {
            Vector p = basePointGenerator.get();
            double n = base.get(p);
            generators.add(new GaussianOffsetGenerator(p, MathUtils.map(n, 0, 1, minDeviation, maxDeviation)));
        }

        double h = Math.random();
        double hIncrement = 0.001;
        for(int i = 0; i < baseShapes; i++) {
            // Get point generator
            PointGenerator generator = ArrayAndListTools.randomElement(generators);

            // Get triangle corners
            Triangle t = new Triangle(generator.get(), generator.get(), generator.get());

            // Calculate triangle data
            if(t.area < 500) { i--; continue; }

            // Fetch probability
            double n = base.get(t.center);

            // Create fill and shadow colors
            //canvas.point((float)p.getX(), (float)p.getY());
            Color c = Colors.HSB_SPACE.getColor((h + (Math.random() < 0.5 ? 0 : 0.5)) % 1.0, 0.5, Math.random());
            Color shadow = Colors.mult(c, Colors.HSB_SPACE.getColor(1.0, 0.1, 0.1), Colors.HSB_SPACE);

            // calculate shadow characteristics
            double shadowAngle = n * Math.PI * 1;
            //Math.PI/3;
            double sx = Math.cos(shadowAngle) * shadowOffset;
            double sy = Math.sin(shadowAngle) * shadowOffset;

            canvas.noStroke();

            // Render shadow
            canvas.fill(shadow.toRGB(), (float) (255 * shadowOpacity));
            //canvas.triangle((float)(p1.getX() + sx), (float)(p1.getY() + sy), (float)(p2.getX() + sx), (float)(p2.getY() + sy), (float)(p3.getX() + sx), (float)(p3.getY() + sy));
            t.draw(canvas, new Vector(sx, sy));

            // Render triangle
            canvas.fill(c.toRGB());
            t.draw(canvas);
            //canvas.triangle((float)p1.getX(), (float)p1.getY(), (float)p2.getX(), (float)p2.getY(), (float)p3.getX(), (float)p3.getY());

            // Calculate next hue
            h += hIncrement;
            h %= 1.0;


            // Generate poitns
            if(Math.random() > 0.06) continue;
            int points = (int) MathUtils.random(2, 20);
            double dist = Math.sqrt(t.area);
            double angle = Math.random() * 2 * Math.PI;
            double inc = Math.PI * 2 / points;
            for(int j = 0; j < points; j++) {
                Vector p = Vector.fromAngle(angle).mult(dist).add(t.center);
                angle += inc;

                Color pc = Colors.add(c, Colors.HSB_SPACE.getColor(0.5, 0.0, 0.0), Colors.HSB_SPACE);

                //canvas.stroke(shadow.toRGB());
                canvas.stroke(pc.toRGB());
                canvas.strokeWeight(5);

                //Vector p = generator.get();
                canvas.point((float)p.getX(), (float)p.getY());
            }
        }

        canvas.endDraw();
        return canvas;
    }
}
