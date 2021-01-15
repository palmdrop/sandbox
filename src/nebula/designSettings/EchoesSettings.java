package nebula.designSettings;

import color.colors.Color;
import color.colors.Colors;
import nebula.AbstractNebulaSettings;
import processing.core.PApplet;
import render.heightMap.FadingHeightMapDrawer;
import sampling.heightMap.HeightMap;
import sampling.heightMap.HeightMaps;
import sampling.heightMap.modified.FeedbackHeightMap;
import util.geometry.Rectangle;
import util.vector.Vector;

public class EchoesSettings extends AbstractNebulaSettings {
    public EchoesSettings(PApplet p) {
        double offset = 0;//Math.random();

        HeightMap d1 =
                //(x, y) -> (new Vector(x, y).angle() / (Math.PI*2) + offset);
                (x, y) -> Math.atan2(x,  y) / (Math.PI *2);
        HeightMap d2 = (x, y) -> (1 - new Vector(x, y).angle() / (Math.PI*2) + offset) % 1.0;
        HeightMap dSq = (x, y) -> new Vector(x, y).lengthSq();

        // Base shape
        double size = 150;
        HeightMap shape =
                HeightMaps.rect(new Rectangle(-size/2, -size/2, size, size))
                       .toDistorted().rotate(new Vector(), Math.PI/4)
                .frequencyModulation(dSq, 1.00003, 1)
                //.toFeedbackHeightMap(1, FeedbackHeightMap.Mode.MULT)
        ;

        shape = shape.toModded().addReverse().addRemap(0, 1, 0.3, 1)
                .toDistorted()
                //.domainWarp(GNoise.simplexNoise(0.01, 1.0, 1.0), 100)
                .domainWarp(d1 , -70)
                .toFeedbackHeightMap(4, FeedbackHeightMap.Mode.MULT);
                //GNoise.simplexNoise(0.01, 1.0, 1.0);
                //HeightMaps.pow(baseMap, HeightMaps.constant(0.8));

        baseMap = shape;//space;

        HeightMap bMask = HeightMaps.circles(p.width*2, p.height*2, 0, 0, new Vector(p.width, p.height), 6)

                .toModded().addReverse()
        ;
        //bMask = HeightMaps.stretch(bMask, 0., 0.5);

        //dSq = HeightMaps.mult(dSq, bMask, 0.5);

        baseMap =
                HeightMaps.mult(
                        HeightMaps.grid(0.2, 0.3, Math.PI/2, Math.PI/3, 0.2, new Vector()),
                                //.toModded().addReverse(),
                //GNoise.simplexNoise(0.03, 1.0, 0.5),
                        //HeightMaps.circles(100, 100, 0, 0, new Vector(), 2)
                        //.toModded().addReverse(),
                        //HeightMaps.rect(new Rectangle(-100, -100, 200, 200))
                        HeightMaps.grid(0.07, 0.07, Math.PI/1.8, 2.8, 0.56, new Vector()),
                        //.toModded().addReverse(),
                        //.toDistorted().rotate(new Vector(), Math.PI/4),
                0.8//0.9
                ).toModded().addReverse()
                        .toDistorted()

                .frequencyModulation(dSq, 1.0000028, 1)
                .domainWarp(d2,40)
                        //.domainWarp(GNoise.simplexNoise(0.01, 1.0, 1.0), 100)
                .toFeedbackHeightMap(3, FeedbackHeightMap.Mode.MULT)
                //.toModded().addReverse()
                ;

        baseMap = HeightMaps.mult(baseMap, bMask, 0.5);
        //baseMap = HeightMaps.pow(baseMap, bMask);

        //baseMap = baseMap.toModded().addReverse();






    //TODO: echo effect in one direction, combine many copied maps of base shape, echo effect in diff directions, etc!!

        Color c1 =
                //Colors.random(Colors.HSB_SPACE, new double[]{0.0, 0.2, 0.0}, new double[]{1.0, 1.0, 0.3});
                //Colors.random()
                //Colors.parseHex("#4a0007");
                Colors.RGB_SPACE.getColor(0.1, 0.0, 0.01);
        Color c2 =
                //Colors.parseHex("#ffd791");
                //Colors.random(Colors.HSB_SPACE, new double[]{0.0, 0.5, 0.8}, new double[]{1.0, 1.0, 1.0});
                //Colors.RGB_SPACE.getColor(1, 1, 0);
                //Colors.parseHex("#ffd9f8");
                Colors.RGB_SPACE.getColor(1.0, 0.7, 0.9);



        drawer = new FadingHeightMapDrawer(baseMap, c1.toRGB(), c2.toRGB(), 0.03, 0.0, PApplet.HSB);
        //ColorFade fade = new RampFade(0.2, 0.4, 1.0, 1.0, RampFade.SatMode.DYNAMIC);
        //drawer = new MapColorFadeDrawer(fade, baseMap, null);


    }
}
