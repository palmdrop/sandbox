package nebula.designSettings.toExplore;

import color.colors.Color;
import color.colors.Colors;
import nebula.AbstractNebulaSettings;
import processing.core.PApplet;
import render.heightMap.FadingHeightMapDrawer;
import sampling.heightMap.HeightMap;
import sampling.heightMap.HeightMaps;
import sampling.heightMap.modified.FeedbackHeightMap;
import util.geometry.Circle;
import util.noise.generator.GNoise;
import util.vector.Vector;

public class ShapeSettings extends AbstractNebulaSettings {
    public ShapeSettings(PApplet p, double width, double height) {
        double size = 400;

        // Base shape
        HeightMap shape =
                HeightMaps.stretch(
                        HeightMaps.circle(new Circle(new Vector(width/2, height/2), size)),
        1.5, 2);

        int times = 5;
        double min = Math.pow(0.01, 1.0 / times);
        HeightMap space = shape
                .toModded()
                .addReverse()
                .addRemap(0, 1, min, 1)

                .toDistorted()
                .domainWarp(
                        shape,
                        GNoise.simplexNoise(0.01, 1.0, 1.0),
                        //HeightMaps.sin(0.05, 0.05, 0, 1.0),

                        -40, 40)
                .domainWarp(GNoise.simplexNoise(0.02, 1.0, 1.0), 35)

                .toFeedbackHeightMap(times, FeedbackHeightMap.Mode.MULT);

        baseMap = space;


        Color c1 =
                Colors.parseHex("#030004");
        Color c2 =
                Colors.parseHex("#ffd791");

        FadingHeightMapDrawer drawer = new FadingHeightMapDrawer(baseMap, c1.toRGB(), c2.toRGB(), 0.03, 0.1, PApplet.HSB);
        drawer.setSuperSampling(true);

        this.drawer = drawer;


    }
}
