package sketch.texture;

import color.colors.Colors;
import color.space.drawer.MapColorSpaceDrawer;
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
import util.noise.generator.GNoise;
import util.noise.type.CellularNoise;

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
                    HeightMaps.thresholdReverse(
                        GNoise.simplexNoise(0.001, 1.0, 1.0), 0.3, false, true),
                        HeightMaps.constant(0.15)
                );


        /*HeightMap warp = new GraphicsHeightMap(
                canvas.parent.loadImage(
                        ArrayAndListTools.randomElement(FileUtils.listFiles(
                                "sourceImages/",
                                new String[]{".png", ".jpg", ".jpeg"})).getPath()),
                GraphicsSampler.WrapMode.MIRROR_WRAP,
                Colors::brightness
        );*/

        HeightMap controller =
                heightMapSupplier.get();
                //HeightMaps.stretch(heightMapSupplier.get(), 3, 3);

        HeightMap base1 = new ComplexFractalHeightMap(1.0,
                1.0,
                1.5,
                HeightMaps.constant(1.0),
                0.8,
                HeightMaps.stretch(controller, 2, 	2),
                heightMapSupplier,
                8).setNormalize(true);


        Supplier<HeightMap> shapeSupplier =
                () -> {
                    double f = MathUtils.random(0.0001, 0.02);
                    return HeightMaps.stretch(
                            CellularNoise.getInstance().toModded().addReverse(),
                            f, f);
                };

        Sampler<Double> shape =
                HeightMaps.add(shapeSupplier.get(), shapeSupplier.get(), shapeSupplier.get())
                .toDistorted()
                .domainWarp(
                        base1,
                        50
                        );


        //base1 =
                //HeightMaps.add(shape, HeightMaps.mult(base1, 0.2));
                //HeightMaps.mult(shape, base1, 0.5);

        Sampler<Double> texture =
                base1;

        double warpAmount = 50;
        int recursionSteps = 2;

        double rotation = Math.random() * Math.PI * 2;
        for(int i = 0; i < recursionSteps; i++) {
            texture =
                    new WarpedHeightMap(base1).domainWarp(
                            texture,
                            HeightMaps.constant(1),
                            0, warpAmount);

            //texture = HeightMaps.pow(texture, HeightMaps.constant(0.7));
        }

        texture = new NavelFabricPattern(
                0.002,
                0.1,
                0.2,
                1.5,
                0.8,
                50,
                true
        );


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
        drawer.setComponentRange(1, 0, 0.28);
        drawer.setComponentRange(2, 0.0, 1);

        drawer.setSuperSampling(superSampling);
        drawer.draw(canvas, frequency * 1.0);

        return canvas;
    }
}
