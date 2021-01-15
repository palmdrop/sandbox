package sketch.texture;

import color.colors.Color;
import color.colors.Colors;
import processing.core.PApplet;
import processing.core.PGraphics;
import render.AbstractDrawer;
import render.heightMap.FadingHeightMapDrawer;
import sampling.Sampler;
import sampling.heightMap.HeightMap;
import sampling.heightMap.HeightMaps;
import sampling.heightMap.modified.ModdedHeightMap;
import sampling.heightMap.modified.pixelated.PixelatedSampler;
import sampling.heightMap.modified.WarpedHeightMap;
import sketch.Sketch;
import util.geometry.Rectangle;
import util.math.MathUtils;
import util.noise.ComplexFractalHeightMap;
import util.noise.FractalHeightMap;
import util.noise.generator.GNoise;
import util.vector.Vector;

public class RecursiveTextureSketch extends AbstractDrawer implements Sketch {
    public RecursiveTextureSketch(Rectangle bounds) {
        super(bounds);
    }

    @Override
    public PGraphics draw(PGraphics canvas, double frequency) {
        return draw(canvas, frequency, false);
    }

    public PGraphics draw(PGraphics canvas, double frequency, boolean superSampling) {
        HeightMap dSq = (x, y) -> new Vector(x, y).lengthSq();
        Vector center = new Vector(0, 0).add(Vector.randomWithLength(MathUtils.random(canvas.width/30.0, canvas.height/10.0)));
        double radius = MathUtils.random(canvas.width/10.0, canvas.width/6.0);
        Sampler<Double> hm1 =
                //HeightMaps.sin(0.08, 0.0, 0.0, 1.0);
                //GNoise.simplexNoise(0.03, 1.0, 1.0);
                //new FractalHeightMap(0.006, 1.0, 2, 0.5, FractalHeightMap.Type.SIMPLEX, 3, System.nanoTime()).setNormalize(true);
                //HeightMaps.grid(0.01, 0.01, 0.0, 0.2, 0.5, new Vector());
                //HeightMaps.circles(250, 250, 0, 0, new Vector(250/2, 250/2), 1);
                //HeightMaps.circle(new Circle(new Vector(0, 0), canvas.width/2.8));
                //HeightMaps.circle(new Circle(center, radius), 1.0, MathUtils.EasingMode.EASE_IN);
                HeightMaps.saw(0.001, 0.002, 1.0, 1.0);
                //HeightMaps.clip(GNoise.simplexNoise(0.01, 1.0, 1.0), 0.5, 0.0, true);

        /*hm1 = HeightMaps.mult(hm1,
                HeightMaps.circle(new Circle(center, MathUtils.random(radius/5, radius/1.3)), 1.0, MathUtils.EasingMode.EASE_IN).toModded().addReverse(),
                0.5);*/

        hm1 =
                //HeightMaps.mult(
                //HeightMaps.constant(1.0),
                //hm1.toModded().addReverse(),
                new ModdedHeightMap(hm1).addOffset(
                //GNoise.simplexNoise(0.06, 1.0, 1.0),
                new ComplexFractalHeightMap(0.009,
                        1.0,
                        2.0,
                        //GNoise.simplexNoise(0.01, 1.0, 1.0).toModded().addRemap(0, 1, 0.93, 1.0),
                        HeightMaps.constant(1.0),
                        0.7,
                        GNoise.simplexNoise(0.01, 1.0, 1.0).toModded().addRemap(0.0, 1.0, 0.7, 1.0),
                        FractalHeightMap.Type.SIMPLEX, 5, System.nanoTime()
                        ).setNormalize(true), true);
                //0.5);

        //);

        hm1 = PixelatedSampler.pixelatedHeightmap(hm1, 50, 50, 100, 100, PixelatedSampler.Mode.CORNER, 1, hm1);

        /*hm1 = HeightMaps.mult(hm1,
                GNoise.simplexNoise(0.01, 1.0, 1.0).toModded().addRemap(0, 1, 0.3, 1.0),
                0.5);*/

        //hm1 = new WarpedHeightMap(hm1).frequencyModulation(HeightMaps.cos(0.01, 0.02, 1, 1), 0.7, 1.3);
        //hm1 = hm1.toDistorted().frequencyModulation(dSq, 0.99999, 1.0);
        //hm1 = hm1.toDistorted().frequencyModulation(GNoise.simplexNoise(0.012, 1.0, 1.0), 1.1, 0.9);

        Sampler<Double> texture = hm1;

        double warpAmount = 10;
        int recursionSteps = 2;

        double randomOffset = 20;

        double f = Math.random() * Math.PI * 2;
        double r = 0.05 * MathUtils.random(-Math.PI, Math.PI);
        double rr = 0.02 * MathUtils.random(-Math.PI, Math.PI);

        for(int i = 0; i < recursionSteps; i++) {
            texture =
                    //texture.domainWarp(texture, 10);
                    new WarpedHeightMap(texture).domainWarp(
                            //GNoise.simplexNoise(0.02, 1.0, 1.0),
                            //hm1,
                            //hm1.toModded().addMod(1),
                            //HeightMaps.cos(0.003, 0.01, 1.0, 1),

                            //hm1.toModded().addOffset(f, true),
                            texture,
                            new WarpedHeightMap(texture).rotate(center, f),
                            /*HeightMaps.offset(
                                    texture,
                                    randomOffset*Math.random(), randomOffset*Math.random()),*/
                            //texture,
                            -warpAmount, warpAmount);

            f += r;
            r += rr;
        }

        texture = new ModdedHeightMap(texture).addOffset(new WarpedHeightMap(texture).rotate(center, Math.random() * Math.PI * 2), true);

        texture = new WarpedHeightMap(texture).rotate(center, Math.random() * Math.PI * 2);

        Color c1 = Colors.random(Colors.HSB_SPACE, new double[]{0.0, 0.2, 1.0}, new double[]{1.0, 0.5, 1.0});
        Color c2 = Colors.random(Colors.HSB_SPACE, new double[]{0.0, 0.1, 0.2}, new double[]{1.0, 0.4, 0.5});

        FadingHeightMapDrawer drawer = new FadingHeightMapDrawer(texture, c1.toRGB(), c2.toRGB(), 0, 0, PApplet.HSB);
        drawer.setSuperSampling(superSampling);
        drawer.draw(canvas, frequency);

        return canvas;
    }
}
