package sketch.organic;

import color.colors.Color;
import color.colors.Colors;
import color.fade.ColorFade;
import color.fade.Fades;
import color.fade.drawer.ColorFadeDrawer;
import color.fade.drawer.MapColorFadeDrawer;
import color.fade.fades.MultiColorFade;
import color.fade.fades.RampFade;
import color.palette.Palettes;
import color.palette.palettes.balanced.sample1D.FadePalette;
import organic.Component;
import organic.generation.points.PointGenerator;
import organic.generation.points.gaussian.GaussianOffsetGenerator;
import organic.generation.points.limited.SetPointGenerator;
import organic.generation.points.limited.UniformLinePointGenerator;
import organic.generation.segments.SpaceFillTree;
import organic.structure.segment.Segment;
import organic.structure.segment.drawer.FadingSegmentDrawer;
import processing.core.PGraphics;
import render.Drawer;
import sampling.countour.Contours;
import sampling.heightMap.HeightMap;
import sampling.heightMap.HeightMaps;
import sketch.Sketch;
import texture.drawer.apply.ComponentTextureApplier;
import texture.drawer.apply.TextureApplier;
import texture.texture.layered.LayeredTexture;
import util.geometry.Rectangle;
import util.math.MathUtils;
import util.noise.generator.GNoise;
import util.vector.Vector;

import java.util.List;

public class CyberGrowthSketch implements Sketch {
    private Rectangle bounds;

    public CyberGrowthSketch(Rectangle bounds) {
        this.bounds = bounds;
    }

    private List<Vector> leafGenerator(Vector origin, Vector center, double deviation, int numberOfLeaves) {
        PointGenerator base = new SetPointGenerator(center);
        PointGenerator mainGenerator = new GaussianOffsetGenerator(base, 0, Math.PI * 2, deviation);


        List<Vector> leaves = mainGenerator.generate(numberOfLeaves);

        PointGenerator helperGenerator = new UniformLinePointGenerator(origin, center);

        //helperGenerator = new GaussianOffsetGenerator(helperGenerator, 0, 0, deviation/5);
        //leaves.addAll(helperGenerator.generate(numberOfLeaves/10));

        return leaves;
    }

    private ColorFade getFade() {

        Color c = Colors.random();
        RampFade fade = RampFade.fromColor(c, 0.4, 1.0, 1.0, RampFade.SatMode.DYNAMIC);

        return fade;
    }

    @Override
    public PGraphics draw(PGraphics canvas, double frequency) {
        ColorFade plantFade = getFade();

        ColorFade backgroundFade =
                new MultiColorFade(Colors.RGB_SPACE, Contours.linear(0, 1),
                        Palettes.componentVariation(
                            new FadePalette(plantFade, 5),
                            Colors.HSB_SPACE,
                            new double[]{1.0, 0.5, 0.2},
                            MathUtils.BinaryOpMode.MULT
                        )
                        );

        HeightMap cm = GNoise.simplexNoise(1.0, 1.0, 1.0);

        ColorFadeDrawer backgroundDrawer
                //= new SimpleColorFadeDrawer(backgroundFade, bounds, Mode.VERTICAL);
                = new MapColorFadeDrawer(backgroundFade, cm, bounds);
        backgroundDrawer.draw(canvas);


        // Render bushes
        double deviation = 160;
        int iterations = 400;
        int numberOfLeaves = 5000;

        Vector center = new Vector(bounds.width/2.0, bounds.height/2.0);
        Vector origin =
                Vector.add(
                center,
                new Vector(MathUtils.random(-100, 100), deviation * 2.5));

        int layers = 3;
        for(int i = 0; i < layers; i++) {
            List<Vector> leaves = leafGenerator(origin, center, deviation * Math.pow(1, i), numberOfLeaves);

            double startAngle =
                    //-Math.PI / 2;
                    Math.random() * 2 * Math.PI;

            SpaceFillTree<Component> treeBuilder = new SpaceFillTree<>(5, 40, 0.4, 5, 0.0);
            Segment<Component> root = treeBuilder.generate(origin, startAngle, leaves, iterations);

            //double start = (double)i / layers;
            double start = 0.0;
            //double amount = 1.0 / layers;
            double amount = 1.0 / (layers - i);

            ColorFade f = Fades.subfade(plantFade, start, start + amount);


            Drawer drawer =
                    new FadingSegmentDrawer(root, bounds,
                            f,
                            0.6,
                            10,
                            Contours.easing(MathUtils.EasingMode.EASE_OUT, 2)
                    );
            drawer.draw(canvas);
        }

        // Post processing
        HeightMap c1 =
                HeightMaps.constant(1.0);
        HeightMap c2 =
                //HeightMaps.constant(1.0);
                HeightMaps.random(0.9, 1.0);
        HeightMap c3 =
                HeightMaps.random(0.9, 1.0);

        LayeredTexture texture = new LayeredTexture(c1, c2, c3);
        TextureApplier textureApplier = new ComponentTextureApplier(texture, Colors.HSB_SPACE, bounds, 0.0);

        textureApplier.draw(canvas);

        //textureApplier = new CMYKTextureApplier(0.004, 0.3, 0.9, bounds);
        //textureApplier.draw(canvas);



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
