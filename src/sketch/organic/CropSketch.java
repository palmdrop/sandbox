package sketch.organic;

import color.colors.Color;
import color.colors.Colors;
import color.fade.ColorFade;
import color.fade.Fades;
import color.fade.drawer.ColorFadeDrawer;
import color.fade.drawer.MapColorFadeDrawer;
import color.fade.fades.MultiColorFade;
import organic.Component;
import organic.generation.points.PointGenerator;
import organic.generation.points.gaussian.GaussianOffsetGenerator;
import organic.generation.segments.SpaceFillTree;
import organic.structure.branch.Branch;
import organic.structure.branch.Branches;
import organic.structure.branch.SimpleBranch;
import organic.structure.branch.drawer.SimpleBranchDrawer;
import organic.structure.segment.Segment;
import organic.structure.segment.drawer.FadingSegmentDrawer;
import processing.core.PGraphics;
import render.AbstractDrawer;
import render.Drawer;
import sampling.countour.Contours;
import sampling.heightMap.HeightMap;
import sampling.heightMap.HeightMaps;
import sketch.Sketch;
import util.geometry.Rectangle;
import util.math.MathUtils;
import util.noise.generator.GNoise;
import util.noise.type.OpenSimplexNoise;
import util.vector.Vector;

import java.util.List;

public class CropSketch extends AbstractDrawer implements Sketch  {
    private ColorFade fade;


    public CropSketch(Rectangle bounds) {
        super(bounds);

        Color c1 = Colors.parseHex("#3A1B31");
        Color c2 = Colors.parseHex("#28412B");
        Color c3 = Colors.parseHex("#423D1E");
        Color c4 = Colors.parseHex("#98801F");
        fade =
                //new SimpleColorFade(c1, c2, c3, Colors.CMY_SPACE);
                new MultiColorFade(Colors.CMY_SPACE, Contours.linear(0, 1), c1, c2, c3, c4);
    }

    private void renderBackground(PGraphics canvas) {
        HeightMap hm =
                //HeightMaps.fade(0.0, 1.0, 0.0, 0.0, 1.0, 1.0, 1.0);
                new GNoise(new OpenSimplexNoise(), 1, 1.0)
                .toDistorted().sin(0.0, 1.0, 1.0, 1.0);

        hm = hm.toDistorted()
            .domainWarp(HeightMaps.randomGaussian(0, 0.5), 0.03);
                //.domainWarp(GNoise.simplexNoise(0.01, 1.0, 1.0), 5)
                //.domainWarp()
        ;

        ColorFade bgFade = Fades.modifyHSB(fade, Colors.CMY_SPACE, new double[]{1.0, 0.6, 0.85}, MathUtils.BinaryOpMode.MULT, 5);

        ColorFadeDrawer drawer
                //= new SimpleColorFadeDrawer(fade, bounds, Mode.VERTICAL);
                = new MapColorFadeDrawer(bgFade, hm, bounds);
        drawer.draw(canvas);
    }

    @Override
    public PGraphics draw(PGraphics canvas, double frequency) {
        renderBackground(canvas);

        Vector origin = new Vector(bounds.width/2, bounds.height/1.01);

        int iterations = 2;

        for(int i = 0; i < iterations; i++) {
            Branch<Component> root = new SimpleBranch<>(-Math.PI / 2, 100);
            Branches.curve(root, 0.02, -0.0005, 210, 7);
            Branches.gaussianAngleOffset(root, 0.02);

            Drawer drawer = new SimpleBranchDrawer(origin, root, bounds);


            PointGenerator pg = Branches.toPointGenerator(root, origin);
            pg = new GaussianOffsetGenerator(pg, 20 * (1 + i));

            List<Vector> points = pg.generate(4000);

            SpaceFillTree<Component> sft = new SpaceFillTree<>(5, 40, 0.2, 3, 1.0);
            Segment<Component> segmentRoot = sft.generate(origin, root.getAngle(), points, 1000);
            //TODO: build on this! layers! make fade and die? unnatural and natural at the same time

            drawer =
                    //new SimpleSegmentDrawer(segmentRoot, bounds);
                    new FadingSegmentDrawer(segmentRoot, bounds, fade, 4, 2, Contours.easing(MathUtils.EasingMode.EASE_OUT, 0.5));

            //TODO: generate winding patterns using bezier curves!

            canvas.stroke(255 * (iterations - i) / iterations);
            drawer.draw(canvas);

            for (Vector p : points) {
                //canvas.point((float) p.getX(), (float) p.getY());
            }

        }
        return canvas;
    }
}
