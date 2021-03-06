package sketch.organic;

import color.colors.Colors;
import flow.FlowField;
import flow.ParticleSimulation;
import flow.ParticleSimulationDrawer;
import flow.particle.Particle;
import organic.Component;
import organic.generation.growth.LayeredGrowth;
import organic.generation.points.area.HeightMapPointGenerator;
import organic.generation.segments.DynamicSpaceFillTree;
import organic.structure.segment.Segment;
import organic.structure.segment.drawer.DirectionalPulsingSegmentDrawer;
import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PImage;
import render.Drawer;
import render.heightMap.FadingHeightMapDrawer;
import sampling.GraphicsHeightMap;
import sampling.GraphicsSampler;
import sampling.countour.Contours;
import sampling.heightMap.HeightMap;
import sampling.heightMap.HeightMaps;
import sampling.heightMap.modified.DynamicFractalHeightMap;
import sampling.heightMap.modified.FractalHeightMap;
import sketch.Sketch;
import util.ArrayAndListTools;
import util.file.FileUtils;
import util.geometry.Rectangle;
import util.math.MathUtils;
import util.noise.generator.GNoise;
import util.vector.ReadVector;
import util.vector.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class FlowFieldGrowthSketch implements Sketch {
    // *** GENERAL ***/
    private final PApplet p;
    private Rectangle bounds;

    // *** SETTINGS *** //
    private final int iterations = 800;
    private final int numberOfLeaves = 8000;
    private final int layers = 10;

    private final double[] noiseFrequency = {0.0005, 0.003};
    private final double[] noisePow = {1.0, 3.0};

                                              //START, STOP (or MIN, MAX)
    private final double[] treeMinDist     = {50, 25, 5};
    private final double[] treeMaxDist     = {400, 100, 20};
    private final double[] treeDynamics    = {0.4f, 0.7f, 0.9f};
    private final double[] treeStepSize    = {4, 3, 2};
    private final double[] treeDeviation   = {1.5f, 0.2f};

    private final double[] drawerMinWidth = {1.0, 1.0};
    private final double[] drawerMaxWidth = {300.0, 600.0};

    //HeightMaps.pow(new FractalHeightMap(1, 1.0, FractalHeightMap.Type.SIMPLEX, 8).setNormalize(true), HeightMaps.constant(2)),
    private final HeightMap
            minControl =
                    GNoise.simplexNoise(MathUtils.random(noiseFrequency), 1.0, 2.0),
                    //HeightMaps.sin(MathUtils.random(noiseFrequency), MathUtils.random(noiseFrequency), 0.0, 1.5),
            maxControl =
                    GNoise.simplexNoise(MathUtils.random(noiseFrequency), 1.0, 2.0),
            dynamicsControl =
                    GNoise.simplexNoise(MathUtils.random(noiseFrequency), 1.0, 2.0),
            stepSizeControls =
                    GNoise.simplexNoise(MathUtils.random(noiseFrequency), 1.0, 2.0),
            deviationControls =
                    GNoise.simplexNoise(MathUtils.random(noiseFrequency), 1.0, 2.0);

    // *** INTERNAL DATA *** //
    private final List<Drawer> drawers;
    private HeightMap baseHeightmap;
    private HeightMapPointGenerator hp;

    // Stage
    private enum Stage {
        NONE,
        HEIGHTMAP,
        GROW,
        FIELD,
        FLOW,
        DONE {
            @Override
            public Stage next() {
                return DONE;
            };
        };

        public Stage next() {
            // No bounds checking required here, because the last instance overrides
            return values()[ordinal() + 1];
        }
    };

    private Stage stage;

    public FlowFieldGrowthSketch(PApplet p, Rectangle bounds) {
        this.p = p;
        this.bounds = bounds;
        drawers = new ArrayList<>(layers);

        stage = Stage.values()[0];

        progress();
    }

    public void progress() {
        stage = stage.next();
        generateCurrent();
    }

    public void generateCurrent() {
        switch (stage) {
            case NONE:
                break;
            case HEIGHTMAP:
                System.out.println("Generating heightmap");
                generateHeightmap();
                System.out.println("[DONE] Generating heightmap");
                break;
            case GROW:
                System.out.println("Generating growth");
                generateGrowth();
                System.out.println("[DONE] Generating growth");
                break;
            case FIELD:
                System.out.println("Generating flow field");
                generateFlowField();
                System.out.println("[DONE] Generating flow field");
                break;
            case FLOW:
                System.out.println("Generating particles system");
                generateParticleSystem();
                System.out.println("[DONE] Generating particles system");
                break;
            case DONE:
                break;
        }
    }

    private void generateHeightmap() {
        String path =
                ArrayAndListTools.randomElement(FileUtils.listFiles(
                        "output/texture/",
                        //"sourceImages/",
                        //"/home/xan/usr/pictures/dada/",
                        new String[]{".png", ".jpg", ".jpeg"})).getPath();
        PImage img = p.loadImage(path);

        System.out.println(path);

        double scaleX = img.width / bounds.width;
        double scaleY = img.height / bounds.height;

        baseHeightmap =
                HeightMaps.stretch(
                        new GraphicsHeightMap(
                            img, GraphicsSampler.WrapMode.MIRROR_WRAP,
                            Colors::brightness
                        ), scaleX, scaleY);
                /*minControl.toDistorted()
                //.domainWarp(HeightMaps.circles(1000, 1000, 0, 0, new Vector(), 1), 100)
                .domainWarp(maxControl, dynamicsControl, 400)
                .domainWarp(stepSizeControls, 400);*/
                //.rotate(bounds.getCenter(), 1, 0.5);
                //.toModded().addMod(10 * Math.random());
                //new SpirePattern(MathUtils.random(noiseFrequency), 0.2, 0.8, 1.8, 0.65, 100, 1);


                //GNoise.simplexNoise(0.01, 1.0, 1.0);
        hp = new HeightMapPointGenerator(baseHeightmap, (int)bounds.width, (int)bounds.height);
    }

    private Drawer getDrawer(Segment<Component> root) {
        return
                new DirectionalPulsingSegmentDrawer(root, bounds,
                        //TODO vary use of bri controller, swap with width controller?!!?!?!? but use offset/variations for each layer?
                        hp.getHeightMap(),
                        0.0,
                        MathUtils.random(drawerMinWidth),
                        MathUtils.random(drawerMaxWidth),
                        //deviationControls,
                        HeightMaps.pow(new FractalHeightMap(0.003, 1.0, FractalHeightMap.Type.SIMPLEX, 8).setNormalize(true), HeightMaps.constant(3)),
                        //HeightMaps.constant(1.0),
                        1.0,
                        Contours.easing(MathUtils.EasingMode.EASE_OUT, 3)
                );
    }
    private void generateGrowth() {
        //HeightMap h =
        //GNoise.simplexNoise(0.01, 1.0, 1.0);
                //hp.getHeightMap();
        Supplier<DynamicSpaceFillTree<Component>> treeGenerator = () -> new DynamicSpaceFillTree<>(
                treeMinDist, minControl,
                treeMaxDist, maxControl,
                treeDynamics, dynamicsControl,
                treeStepSize, stepSizeControls,
                treeDeviation, deviationControls);

        LayeredGrowth layeredGrowth = new LayeredGrowth(hp, numberOfLeaves, layers, iterations, treeGenerator);

        for(int i = 0; i < layeredGrowth.getNumberOfLayers(); i++) {
            drawers.add(getDrawer(layeredGrowth.getRoot(i)));
        }
    }

    private FlowField field = null;
    private void generateFlowField() {
        PImage source = previousCanvas.get();
        field = new FlowField(
                new GraphicsSampler(source),
                c -> Colors.hue(c) * Math.PI * 2,
                c -> Math.pow(Colors.brightness(c), 0.2),
                previousCanvas.width,
                previousCanvas.height,
                1
        );

        progress();
    }

    private ParticleSimulation particleSimulation = null;
    private ParticleSimulationDrawer particleSimulationDrawer = null;

    private void generateParticleSystem() {
        long lifeTime = 2 * 1000;

        //HeightMapPointGenerator points = new HeightMapPointGenerator(HeightMaps.sub(HeightMaps.constant(1.0), baseHeightmap), (int)bounds.width, (int)bounds.height);

        particleSimulation = new ParticleSimulation(
                field,
                () -> new Particle(hp.get()),
                //() -> new Particle(points.get()),
                10000,
                lifeTime,
                0.02,
                1.3
        );

        double minHue = Math.random();
        double maxHue = minHue + (Math.random() > 0.5 ? -1 : 1) * MathUtils.random(0.3, 0.7);
        particleSimulationDrawer = new ParticleSimulationDrawer(
                particleSimulation,
                (c, p, l) -> {
                    double time = 1.0 - (double)l / lifeTime;

                    double hue =
                            //p.getVel().getX() * p.getVel().getY();
                            MathUtils.map(time * p.getVel().angle(), 0, Math.PI * 2, minHue, maxHue);
                            //MathUtils.map(baseHeightmap.get(p.getPos()) * maxControl.get(p.getPos()) * time, 0, 1, minHue, maxHue);
                            //p.getX() / 2.0;
                    double sat = Math.pow(0.1 * p.getVel().length(), 1.5);
                    double bri = Math.pow(0.5 * p.getVel().length(), 2.5);


                    c.strokeWeight(1);
                    c.stroke(Colors.HSB_SPACE.getRGB(hue, sat, bri), (float) (30 * time));
                    c.line((float)p.getPreviousPosition().getX(), (float)p.getPreviousPosition().getY(), (float)p.getX(), (float)p.getY());
                },
                bounds
        );

        drawBackground = true;
    }



    private PGraphics previousCanvas;

    private boolean drawBackground = false;
    @Override
    public PGraphics draw(PGraphics canvas, double frequency) {
        canvas.beginDraw();
        if(drawBackground) canvas.background(0);

        switch(stage) {
            case HEIGHTMAP:
                renderHeightmap(canvas);
                break;
            case GROW:
                renderGrow(canvas);
                break;
            case FIELD:
                //renderFlowField(canvas);
                break;
            case FLOW:
                renderParticles(canvas);
                break;
            default:
                break;
        }

        previousCanvas = canvas;

        canvas.endDraw();
        return canvas;
    }

    private void renderHeightmap(PGraphics canvas) {
        FadingHeightMapDrawer drawer = new FadingHeightMapDrawer(hp.getHeightMap(), 0, 0);
        drawer.draw(canvas);
    }

    private void renderGrow(PGraphics canvas) {
        canvas.background(0);

        drawers.forEach(drawer -> {
            canvas.stroke(255);
            drawer.draw(canvas);
        });
    }

    private void renderFlowField(PGraphics canvas) {
        canvas.loadPixels();
        for(int x = 0; x < Math.min(canvas.width, field.getWidth()); x++) for(int y = 0; y < Math.min(canvas.height, field.getHeight()); y++) {
            ReadVector v = field.get(x, y);

            double hue = v.angle() / (Math.PI * 2);
            double sat = 1.0;
            double bri = v.length();

            canvas.pixels[x + y * canvas.width] =  Colors.HSB_SPACE.getRGB(hue, sat, bri);
        }
        canvas.updatePixels();
    }

    private void renderParticles(PGraphics canvas) {
        canvas.strokeWeight(2);
        particleSimulationDrawer.draw(canvas);
        particleSimulation.update();
        drawBackground = false;
    }

    @Override
    public Rectangle getBounds() {
        return bounds;
    }

    @Override
    public void setBounds(Rectangle bounds) {
        this.bounds = bounds;
    }

    public void nextStage() {
        stage = stage.next();
    }
}
