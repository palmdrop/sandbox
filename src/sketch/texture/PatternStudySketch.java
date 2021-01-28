package sketch.texture;

import color.colors.Colors;
import color.space.drawer.MapColorSpaceDrawer;
import processing.core.PApplet;
import processing.core.PGraphics;
import render.AbstractDrawer;
import sampling.Sampler;
import sampling.heightMap.HeightMap;
import sampling.heightMap.HeightMaps;
import sampling.heightMap.modified.WarpedHeightMap;
import sampling.patterns.NavelFabricPattern;
import sketch.Sketch;
import util.geometry.Rectangle;
import util.math.MathUtils;
import util.noise.ComplexFractalHeightMap;
import util.noise.FractalHeightMap;
import util.noise.generator.GNoise;
import util.noise.type.CellularNoise;
import util.vector.Vector;

import java.util.function.Supplier;

public class PatternStudySketch extends AbstractDrawer implements Sketch {
    public PatternStudySketch(Rectangle bounds) {
        super(bounds);
    }

    @Override
    public PGraphics draw(PGraphics canvas, double frequency) {
        return draw(canvas, frequency, false);
    }

    public PGraphics draw(PGraphics canvas, double frequency, boolean superSampling) {
        //double f = 0.01 + Math.random() * 0.1;
        double f = 0.1;
        double r =
                //0;
                Math.random() * PApplet.PI * 2;

        Supplier<HeightMap> heightMapSupplier = () -> {
            HeightMap h = HeightMaps.sin(
                    Math.cos(r) * f,
                    Math.sin(r) * f, 0.0, 1.0);
            return HeightMaps.pow(h, HeightMaps.constant(0.5));
        };

                /*HeightMaps.pow(
                    HeightMaps.tan(
                            Math.cos(r) * f,
                            Math.sin(r) * f, 0.0, 1.0)
                        .toModded().addSmoothMod(2.5)
                        ,
                    HeightMaps.constant(0.6)
                );*/

        HeightMap controller =
                heightMapSupplier.get();

        HeightMap base1 = new ComplexFractalHeightMap(1.0,
                1.0,
                1.5,
                HeightMaps.constant(1.0),
                0.5,
                HeightMaps.stretch(controller, 2, 	2),
                heightMapSupplier,
                1).setNormalize(true);

        base1 = base1.toDistorted()
        .domainWarp(
                //base1,
                //heightMapSupplier.get()
                //GNoise.simplexNoise(0.01, 1.0, 1.0)
                new FractalHeightMap(
                        0.01,
                        1.0,
                        1.7,
                        0.46,
                        FractalHeightMap.Type.SIMPLEX,
                        8,
                        System.currentTimeMillis()
                ),
                GNoise.simplexNoise(0.01, 1.0, 1.0)
                , 0, 10)
                //.rotate(new Vector(500, 500), 2, 0.2)
                //.sin(Math.random() * Math.PI * 2, 0.5, 0.5, 1)
        ;

        Sampler<Double> texture =
                base1;

        double warpAmount = 15;
        int recursionSteps = 3;

        for(int i = 0; i < recursionSteps; i++) {
            texture =
                    new WarpedHeightMap(base1).domainWarp(
                            texture,
                            //HeightMaps.constant(1),
                            texture,
                            0, warpAmount);
        }

        HeightMap h =
                GNoise.simplexNoise(0.001, 1.0, 1.0)
                .toDistorted().domainWarp(texture, 100);

        MapColorSpaceDrawer drawer = new MapColorSpaceDrawer(Colors.HSB_SPACE, null, canvas.width, canvas.height,
                h,
                HeightMaps.constant(1.0),
                texture::get
        );

        double hStart = Math.random();
        double hEnd = hStart + (Math.random() > 0.5 ? -1 : 1) * MathUtils.random(0.3, 0.5);

        drawer.setComponentRange(0, hStart, hEnd);
        drawer.setComponentRange(1, 0, 0.0);
        drawer.setComponentRange(2, 0.0, 1);

        drawer.setSuperSampling(superSampling);
        drawer.draw(canvas, frequency);

        return canvas;
    }
}
