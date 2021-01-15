package nebula.designSettings;

import color.colors.Color;
import color.colors.Colors;
import nebula.AbstractNebulaSettings;
import processing.core.PApplet;
import render.heightMap.FadingHeightMapDrawer;
import sampling.heightMap.HeightMap;
import sampling.heightMap.HeightMaps;
import sampling.heightMap.modified.FeedbackHeightMap;
import sampling.heightMap.modified.ModdedHeightMap;
import util.noise.FractalHeightMap;
import util.noise.generator.GNoise;
import util.vector.Vector;

public class VortexSettings extends AbstractNebulaSettings {
    public VortexSettings(PApplet p) {
        /*
        HeightMap circles = HeightMaps.limitTo(
                HeightMaps.circles(40, 40, 0, 0, new Vector(20, 20), 1.0),

                new ModdedHeightMap(
                        GNoise.perlinNoise(0.007, 1.0, 1.0)
                ).addSoftThreshold(0.2, 0.4, 1.0)
        );

        HeightMap squares = HeightMaps.limitTo(
                HeightMaps.grid(0.1, 0.1, 0.0, 0.0, 1.0, new Vector(-20, -20)),
                new ModdedHeightMap(
                        GNoise.perlinNoise(0.01, 1.0, 1.0)
                ).addSoftThreshold(0.4, 0.2, 1.0)
        );

        HeightMap circleSquares =
                HeightMaps.average(HeightMaps.mult(circles, squares, 0.5),
                    GNoise.perlinNoise(0.01, 1.0, 1.0));

        HeightMap base =
                new WarpedHeightMap(circleSquares)
                        .sin(0, 0.00, 0.01, 100.0)
                        .domainWarp(circleSquares, HeightMaps.constant(1.0), 10, 30)
                ;

        WarpedHeightMap distorted = new WarpedHeightMap(
                ModdedHeightMap.reverse(base)
        ).domainWarp(base, HeightMaps.constant(1.0), 0, 20);

        baseMap = ModdedHeightMap.contrast(new FeedbackHeightMap(distorted, 2, FeedbackHeightMap.Mode.MULT), 1.0);
         */

        HeightMap holeMask =
                //HeightMaps.circles(p.width, p.height, 0, 0, new Vector(p.width/2.0, p.height/2.0), 0.5)
                HeightMaps.circles(p.width, p.height, 0, 0, new Vector(p.width/2.0, p.height/2.0), 1.7)
                .toModded().addReverse()
        ;

        // <<==>> <<==>>==>>==<<==<<==<<==>>
        // -->--<-->---<-->>->>->>-<<-<<-<<--<--<
        // &&***&&***&&

        holeMask = holeMask.toDistorted()
            .domainWarp(GNoise.simplexNoise(0.01, 1.0, 1), 2)
        ;


        FractalHeightMap fractal =
                new FractalHeightMap(0.005, 1, 2, 0.6, FractalHeightMap.Type.PERLIN, 5, System.nanoTime());
        fractal.setNormalize(true);

        HeightMap environment =
                //fractal.toDistorted()
                //fractal.toModded().addRemap(0, 1, 0.7, 1.0);
                HeightMaps.pow(fractal, HeightMaps.constant(0.185))
                .toDistorted().domainWarp(
                        fractal, 100)
                .toFeedbackHeightMap(3, FeedbackHeightMap.Mode.MULT)

                ;


        HeightMap space =
                //GNoise.simplexNoise(0.07, 1.0, 0.6)
                //HeightMaps.pow(environment, HeightMaps.constant(0.3))
                environment

                .toDistorted()
                        .domainWarp(holeMask, new ModdedHeightMap(holeMask).addReverse(), -0, 200)
                        //.domainWarp(GNoise.simplexNoise(0.01, 1.0, 1.0), new ModdedHeightMap(holeMask).addReverse(), 20, -50)

                        .toFeedbackHeightMap(3, FeedbackHeightMap.Mode.MULT);


        baseMap = HeightMaps.mult(space, holeMask, 0.5);
        //baseMap = space;

        baseMap = HeightMaps.pow(baseMap, HeightMaps.constant(0.8));

        Color c1 =
                //Colors.random(Colors.HSB_SPACE, new double[]{0.0, 0.2, 0.0}, new double[]{1.0, 1.0, 0.3});
                //Colors.random()
                Colors.parseHex("#030004");
        Color c2 =
                Colors.parseHex("#ffd791");
                //Colors.random(Colors.HSB_SPACE, new double[]{0.0, 0.5, 0.8}, new double[]{1.0, 1.0, 1.0});

        drawer = new FadingHeightMapDrawer(baseMap, c1.toRGB(), c2.toRGB(), 0.03, 0.0, PApplet.HSB);


    }
}
