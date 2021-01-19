package nebula.designSettings;

import nebula.AbstractNebulaSettings;
import processing.core.PApplet;
import render.heightMap.TriadHeightMapDrawer;
import sampling.heightMap.HeightMap;
import sampling.heightMap.HeightMaps;
import sampling.heightMap.modified.FeedbackHeightMap;
import sampling.heightMap.modified.ModdedHeightMap;
import sampling.heightMap.modified.WarpedHeightMap;
import util.noise.generator.GNoise;
import util.vector.Vector;

public class ExperimentalSettings extends AbstractNebulaSettings {
    public ExperimentalSettings(PApplet p) {
        HeightMap base =
                HeightMaps.mult(
                    HeightMaps.fade(new Vector(), 0.0, 1.0, 0.1),
                    GNoise.perlinNoise(0.01, 1.0, 1.0), 1.0)
                .toDistorted()
                //.addDomainWarp(GNoise.perlinNoise(0.04, 1.0, 1.0), GNoise.simplexNoise(0.002, 1.0, 1.0), 3, 20)
                .toModded()
                .setContrastAndBrightness(1.2, 0.1)
        ;

        HeightMap rotation =
                //HeightMaps.circles(200, 200, 0.0, 0.0, new Vector(), 2.0)
                HeightMaps.grid(0.001, 0.001, 0.0, 0.0, 1.0, new Vector(1.7, 0))
                ;

        HeightMap offset =
                //GNoise.simplexNoise(0.01, 1.0, 1.0)
                HeightMaps.mult(
                    HeightMaps.sin(0.03, 0.03, 0.0, 1.0),
                    HeightMaps.fade(1.0, 0.1, 0.0, 0.4, 1.0, 100, 1.0),
                    0.5
                )
                .toDistorted()
                .domainWarp(base, rotation, 10, 40)

                //HeightMaps.grid(0.04, 0.04, 0.0, 0.0, 1.0, new Vector())
                ;

        baseMap =
                new WarpedHeightMap(base)
                        .sin(PApplet.PI, 0.5, 0.7, 20)
                        .domainWarp(rotation, offset, -20, 80)
                .toFeedbackHeightMap(3, FeedbackHeightMap.Mode.MULT)
        ;

        // Setup background
        System.out.println("SETTING UP BACKGROUND");
        p.colorMode(PApplet.HSB);

        int c1 =
                p.color((float) (Math.random() * 255), 255, 255);
        int c2 =
                p.color((float) (Math.random() * 255), 255, 0);
        int c3 =
                p.color((float) (Math.random() * 255), 255, 50);

        //TODO: fade between three colors using two heightmaps? ex one dark base, two bright glows!
        drawer
                //= new FadingHeightMapVisualizer(baseMap, c1, c2,  0.03, 0, PApplet.HSB);
                = new TriadHeightMapDrawer(baseMap,
                //GNoise.perlinNoise(0.01, 1.0, 1.0),
                ModdedHeightMap.reverse(offset),
                //rotation,
                //HeightMaps.constant(1.0),
                //base,
                //HeightMaps.mult(offset, rotation, 0.5),

                c1,
                c2,
                c3,
                0.03,
                0.0,
                PApplet.HSB);
    }
}
