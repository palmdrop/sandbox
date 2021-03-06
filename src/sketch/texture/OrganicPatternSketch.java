package sketch.texture;

import color.colors.Colors;
import color.space.drawer.MapColorSpaceDrawer;
import processing.core.PGraphics;
import render.AbstractDrawer;
import sampling.Sampler;
import sampling.heightMap.HeightMap;
import sampling.heightMap.HeightMaps;
import sampling.heightMap.modified.WarpedHeightMap;
import sketch.Sketch;
import util.geometry.Rectangle;
import util.math.MathUtils;
import sampling.heightMap.modified.DynamicFractalHeightMap;
import sampling.heightMap.modified.FractalHeightMap;
import util.noise.generator.GNoise;

import java.util.function.Supplier;

public class OrganicPatternSketch extends AbstractDrawer implements Sketch {
    public OrganicPatternSketch(Rectangle bounds) {
        super(bounds);
    }

    public PGraphics drawLayer(PGraphics canvas, double frequency, boolean superSampling, boolean alpha) {
        double f = 0.08;

        Supplier<HeightMap> heightMapSupplier = () -> {
            HeightMap h = (x, y) -> {
                x *= f;
                y *= f;

                x -= f * 500;
                x = MathUtils.limit(x, -Math.PI, Math.PI);
                y -= f * 500;
                y = MathUtils.limit(y, -Math.PI, Math.PI);

                return Math.pow(0.5 + Math.cos(x)/2, 5.5);
            };

            return h.toDistorted().domainWarp(
                    //GNoise.simplexNoise(0.005, 1.0, 1.0)
                    new DynamicFractalHeightMap(0.002, 1.0,
                            1.8, HeightMaps.constant(1.0),
                            0.5, HeightMaps.constant(1.0),
                            FractalHeightMap.Type.SIMPLEX, 8, (long) (Math.random() * 10000))
                    , 200);
        };

        HeightMap base =
                heightMapSupplier.get();
                /*HeightMaps.sub(HeightMaps.sub(
                        heightMapSupplier.get(),
                        heightMapSupplier.get()
                        //heightMapSupplier.get()
                ), heightMapSupplier.get());*/

        Sampler<Double> texture = base;

        double warpAmount = 5;
        int recursionSteps = 1;

        for(int i = 0; i < recursionSteps; i++) {
            texture =
                    new WarpedHeightMap(texture).domainWarp(
                            texture,
                            //texture,
                            //heightMapSupplier.get(),
                            HeightMaps.constant(1),
                            //,
                            warpAmount);
        }

        HeightMap h =
                GNoise.simplexNoise(0.002, 1.0, 1.0)
                        .toDistorted().domainWarp(texture, 100);

        MapColorSpaceDrawer drawer = new MapColorSpaceDrawer(Colors.HSB_SPACE, null, canvas.width, canvas.height,
                h,
                HeightMaps.constant(1.0),
                texture::get
        );

        if(alpha) drawer.addAlpha(HeightMaps.pow(texture, HeightMaps.constant(0.5)));

        drawer.blend(true);

        double hStart = Math.random();
        double hEnd = hStart + (Math.random() > 0.5 ? -1 : 1) * MathUtils.random(0.4, 0.55);

        drawer.setComponentRange(0, hStart, hEnd);
        drawer.setComponentRange(1, 0, 0.2);
        drawer.setComponentRange(2, 0.0, 1);

        drawer.setSuperSampling(superSampling);
        drawer.draw(canvas, frequency);

        return canvas;

    }

    @Override
    public PGraphics draw(PGraphics canvas, double frequency) {
        return draw(canvas, frequency, false);
    }

    public PGraphics draw(PGraphics canvas, double frequency, boolean superSampling) {
        drawLayer(canvas, frequency, superSampling, false);
        //drawLayer(canvas, frequency, superSampling, true);
        return canvas;
    }
}
