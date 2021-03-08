package sketch.organic;

import color.colors.Color;
import color.colors.Colors;
import color.fade.ColorFade;
import color.fade.fades.RampFade;
import flow.FlowField;
import organic.Component;
import organic.generation.points.area.HeightMapPointGenerator;
import organic.generation.segments.SpaceFillTree;
import organic.structure.segment.Segment;
import organic.structure.segment.drawer.DirectionalPulsingSegmentDrawer;
import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PImage;
import render.Drawer;
import sampling.GraphicsHeightMap;
import sampling.GraphicsSampler;
import sampling.countour.Contours;
import sampling.heightMap.HeightMaps;
import sketch.Sketch;
import util.ArrayAndListTools;
import util.file.FileUtils;
import util.geometry.Rectangle;
import util.math.MathUtils;
import util.vector.Vector;

import java.util.ArrayList;
import java.util.List;

public class FlowFieldGrowthSketch implements Sketch {
    // *** RENDERING FIELDS ***/
    private Rectangle bounds;
    private final PApplet p;
    private PGraphics buffer;

    // *** SETTINGS *** //

    private final int iterations = 800;
    private final int numberOfLeaves = 8000;

    private double startAngle = Math.random() * 2 * Math.PI;
    private final int layers = 10;


    private final double[] noiseFrequency = {0.0005, 0.002};
    private final double[] noisePow = {4.0, 10.0};

    private enum LayerMode {
        RANDOM,
        LINEAR
    }

    private LayerMode layerMode = LayerMode.RANDOM;

    //TODO use start stop either for random or for iterating (but expand to allow more elements, see previous sketches)
                                      //START, STOP (or MIN, MAX)
    private final double[] treeMinDist     = {30, 100};
    private final double[] treeMaxDist     = {150, 300};
    private final double[] treeDynamics    = {0.2f, 0.7f};
    private final double[] treeStepSize    = {3, 20};
    private final double[] treeDeviation   = {0.5f, 2};

    // *** INTERNAL DATA *** //
    private List<Vector> leaves;
    private SpaceFillTree<Component> treeBuilder;

    private final List<Segment<Component>> roots;
    private final List<Drawer> drawers;

    private final ColorFade fade;

    private final HeightMapPointGenerator hp;

    // Stage
    private enum Stage {
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
        roots = new ArrayList<>(layers);
        drawers = new ArrayList<>(layers);


        String path = ArrayAndListTools.randomElement(FileUtils.listFiles(
                "output/",
                //"sourceImages/collected",
                new String[]{".png", ".jpg", ".jpeg"})).getPath();
        PImage img = p.loadImage(path);

        System.out.println(path);

        hp = new HeightMapPointGenerator(
                new GraphicsHeightMap(
                        img,
                        GraphicsSampler.WrapMode.MIRROR_WRAP,
                        c -> Colors.brightness(c)
                ),
                //HeightMaps.sin(0.005, 0.005, 1.0, 4.0).toDistorted().rotate(bounds.getCenter(), 1.0, 0.3),
                //GNoise.simplexNoise(
                        //getValue(noiseFrequency), 1.0, getValue(noisePow)),
                        //.toDistorted().rotate(bounds.getCenter(), 10, 0.2),
                        //.toDistorted().domainWarp(
                        //HeightMaps.sin(0.01, 0.01, 1.0, 1.0), 200),
                                //GNoise.simplexNoise(0.001, 1.0, 1.0), 200),
                //HeightMaps.checkers(400, 400, 0, 1),
                //HeightMaps.sin(0.002, 0.002, 1.0, 1.0),
                //HeightMaps.circles(100, 100, 10, 10, new Vector(), 0.9),
                (int)bounds.width, (int)bounds.height);
        fade = getFade();

        stage = Stage.GROW;

        setup();
    }

