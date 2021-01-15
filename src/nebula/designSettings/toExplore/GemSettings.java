package nebula.designSettings.toExplore;

import color.colors.Color;
import color.colors.Colors;
import color.colors.rgb.RGBColor;
import nebula.AbstractNebulaSettings;
import processing.core.PApplet;
import render.heightMap.FadingHeightMapDrawer;
import sampling.heightMap.HeightMap;
import sampling.heightMap.HeightMaps;
import sampling.heightMap.converter.PoissonDiskHeightMap;
import sampling.heightMap.converter.RandomCircleHeightMap;
import sampling.heightMap.modified.FeedbackHeightMap;
import sampling.heightMap.modified.ModdedHeightMap;
import util.noise.FractalHeightMap;
import util.noise.generator.GNoise;
import util.vector.Vector;

public class GemSettings extends AbstractNebulaSettings {
    public GemSettings(PApplet p) {
        double minRadius = 30;
        double maxRadius = 80;
        HeightMap holeMask =
                HeightMaps.offset(
                        //new PoissonDiskHeightMap(p.width, p.height, GNoise.simplexNoise(0.02, 1.0, 2.0), minRadius, maxRadius, 1, 2.1),
                        new RandomCircleHeightMap(p.width, p.height, 40, minRadius, maxRadius, 1, 2.1),
                        p.width/2, p.height/2);

                //HeightMaps.circles(p.width/6, p.height/6, 0, 0, new Vector(), 2)
        //;

        HeightMap environment =
                GNoise.simplexNoise(0.01, 1.0, 1);
        //new FractalHeightMap(0.013, 1.0, 2.0, 0.4, FractalHeightMap.Type.SIMPLEX, 3, System.nanoTime());

        holeMask = holeMask
                .toModded()
                    .addRemap(0, 1, 0.1, 1)
                .toDistorted()
            .domainWarp(GNoise.simplexNoise(0.01, 1.0, 1), 10)
            //.domainWarp(HeightMaps.sin(0.1, 0.1, 0.0, 1.0),3)
            //.domainWarp(environment, 3)
        ;


        HeightMap space =
                HeightMaps.pow(
                        environment.toDistorted()
                        .domainWarp(environment, 50)

                        , HeightMaps.constant(0.35))
                //environment
                .toDistorted()
                        //.domainWarp(holeMask, new ModdedHeightMap(holeMask).addReverse(), -200, 200)
                        .domainWarp(GNoise.simplexNoise(0.013, 1.0, 1.0), new ModdedHeightMap(holeMask), 0, 60)
                        .domainWarp(GNoise.simplexNoise(0.1, 1.0, 1.0), holeMask, 0, 2)
                        //.domainWarp(environment, new ModdedHeightMap(holeMask), 0, 100)
                        //.domainWarp(environment, environment, 20, -20)

                        .toFeedbackHeightMap(2, FeedbackHeightMap.Mode.MULT);


        baseMap = HeightMaps.mult(space,
                //HeightMaps.mult(holeMask.toModded().addReverse(), GNoise.simplexNoise(0.1, 1.0, 1.0), 0.5).toModded().addReverse(),
                holeMask,
                0.3);
        baseMap = baseMap.toModded().changeContrast(1.4);
        //baseMap = space;

        //baseMap = HeightMaps.pow(baseMap, HeightMaps.constant(0.8));

        Color c1 =
                //Colors.random(Colors.HSB_SPACE, new double[]{0.0, 0.2, 0.0}, new double[]{1.0, 1.0, 0.3});
                //new RGBColor(Colors.BLACK);
                new RGBColor(0.01, 0.01, 0.1);
        Color c2 =
                //new RGBColor(Colors.WHITE);
                new RGBColor(0.4, 0.4, 1);
                //new RGBColor(Colors.WHITE);
                //Colors.parseHex("#ffd791");
                //Colors.random(Colors.HSB_SPACE, new double[]{0.0, 0.5, 0.8}, new double[]{1.0, 1.0, 1.0});

        FadingHeightMapDrawer drawer = new FadingHeightMapDrawer(baseMap, c1.toRGB(), c2.toRGB(), 0.03, 0.4, PApplet.RGB);
        //drawer.setSuperSampling(true);
        this.drawer = drawer;


    }
}
