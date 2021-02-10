package sketch.organic;

import organic.Component;
import organic.generation.points.PointGenerator;
import organic.generation.points.area.HeightMapPointGenerator;
import organic.generation.points.gaussian.GaussianOffsetGenerator;
import organic.generation.points.limited.SetPointGenerator;
import organic.generation.segments.SpaceFillTree;
import organic.structure.segment.Segment;
import organic.structure.segment.Segments;
import organic.structure.segment.drawer.SimpleSegmentDrawer;
import processing.core.PGraphics;
import render.Drawer;
import sampling.heightMap.HeightMap;
import sampling.heightMap.HeightMaps;
import sketch.Sketch;
import util.geometry.Rectangle;
import util.math.MathUtils;
import util.noise.generator.GNoise;
import util.vector.Vector;

import java.util.List;

public class CthulhuSketch implements Sketch {
    // Bounds
    private Rectangle bounds;

    // Spawning
    private HeightMap pointMap;
    private PointGenerator spawningBehavior;
    private HeightMap settingsMap;

    // Settings
    private double[] alphaDeviation = {60.0, 80.0, 50};
    private double[] alphaMean = {0.0, 0.0, 60};
    private double[] strokeMin = {0.0, 60.0, 120};
    private double[] strokeMax = {170.0, 255.0};
    private double[] strokeWeight = {5.5, 2.5, 3.0, 6.3};

    private double[] minLayers = {3.0};
    private double[] maxLayers = {8.0};
    private double[] layerTransposeDeviation = {20.0};

    private double[] minSplitIterations = {3.0};
    private double[] maxSplitIterations = {8.0};
    private double[] splitOffset = {0.3};
    private double[] splitLengthPow = {1.0};
    private double[] splitStart = {0.0, 0.3};
    private double[] splitEnd = {0.7, 1.0};

    private double[] treeMinDist = {10.0, 30};
    private double[] treeMaxDist = {100.0, 300.0};
    private double[] treeDynamics = {1.0};
    private double[] treeStepSize = {10.0, 40.0};
    private double[] treeRandomDeviation = {10.0, 20.0};

    private double[] numberOfLeaves = {200.0};
    private double[] numberOfIterations = {200.0};

    private double getValue(double[] values, double y) {
        double i = MathUtils.map(y, 0, 1, 0, values.length - 1);

        int ifloor = (int) Math.floor(i);
        int iceil = (int) Math.ceil(i);

        double v1 = values[ifloor];
        double v2 = values[iceil];

        double r = 1.0 / values.length;
        double v = MathUtils.map((y % r), 0, r, 0, 1);
        return MathUtils.map(v, 0, 1, v1, v2);
    }

    public CthulhuSketch(Rectangle bounds, double multiplier) {
        this.bounds = bounds;
        pointMap = GNoise.simplexNoise(0.01, 1.0, 25);
        //h = HeightMaps.mult(h,
                //HeightMaps.fade(new Vector(sketchWidth/2, sketchHeight/2), 2),
                //HeightMaps.circles(bounds.width * multiplier, bounds.height * multiplier, 0, 0, new Vector(), 2),
                //HeightMaps.grid(0.01, 0.01, Math.random() * Math.PI, 0.2, 1.0, new Vector()),
                //0.5);
        pointMap = HeightMaps.stretch(pointMap, 0.5, 2.0);

        settingsMap = GNoise.simplexNoise(0.002, 1.0, 1);

        spawningBehavior =
                //new UniformPointGenerator(bounds);
                new GaussianOffsetGenerator(
                        //new RingPointGenerator(0.9*bounds.width/2, bounds.getCenter()),
                        new SetPointGenerator(bounds.getCenter()),
                        bounds.width/2);

    }

    @Override
    public PGraphics draw(PGraphics canvas, double frequency) {
        canvas.beginDraw();

        // Get start position and y value
        Vector startPos = spawningBehavior.get();
        double y = settingsMap.get(startPos);

        // Calculate settings
        double alphaDeviation = getValue(this.alphaDeviation, y);
        double alphaMean = getValue(this.alphaMean, y);
        double strokeMin = getValue(this.strokeMin, y);
        double strokeMax = getValue(this.strokeMax, y);
        double strokeWeight = getValue(this.strokeWeight, y) * frequency;

        int minLayers = (int) getValue(this.minLayers, y);
        int maxLayers = (int) getValue(this.maxLayers, y);
        double layerTransposeDeviation = getValue(this.layerTransposeDeviation, y) * frequency;

        int minSplitIterations = (int) getValue(this.minSplitIterations, y);
        int maxSplitIterations = (int) getValue(this.maxSplitIterations, y);
        double splitOffset = getValue(this.splitOffset, y);
        double splitLengthPow = getValue(this.splitLengthPow, y);
        double splitStart = getValue(this.splitStart, y);
        double splitEnd = getValue(this.splitEnd, y);

        double treeMinDist = getValue(this.treeMinDist, y) * frequency;
        double treeMaxDist = getValue(this.treeMaxDist, y) * frequency;
        double treeStepSize = getValue(this.treeStepSize, y) * frequency;
        double treeRandomDeviation = getValue(this.treeRandomDeviation, y) * frequency;

        double treeDynamics = getValue(this.treeDynamics, y);
        int numberOfLeaves = (int) getValue(this.numberOfLeaves, y);
        int numberOfIterations = (int) getValue(this.numberOfIterations, y);

        // Build tree
        SpaceFillTree<Component> treeGenerator = new SpaceFillTree<>(treeMinDist, treeMaxDist, treeDynamics, treeStepSize, treeRandomDeviation);

        double alpha = Math.abs(MathUtils.randomGaussian(alphaDeviation) + alphaMean);
        double stroke = MathUtils.random(strokeMin, strokeMax);
        canvas.stroke((float)stroke, (float)alpha);
        canvas.fill((float)stroke, (float) 0);
        canvas.strokeWeight((float) strokeWeight);

        PointGenerator pg = new HeightMapPointGenerator(
                HeightMaps.stretch(pointMap, 1.0/frequency, 1.0/frequency),
                (int)bounds.width, (int)bounds.height);

        List<Vector> leaves = pg.generate(numberOfLeaves);
        Segment<Component> tree = treeGenerator.generate(startPos, MathUtils.random(Math.PI*2), leaves, numberOfIterations);

        int layers = (int)MathUtils.random(minLayers, maxLayers);
        int splitIterations = (int)MathUtils.random(minSplitIterations, maxSplitIterations);
        for(int v = 0; v < layers; v++) {
            Segment<Component> connection = Segments.toSkeleton(tree);
            Vector transpose = Vector.randomWithLength(MathUtils.randomGaussian(layerTransposeDeviation));
            Segments.transpose(connection, transpose);

            for (int i = 0; i < splitIterations; i++) {
                Segments.offsetSplit(connection, splitStart, splitEnd, splitOffset,splitLengthPow);
            }

            Drawer drawer =
                    new SimpleSegmentDrawer(connection, bounds);
                    //new CircleSegmentDrawer(connection, bounds, 0.85, 1.2, 20);
            drawer.draw(canvas);
        }

        canvas.endDraw();

        System.out.println("Drawed!");
        return canvas;
    }

    @Override
    public Rectangle getBounds() {
        return bounds;
    }

    @Override
    public void setBounds(Rectangle bounds) {
        this.bounds = bounds;
    }
}