    private ColorFade getFade() {
        Color c = Colors.random(Colors.HSB_SPACE, new double[]{0.0, 0.4, Math.pow(0.4, layers - layerCounter)}, new double[]{1.0, 0.6, Math.pow(0.9, layers - layerCounter)});
        return RampFade.fromColor(c, 0.7, 1.0, 0.8, RampFade.SatMode.DYNAMIC);
    }

    private double getValue(double[] value) {
       if(layerMode == LayerMode.RANDOM) {
           return MathUtils.random(value[0], value[1]);
       } else {
           return MathUtils.map(layerCounter, 0, layers - 1, value[0], value[1]);
       }
    }

    private void setup() {
        leaves = leafGenerator();
        treeBuilder = new SpaceFillTree<>(
                getValue(treeMinDist),
                getValue(treeMaxDist),
                getValue(treeDynamics),
                getValue(treeStepSize),
                getValue(treeDeviation));
        roots.add(treeBuilder.generate(ArrayAndListTools.randomElement(leaves), startAngle, leaves, 1));
        drawers.add(getDrawer(roots.get(roots.size() - 1)));
    }

    private List<Vector> leafGenerator() {
        return hp.generate(numberOfLeaves);
    }


    private Drawer getDrawer(Segment<Component> root) {
        return
                new DirectionalPulsingSegmentDrawer(root, bounds,
                        hp.getHeightMap(),
                        0.0,
                        MathUtils.random(1, 5),
                        MathUtils.random(60, 120),
                        HeightMaps.pow(hp.getHeightMap(), HeightMaps.constant(6)),
                        1.0,
                        Contours.easing(MathUtils.EasingMode.EASE_OUT, 20)
                );
    }

    private int growCounter = 0;
    private int layerCounter = 0;

    private PGraphics previousCanvas;

    @Override
    public PGraphics draw(PGraphics canvas, double frequency) {
        canvas.beginDraw();

        switch(stage) {
            case GROW:
                renderGrow(canvas);
                break;
            case FIELD:
                renderFlowField(canvas);
                break;
            case FLOW:
                renderParticles(canvas);
                break;
            case DONE:
            default:
                System.out.println("Done!");
                return canvas;
        }

        previousCanvas = canvas;

        canvas.endDraw();
        return canvas;
    }

    private void renderGrow(PGraphics canvas) {

        if(layerCounter > layers) {
            canvas.image(buffer, 0, 0);
            canvas.endDraw();
            return;
        }

        if(buffer == null) {
            buffer = p.createGraphics(canvas.width, canvas.height);
        }

        for(int i = 0; i < 20; i++) {
            treeBuilder.grow();
            growCounter++;
        }

        if(growCounter >= iterations || treeBuilder.isExhausted()) {
            setup();
            growCounter = 0;
            layerCounter++;
            System.out.println("Layer " + layerCounter);

            buffer.beginDraw();
            buffer.image(canvas, 0, 0);
            buffer.endDraw();
        }

        canvas.background(0);
        canvas.image(buffer, 0, 0);

        Drawer drawer = drawers.get(layerCounter);

        canvas.stroke(255);
        drawer.draw(canvas);
    }

    private FlowField field = null;
    private void renderFlowField(PGraphics canvas) {
        if(field == null) {
            System.out.println("Generating flow field");
            PImage source = previousCanvas.get();
            field = new FlowField(
                    new GraphicsSampler(source),
                    c -> Colors.hue(c) * Math.PI * 2,
                    c -> Colors.brightness(c),
                    previousCanvas.width,
                    previousCanvas.height,
                    1
            );
            System.out.println("Generated!");
        }

        canvas.loadPixels();
        for(int x = 0; x < Math.min(canvas.width, field.getWidth()); x++) for(int y = 0; y < Math.min(canvas.height, field.getHeight()); y++) {
            Vector v = field.get(x, y);

            double hue = v.angle() / (Math.PI * 2);
            double sat = 1.0;
            double bri = v.length();

            canvas.pixels[x + y * canvas.width] =  Colors.HSB_SPACE.getRGB(hue, sat, bri);
        }
        canvas.updatePixels();
    }

    private void renderParticles(PGraphics canvas) {

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
