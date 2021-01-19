package sketch.texture;

import color.colors.Color;
import color.colors.Colors;
import color.fade.ColorFade;
import color.fade.drawer.MapColorFadeDrawer;
import color.fade.fades.RampFade;
import color.space.ColorSpace;
import color.space.sampler.ColorSpaceSampler;
import processing.core.PGraphics;
import render.AbstractDrawer;
import render.Drawer;
import render.SampleDrawer;
import sampling.GraphicsSampler;
import sampling.domainWarp.DomainWarp;
import sampling.domainWarp.SimpleDomainWarp;
import sampling.heightMap.HeightMap;
import sampling.heightMap.HeightMaps;
import sketch.Sketch;
import util.geometry.Rectangle;
import util.math.MathUtils;
import util.noise.generator.GNoise;
import util.vector.Vector;

public class ColorGrainAndGlitchSketch extends AbstractDrawer implements Sketch  {
    public ColorGrainAndGlitchSketch(Rectangle bounds) {
        super(bounds);
    }

    @Override
    public PGraphics draw(PGraphics canvas, double frequency) {
        wow(canvas);

        return canvas;
    }

    private void lessWow(PGraphics canvas) {
        ColorSpace colorSpace = Colors.CMY_SPACE;

        ColorSpaceSampler colorSpaceSampler = new ColorSpaceSampler(colorSpace,
                GNoise.simplexNoise(MathUtils.random(0, 1), 1.0, 1.0)
                        .toModded().addRemap(0, 1, Math.random(), Math.random()),
                GNoise.simplexNoise(MathUtils.random(0, 1), 1.0 ,1.0)
                        .toModded().addRemap(0, 1, Math.random(), Math.random()),
                GNoise.simplexNoise(MathUtils.random(0, 1), 1.0 ,1.0)
                        .toModded().addRemap(0, 1, Math.random(), Math.random())
        );

        DomainWarp<Color> warp = new SimpleDomainWarp<>(colorSpaceSampler);
        warp.domainWarp(GNoise.simplexNoise(1, 1.0, 1.0), 0);
        warp.domainWarp(HeightMaps.random(0, 1), HeightMaps.random(0, 1), 0, 1);

        Drawer drawer =
                //new SampleDrawer(warp, canvas.width, canvas.height, new Vector());
                SampleDrawer.fromColorSampler(warp, 1, 1, new Vector());
        drawer.draw(canvas);
    }

    private void wow(PGraphics canvas) {
        /*
        Color c1 =
                Colors.random(Colors.HSB_SPACE, new double[]{0.1, 0.8, 0.1}, new double[]{1, 1.0, 1.0});
        Color c2 =
                Colors.random(Colors.HSB_SPACE, new double[]{0.1, 0.8, 0.1}, new double[]{1, 1.0, 1.0});
        Color c3 =
                Colors.random(Colors.HSB_SPACE, new double[]{0.1, 0.8, 0.1}, new double[]{1, 1.0, 1.0});

         */

        ColorFade fade = new RampFade(0.1, -0.5, 1.0, 2.0, RampFade.SatMode.DYNAMIC);
                //new MultiColorFade(Colors.HSB_SPACE, Contours.linear(0, 1), c1, c2, c3);

        HeightMap h = GNoise.simplexNoise(1.0, 1.0, 1.0);

        Drawer d = new MapColorFadeDrawer(fade, h, bounds);

        /*ColorSpace colorSpace = Colors.HSB_SPACE;

        HeightMap h1 = GNoise.simplexNoise(2.0, 1.0, 1.0)
                .toModded().addRemap(0, 1, 0.2, 0.6);
        HeightMap h2 = GNoise.simplexNoise(1.0, 1.0, 1.0)
                .toModded().addRemap(0, 1, 1, 1.0);
        HeightMap h3 = GNoise.simplexNoise(3, 1.0, 2)
                .toModded().addRemap(0, 1, 0, 1);

        Drawer d = new MapColorSpaceDrawer(colorSpace, bounds,
                h1,
                h2,
                h3
        );*/

        d.draw(canvas);

        GraphicsSampler sampler = new GraphicsSampler(canvas, GraphicsSampler.WrapMode.MIRROR_WRAP);
        DomainWarp<Integer> warp = new SimpleDomainWarp<>(sampler);
        //warp.domainWarp(GNoise.simplexNoise(0.001, 1.0, 1.0), 100);
        warp.domainWarp(HeightMaps.random(0, 1), HeightMaps.randomGaussian(0, 0.5), 0, 20);
        warp.domainWarp(GNoise.simplexNoise(0.001, 1.0, 2.0), GNoise.simplexNoise(0.001, 1.0, 1.0), 600);

        Drawer drawer = new SampleDrawer(warp, canvas.width, canvas.height, new Vector());
        drawer.draw(canvas);
    }
}
