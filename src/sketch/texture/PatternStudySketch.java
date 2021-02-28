package sketch.texture;

import color.colors.Colors;
import color.space.drawer.MapColorSpaceDrawer;
import processing.core.PApplet;
import processing.core.PGraphics;
import render.AbstractDrawer;
import render.heightMap.FadingHeightMapDrawer;
import sampling.Sampler;
import sampling.heightMap.HeightMap;
import sampling.heightMap.HeightMaps;
import sampling.heightMap.modified.WarpedHeightMap;
import sampling.patterns.FabricSurfacePattern;
import sampling.patterns.NavelFabricPattern;
import sampling.patterns.SpirePattern;
import sketch.Sketch;
import util.geometry.Circle;
import util.geometry.Rectangle;
import util.math.MathUtils;
import sampling.heightMap.modified.DynamicFractalHeightMap;
import sampling.heightMap.modified.FractalHeightMap;
import util.noise.generator.GNoise;
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
        Supplier<HeightMap> heightMapSupplier = () ->
                HeightMaps.pow(
                    HeightMaps.thresholdReverse(GNoise.simplexNoise(0.01, 1.0, 1.0), 0.3, false, true),
                    HeightMaps.constant(0.5)
                );


        HeightMap controller =
                heightMapSupplier.get();

        HeightMap base1 = new DynamicFractalHeightMap(1.0,
                1.0,
                2.0,
                HeightMaps.constant(1.0),
                0.5,
                controller,
                heightMapSupplier,
                8).setNormalize(true);

        base1 = HeightMaps.mult(
            HeightMaps.circle(new Circle(new Vector(500, 500), 300), 2, MathUtils.EasingMode.EASE_IN)
                    .toDistorted().domainWarp(base1, 50),
            base1, 0.5);

        Sampler<Double> texture =
                base1;

        double warpAmount = 50;
        int recursionSteps = 2;

        for(int i = 0; i < recursionSteps; i++) {
            texture =
                    new WarpedHeightMap(base1).domainWarp(
                            texture,
                            texture,
                            0, warpAmount);
        }


        HeightMap h =
                GNoise.simplexNoise(0.001, 1.0, 1.0)
                .toDistorted().domainWarp(texture, 100);

        double hStart = Math.random();
        double hEnd = hStart + (Math.random() > 0.5 ? -1 : 1) * MathUtils.random(0.3, 0.5);

        MapColorSpaceDrawer drawer = new MapColorSpaceDrawer(Colors.HSB_SPACE, null, canvas.width, canvas.height,
                h,
                HeightMaps.constant(1.0),
                texture::get
        );

        drawer.setComponentRange(0, hStart, hEnd);
        drawer.setComponentRange(1, 0, 0.0);
        drawer.setComponentRange(2, 0.0, 1);

        drawer.setSuperSampling(superSampling);
        drawer.draw(canvas, frequency);

        return canvas;
    }
}
