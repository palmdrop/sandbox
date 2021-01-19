package nebula.designSettings;

import nebula.AbstractNebulaSettings;
import processing.core.PApplet;
import render.heightMap.TriadHeightMapDrawer;
import sampling.heightMap.HeightMap;
import sampling.heightMap.HeightMaps;
import sampling.heightMap.modified.FeedbackHeightMap;
import sampling.heightMap.modified.WarpedHeightMap;
import util.noise.generator.GNoise;
import util.vector.Vector;

public class HighwaySettings extends AbstractNebulaSettings {
    public HighwaySettings(PApplet p) {
        HeightMap base =
                HeightMaps.mult(
                        //GNoise.simplexNoise(0.01, 1.0, 0.2),
                        HeightMaps.grid(0.09, 0.09, Math.random() * p.TWO_PI, Math.random() * p.TWO_PI, 1.3, new Vector()),
                        GNoise.perlinNoise(0.04, 1.0, 0.05),
                    0.9)
                .toDistorted()
                        .domainWarp(GNoise.perlinNoise(0.005, 1.0, 1.0),
                                GNoise.simplexNoise(0.01, 1.0, 1.0), 0, 100)
                //.addRotation(Vector.randomWithLength(500), 0.05, 0.6)

                .toModded()
                .changeContrast(1.1)
        ;

        HeightMap rotation =
                HeightMaps.saw(0.004, 0.004, 0.0, 1.0)
                .toDistorted()
                .domainWarp(
                        GNoise.simplexNoise(0.025, 1.0, 1.0),
                        HeightMaps.constant(1.0),
                    0, 50)
                        .rotate(new Vector(), 0.01, 0.3)
                .toModded()
                .addOffset(GNoise.perlinNoise(0.01, 1.0, 1.0), true)
                ;

        HeightMap offset =
                HeightMaps.circles(30, 30, 0.0, 0.0, new Vector(), 2.0)
                .toDistorted()
                .rotate(new Vector(), 4, 0.1)
                .domainWarp(GNoise.perlinNoise(0.015, 1, 1.0), 10)
                .toFeedbackHeightMap(2, FeedbackHeightMap.Mode.MULT)
                ;

        baseMap =
                new WarpedHeightMap(base)
                        .domainWarp(rotation, offset, -10, 10)
                .toFeedbackHeightMap(3, FeedbackHeightMap.Mode.MULT)
        ;

        // Setup background
        System.out.println("SETTING UP BACKGROUND");
        p.colorMode(PApplet.HSB);

        int c1 =
                p.color((float) (Math.random() * 255), 100, 0);
        int c2 =
                p.color((float) (Math.random() * 255), 20, 255);
        int c3 =
                p.color((float) (Math.random() * 255), 50, 255);

        //TODO: fade between three colors using two heightmaps? ex one dark base, two bright glows!
        drawer
                //= new FadingHeightMapVisualizer(baseMap, c1, c2,  0.03, 0, PApplet.HSB);
                = new TriadHeightMapDrawer(baseMap,
                //GNoise.perlinNoise(0.01, 1.0, 1.0),
                //offset,
                //rotation,
                base,
                //HeightMaps.constant(1.0),
                c1,
                c2,
                c3,
                0.03,
                0.0,
                PApplet.HSB);
    }
}
