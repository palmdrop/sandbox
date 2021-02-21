package sketch.organic;

import color.colors.Color;
import color.colors.Colors;
import color.fade.ColorFade;
import color.fade.fades.RampFade;
import organic.Component;
import organic.generation.points.area.HeightMapPointGenerator;
import organic.generation.segments.SpaceFillTree;
import organic.structure.segment.Segment;
import organic.structure.segment.drawer.PulsingSegmentDrawer;
import processing.core.PApplet;
import processing.core.PGraphics;
import render.Drawer;
import sampling.countour.Contours;
import sampling.heightMap.HeightMaps;
import sketch.Sketch;
import util.ArrayAndListTools;
import util.geometry.Rectangle;
import util.math.MathUtils;
import util.noise.generator.GNoise;
import util.vector.Vector;

import java.util.ArrayList;
import java.util.List;

public class CyberGrowthSketch implements Sketch {
    private Rectangle bounds;
    private PApplet p;

    // Render bushes
    private int iterations = 800;
    private int numberOfLeaves = 8000;

    private List<Vector> leaves;
    private SpaceFillTree<Component> treeBuilder;

    private List<Segment<Component>> roots;
    private List<Drawer> drawers;

    private ColorFade fade;
    private ColorFade plantFade;
    double startAngle = Math.random() * 2 * Math.PI;
    private int layers = 5;

    private HeightMapPointGenerator hp;

    private PGraphics buffer;

    public CyberGrowthSketch(PApplet p, Rectangle bounds) {
        this.p = p;
        this.bounds = bounds;
        roots = new ArrayList<>(layers);
        drawers = new ArrayList<>(layers);
        hp = new HeightMapPointGenerator(
                //HeightMaps.sin(0.005, 0.005, 1.0, 4.0).toDistorted().rotate(bounds.getCenter(), 1.0, 0.3),
                GNoise.simplexNoise(0.0012, 1.0, 12.0).toDistorted().rotate(bounds.getCenter(), 10, 0.2),
                //HeightMaps.checkers(400, 400, 0, 1),
                //HeightMaps.sin(0.002, 0.002, 1.0, 1.0),
                //HeightMaps.circles(100, 100, 10, 10, new Vector(), 0.9),
                (int)bounds.width, (int)bounds.height);
        fade = getFade();
        setup();
    }

    private ColorFade getFade() {
        Color c = Colors.random(Colors.HSB_SPACE, new double[]{0.0, 0.4, Math.pow(0.4, layers - layerCounter)}, new double[]{1.0, 0.6, Math.pow(0.9, layers - layerCounter)});
        return RampFade.fromColor(c, 0.7, 1.0, 0.8, RampFade.SatMode.DYNAMIC);
    }

    private ColorFade getSubFade() {
        Color c = fade.get(1.0f - (float)layerCounter / layers);
        //Color c = fade.get(Math.random());
        return RampFade.fromColor(c, 0.4, 1.0, 0.8, RampFade.SatMode.DYNAMIC);
    }

    private void setup() {
        leaves = leafGenerator();
        plantFade = getSubFade();
        treeBuilder = new SpaceFillTree<>(25, 200, 0.2, 5, 8.0);
        roots.add(treeBuilder.generate(ArrayAndListTools.randomElement(leaves), startAngle, leaves, 1));
        drawers.add(getDrawer(roots.get(roots.size() - 1)));
    }

    private List<Vector> leafGenerator() {
        return hp.generate(numberOfLeaves);
    }


    private Drawer getDrawer(Segment<Component> root) {
        double r = Math.random() * 8;
        return
                new PulsingSegmentDrawer(root, bounds,
                        plantFade,
                        1,
                        MathUtils.random(40, 120),
                        Contours.easing(MathUtils.EasingMode.EASE_OUT, 30),
                        HeightMaps.stretch(hp.getHeightMap(), r, r)
                        //HeightMaps.stretch(GNoise.simplexNoise(0.001, 1.0, 2.0), r, r)
                        //HeightMaps.sin(0.01, 0.01, 0, 1.0)
                );
    }

    private int growCounter = 0;
    private int layerCounter = 0;

    @Override
    public PGraphics draw(PGraphics canvas, double frequency) {
        canvas.beginDraw();
        if(layerCounter > layers) {
            canvas.image(buffer, 0, 0);
            canvas.endDraw();
            return canvas;
        }

        if(buffer == null) {
            buffer = p.createGraphics(canvas.width, canvas.height);
        }

        //TODO use heightmap to generate points and let algorithm grow between these points
        //TODO e.g grid, sine lines, weird shapes, letters

        //TODO: grow shape, let shape spawn new points surrounding itself, grow to them, continue
        //TODO: let points follow specific pattern?

        //TODO vary color depending on underlying noise?

        //TODO 3d growth but 2d representation?

        //TODO smaller structures, a simple shape, but with more detail
        //TODO vary detail of space fills across layers
        //TODO vary detail across space! dynamyc space fill tree

        //TODO use same technique as in cthulhu sketch but using this new space-filling technique



        for(int i = 0; i < 20; i++) {
            treeBuilder.grow();
            growCounter++;
        }

        if(growCounter >= iterations || treeBuilder.isExhausted()) {
            setup();
            growCounter = 0;
            layerCounter++;

            buffer.beginDraw();
            buffer.image(canvas, 0, 0);
            buffer.endDraw();
        }

        canvas.background(0);
        canvas.image(buffer, 0, 0);

        //for(int i = 0; i < roots.size(); i++) {
            Drawer drawer = drawers.get(layerCounter);

            canvas.stroke(255);
            drawer.draw(canvas);
        //}

        canvas.stroke(255);
        canvas.strokeWeight(5);
        for(Vector p : leaves) {
            //canvas.point((float)p.getX(), (float)p.getY());
        }

        canvas.endDraw();
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
