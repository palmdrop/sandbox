package sketch.texture;

import processing.core.PGraphics;
import render.AbstractDrawer;
import render.heightMap.FadingHeightMapDrawer;
import sampling.Sampler;
import sampling.heightMap.HeightMap;
import sampling.heightMap.HeightMaps;
import sampling.heightMap.modified.CombinedHeightMap;
import sampling.heightMap.modified.ModdedHeightMap;
import sampling.heightMap.modified.pixelated.DynamicPixelatedSampler;
import sampling.heightMap.modified.pixelated.PixelatedSampler;
import sampling.heightMap.modified.WarpedHeightMap;
import sketch.Sketch;
import util.geometry.Circle;
import util.geometry.Rectangle;
import util.math.MathUtils;
import util.noise.ComplexFractalHeightMap;
import util.noise.FractalHeightMap;
import util.noise.generator.GNoise;
import util.noise.type.CellularNoise;
import util.noise.type.CraterNoise;
import util.vector.Vector;

public class TestTextureSketch extends AbstractDrawer implements Sketch {
    public TestTextureSketch(Rectangle bounds) {
        super(bounds);
    }

    @Override
    public PGraphics draw(PGraphics canvas, double frequency) {
        return draw(canvas, frequency, false);
    }

    public PGraphics draw(PGraphics canvas, double frequency, boolean superSampling) {
        Vector center =
                new Vector(0, 0);

        Sampler<Double> base1 =
                new ComplexFractalHeightMap(0.003,
                        1.0,
                        2.0,
                        HeightMaps.constant(1.0),
                        0.7,
                        GNoise.simplexNoise(0.006, 1.0, 1.0).toModded().addRemap(0.0, 1.0, 0.7, 1.0),
                        FractalHeightMap.Type.SIMPLEX, 10, System.nanoTime()
                        ).setNormalize(true);

        Sampler<Double> base2 =
                new ComplexFractalHeightMap(0.003,
                        1.0,
                        2.0,
                        HeightMaps.constant(1.0),
                        0.8,
                        GNoise.simplexNoise(0.005, 1.0, 1.0).toModded().addRemap(0.0, 1.0, 0.7, 1.0),
                        FractalHeightMap.Type.SIMPLEX, 10, System.nanoTime()
                ).setNormalize(true);

        Sampler<Double> pix1
            = new DynamicPixelatedSampler<>(base1,
                50,
                50,
                GNoise.simplexNoise(0.006, 1.0, 1.0),
                0.3,
                4,
                DynamicPixelatedSampler.Mode.CORNER);

        Sampler<Double> pix2
                = new DynamicPixelatedSampler<>(base2,
                25,
                25,
                GNoise.simplexNoise(0.006, 1.0, 1.0),
                0.3,
                4,
                DynamicPixelatedSampler.Mode.CORNER);


        Sampler<Double> entity = new WarpedHeightMap(
                HeightMaps.circle(new Circle(new Vector(), 300), 2, MathUtils.EasingMode.EASE_IN_OUT)
        ).domainWarp(CellularNoise.getInstance().toDistorted().frequencyModulation(HeightMaps.constant(0.0), 0.01, 1.0), 100)
                .toModded().addRemap(0, 1, 0.0, 0.3);

        Sampler<Double> hm1 = base1;
                /*HeightMaps.sub(
                base1,
                entity
        );*/

        pix1 = new CombinedHeightMap(
                HeightMaps.circles(10, 10, 0, 0, new Vector(0.5, 0.5), 2),
                HeightMaps.checkers(100, 100, 0.0, 1.0),
                base2
                //pix1,
        );

        Sampler<Double> texture = hm1;

        double warpAmount = 100;
        int recursionSteps = 3;

        for(int i = 0; i < recursionSteps; i++) {
            texture =
                    new WarpedHeightMap(pix1).domainWarp(
                            //HeightMaps.constant(Math.random()),
                            //HeightMaps.checkers(Math.random()*100, Math.random()*100, 0.5, Math.PI / 2),
                            base1,
                            texture,
                            //pix1,
                            //pix1,
                            //texture,
                            -0, warpAmount);

            //texture = new WarpedHeightMap(texture).rotate(new Vector(), Math.random());
        }

        FadingHeightMapDrawer drawer = new FadingHeightMapDrawer(texture, 0, 0);
        drawer.setSuperSampling(superSampling);
        drawer.draw(canvas, frequency);

        return canvas;
    }
}
