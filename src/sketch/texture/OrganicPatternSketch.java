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
import sketch.Sketch;
import util.geometry.Rectangle;
import util.math.MathUtils;
import util.noise.ComplexFractalHeightMap;
import util.noise.FractalHeightMap;
import util.noise.generator.GNoise;

import java.util.function.Supplier;

public class OrganicPatternSketch extends AbstractDrawer implements Sketch {
    public OrganicPatternSketch(Rectangle bounds) {
        super(bounds);
    }

    @Override
    public PGraphics draw(PGraphics canvas, double frequency) {
        return draw(canvas, frequency, false);
    }

    public PGraphics draw(PGraphics canvas, double frequency, boolean superSampling) {
        //TODO explore long arms, biological strings, worms, etc!
        //TODO: ex long sine way warped many times, overlayed! ridged noise!
        //TODO warl slowly using lots of low frequency noise

        //TODO overlay many strings, diff opacity/color, create depth!

        //TODO string warp thingy but over an image, potentially just visibly or potentially warping the image! or both

        double f = 0.08;

        Supplier<HeightMap> heightMapSupplier = () -> {
            HeightMap h = (x, y) -> {
                x *= f;
                y *= f;

                x -= f * 500;
                x = MathUtils.limit(x, -Math.PI, Math.PI);
                y -= f * 500;
                y = MathUtils.limit(y, -Math.PI, Math.PI);

                return 0.5 + Math.pow(
                        Math.cos(x),
                        1.0) / 2;
            };

            return h.toDistorted().domainWarp(
                    //GNoise.simplexNoise(0.001, 1.0, 1.0)
                    new FractalHeightMap(0.001, 1.0, 1.8, 0.6, FractalHeightMap.Type.SIMPLEX, 8, (long) (Math.random() * 10000))
                    , 300);
        };

        HeightMap base =
                HeightMaps.sub(
                        heightMapSupplier.get(),
                        heightMapSupplier.get()
                        //heightMapSupplier.get()
                );

        //base = HeightMaps.mult(base, 1.0 / 3);
                        //.toModded().addRemap(0, 2, 0, 1);


                //heightMapSupplier.get();
                /*heightMapSupplier.get().toDistorted()
                .domainWarp(
                        //GNoise.simplexNoise(0.001, 1.0, 1.0)
                        new FractalHeightMap(0.001, 1.0, 1.8, 0.6, FractalHeightMap.Type.SIMPLEX, 8, System.currentTimeMillis())
                        , 300);*/

        Sampler<Double> texture = base;

        double warpAmount = 10;
        int recursionSteps = 1;

        for(int i = 0; i < recursionSteps; i++) {
            texture =
                    new WarpedHeightMap(texture).domainWarp(
                            //texture,
                            heightMapSupplier.get(),
                            //HeightMaps.constant(1),
                            //,
                            warpAmount);
        }

        HeightMap h =
                GNoise.simplexNoise(0.01, 1.0, 1.0)
                .toDistorted().domainWarp(texture, 100);

        MapColorSpaceDrawer drawer = new MapColorSpaceDrawer(Colors.HSB_SPACE, null, canvas.width, canvas.height,
                h,
                HeightMaps.constant(1.0),
                texture::get
        );

        double hStart = Math.random();
        double hEnd = hStart + (Math.random() > 0.5 ? -1 : 1) * MathUtils.random(0.4, 0.55);

        drawer.setComponentRange(0, hStart, hEnd);
        drawer.setComponentRange(1, 0, 0.2);
        drawer.setComponentRange(2, 0.0, 1);

        drawer.setSuperSampling(superSampling);
        drawer.draw(canvas, frequency);

        return canvas;
    }
}
