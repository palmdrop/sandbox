package nebula;

import color.colors.Color;
import color.colors.Colors;
import color.fade.ColorFade;
import color.fade.drawer.MapColorFadeDrawer;
import color.fade.fades.RampFade;
import jogamp.graph.curve.tess.HEdge;
import sampling.heightMap.HeightMap;
import sampling.heightMap.HeightMaps;
import sampling.heightMap.converter.PoissonDiskHeightMap;
import sampling.heightMap.modified.WarpedHeightMap;
import sampling.heightMap.modified.FeedbackHeightMap;
import sampling.heightMap.modified.ModdedHeightMap;
import processing.core.PApplet;
import render.heightMap.FadingHeightMapDrawer;
import util.geometry.Circle;
import util.geometry.Rectangle;
import util.math.MathUtils;
import util.noise.FractalHeightMap;
import util.noise.Noise;
import util.noise.generator.GNoise;
import util.vector.Vector;

import javax.imageio.plugins.bmp.BMPImageWriteParam;

public class TestSettings extends AbstractNebulaSettings {
    public TestSettings(PApplet p) {
        double radius = 200;
        baseMap =
                HeightMaps.offset(
                    new PoissonDiskHeightMap(p.width, p.height, GNoise.simplexNoise(0.003, 1.0, 1.0), radius/20, radius, 1, 3),
                p.width/2, p.height/2);








        Color c1 =
                Colors.RGB_SPACE.getColor(0.0, 0.0, 0.0);
        Color c2 =
                Colors.RGB_SPACE.getColor(1.0, 1.0, 1.0);



        drawer = new FadingHeightMapDrawer(baseMap, c1.toRGB(), c2.toRGB(), 0.03, 0.0, PApplet.HSB);
    }
}
