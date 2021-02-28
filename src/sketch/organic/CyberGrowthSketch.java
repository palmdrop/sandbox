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
    private final PApplet p;

    private final int iterations = 800;
    private final int numberOfLeaves = 8000;

    private List<Vector> leaves;
    private SpaceFillTree<Component> treeBuilder;

    private final List<Segment<Component>> roots;
    private final List<Drawer> drawers;

    private final ColorFade fade;
    private ColorFade plantFade;
    double startAngle = Math.random() * 2 * Math.PI;
    private final int layers = 10;

    private final HeightMapPointGenerator hp;

    private PGraphics buffer;

    public CyberGrowthSketch(PApplet p, Rectangle bounds) {
        this.p = p;

        this.bounds = bounds;
        roots = new ArrayList<>(layers);
        drawers = new ArrayList<>(layers);
        hp = new HeightMapPointGenerator(
                //HeightMaps.sin(0.005, 0.005, 1.0, 4.0).toDistorted().rotate(bounds.getCenter(), 1.0, 0.3),
                GNoise.simplexNoise(0.002, 1.0, 4.0),
                        //.toDistorted().rotate(bounds.getCenter(), 10, 0.2),
                        //.toDistorted().domainWarp(
                        //HeightMaps.sin(0.01, 0.01, 1.0, 1.0), 200),
                                //GNoise.simplexNoise(0.001, 1.0, 1.0), 200),
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
        treeBuilder = new SpaceFillTree<>(30, 250, 0.2, 15, 3.0);
        roots.add(treeBuilder.generate(ArrayAndListTools.randomElement(leaves), startAngle, leaves, 1));
        drawers.add(getDrawer(roots.get(roots.size() - 1)));
    }

    private List<Vector> leafGenerator() {
        return hp.generate(numberOfLeaves);
    }


    private Drawer getDrawer(Segment<Component> root) {
        double r = Math.random() * 2;
        return
                new PulsingSegmentDrawer(root, bounds,
                        plantFade,
                        HeightMaps.stretch(GNoise.simplexNoise(0.01, 1.0, 2.0), r, r),
                        //hp.getHeightMap(),
                        0.0,
                        MathUtils.random(1, 5),
                        MathUtils.random(60, 120),
                        hp.getHeightMap(),
                        //HeightMaps.stretch(GNoise.simplexNoise(0.01, 1.0, 2.0), r, r),
                        1.0,
                        Contours.easing(MathUtils.EasingMode.EASE_OUT, 20)
                );
    }

    private int growCounter = 0;
    private int layerCounter = 0;

    @Override
    public PGraphics draw(PGraphics canvas, double frequency) {
        canvas.beginDraw();
        //canvas.strokeCap(PApplet.PROJECT);
        if(layerCounter > layers) {
            canvas.image(buffer, 0, 0);
            canvas.endDraw();
            return canvas;
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
