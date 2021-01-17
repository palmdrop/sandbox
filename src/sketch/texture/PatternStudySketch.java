package sketch.texture;

import color.colors.Colors;
import color.fade.fades.RampFade;
import color.space.drawer.ColorSpaceDrawer;
import color.space.drawer.MapColorSpaceDrawer;
import organic.structure.segment.drawer.CircleSegmentDrawer;
import processing.core.PGraphics;
import render.AbstractDrawer;
import render.heightMap.ColorFadeHeightMapDrawer;
import render.heightMap.FadingHeightMapDrawer;
import sampling.BlurSampler;
import sampling.GraphicsHeightMap;
import sampling.GraphicsSampler;
import sampling.Sampler;
import sampling.heightMap.HeightMap;
import sampling.heightMap.HeightMaps;
import sampling.heightMap.modified.CombinedHeightMap;
import sampling.heightMap.modified.FeedbackHeightMap;
import sampling.heightMap.modified.ModdedHeightMap;
import sampling.heightMap.modified.WarpedHeightMap;
import sampling.heightMap.modified.pixelated.DynamicPixelatedSampler;
import sampling.heightMap.modified.pixelated.PixelatedSampler;
import sampling.patterns.*;
import sketch.Sketch;
import util.ArrayAndListTools;
import util.file.FileUtils;
import util.geometry.Circle;
import util.geometry.Rectangle;
import util.math.MathUtils;
import util.noise.ComplexFractalHeightMap;
import util.noise.FractalHeightMap;
import util.noise.generator.GNoise;
import util.noise.type.CellularNoise;
import util.noise.type.CraterNoise;
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
                    HeightMaps.thresholdReverse(GNoise.simplexNoise(0.003, 1.0, 1.0), 0.4, false, true),
                    HeightMaps.constant(0.6)
                );
                //GNoise.simplexNoise(0.003, 1.0, 1.0);


        HeightMap warp = new GraphicsHeightMap(
                canvas.parent.loadImage(
                        ArrayAndListTools.randomElement(FileUtils.listFiles(
                                "sourceImages/collected/",
                                new String[]{".png", ".jpg", ".jpeg"})).getPath()),
                GraphicsSampler.WrapMode.MIRROR_WRAP,
                Colors::brightness
        );

        HeightMap controller =
                //new CurlWavePattern(0.02, 0.08, 0.3, 2.0, 0.6, 2.0, 8);
                //HeightMaps.stretch(heightMapSupplier.get(), 1.5, 1.5);
                //HeightMaps.sin(0.02, 0.02, 0.0, 1.0)
                        //.toDistorted().rotate(new Vector(500, 500), 1.6, 0.5);
                heightMapSupplier.get();
                //.toDistorted().toFeedbackHeightMap(2, FeedbackHeightMap.Mode.AVERAGE);
                //warp;
                //.toDistorted().toFeedbackHeightMap(2, FeedbackHeightMap.Mode.AVERAGE);
                //HeightMaps.sin(Math.random() * 0.03, Math.random() * 0.03, 1.0, 0.5);
                //HeightMaps.grid(0.02, 0.02, 0.0, 0.0, 0.5, new Vector());
                //HeightMaps.fade(new Vector(500, 500), 0.0001);
                //HeightMaps.circles(20, 20, 0, 0, new Vector(), 1.5);
                //PixelatedSampler.pixelatedHeightmap(heightMapSupplier.get(), 10, 10, 40, 40, PixelatedSampler.Mode.CORNER, 1, heightMapSupplier.get())::get;
                //HeightMaps.grid(0.01, 0.01, Math.random(), Math.random(), 3.0, new Vector());

        HeightMap base1 = new ComplexFractalHeightMap(1.0,
                1.0,
                1.5,
                HeightMaps.constant(1.0),
                //heightMapSupplier.get().toModded().addRemap(0, 1, 0.5, 1.0),
                //HeightMaps.rects(20, 20, 10, 10, new Vector()),
                1.0,
                //heightMapSupplier.get(),
                controller,
                //GNoise.simplexNoise(0.003, 1.0, 1.5),
                //HeightMaps.sub(HeightMaps.constant(1), HeightMaps.stretch(heightMapSupplier.get(), 2, 2)),
                //HeightMaps.stretch(heightMapSupplier.get(), 2, 	2),
                //HeightMaps.circles(500, 500, 500, 500, new Vector(0, 0), 0.9),
                //HeightMaps.grid(0.05, 0.05, Math.random() * Math.PI * 2, 0, 0.1, new Vector()),
                //HeightMaps.sub(HeightMaps.constant(1), HeightMaps.circle(new Circle(500, 500, 500), 3, MathUtils.EasingMode.EASE_IN_OUT)),
                heightMapSupplier,
                8).setNormalize(true);


        Supplier<HeightMap> shapeSupplier =
                () -> {
                    double f = MathUtils.random(0.0001, 0.02);
                    return HeightMaps.stretch(
                            //CraterNoise.getInstance(),
                            CellularNoise.getInstance().toModded().addReverse(),
                            f, f);
                };

        Sampler<Double> shape =
                HeightMaps.add(shapeSupplier.get(), shapeSupplier.get(), shapeSupplier.get())
                //HeightMaps.stretch(CraterNoise.getInstance(), 0.004, 0.004)

                /*HeightMaps.mult(
                    HeightMaps.circle(new Circle(new Vector(500, 500), MathUtils.random(200, 400)), 3.0, MathUtils.EasingMode.EASE_IN_OUT ),
                    HeightMaps.stretch(controller, 2, 2),
                      0.5
                )*/
                .toDistorted()
                .domainWarp(
                        //GNoise.simplexNoise(0.006, 1.0, 1.0),
                        base1,
                        //warp::get,
                        50
                        );


        base1 =
                //HeightMaps.add(shape, HeightMaps.mult(base1, 0.2));
                HeightMaps.mult(shape, base1, 0.5);

        Sampler<Double> texture =
                base1;

        double warpAmount = 50;
        int recursionSteps = 2;


        double rotation = Math.random() * Math.PI * 2;
        for(int i = 0; i < recursionSteps; i++) {
            texture =
                    new WarpedHeightMap(texture).domainWarp(
                            //HeightMaps.constant(0.0),
                            texture,
                            //warp,
                            //HeightMaps.constant(rotation),
                            HeightMaps.constant(1),
                            //HeightMaps.sub(HeightMaps.constant(1), base1),
                            0, warpAmount);

            //texture = HeightMaps.pow(texture, HeightMaps.constant(0.7));
        }

        //TODO optimize by doing two passes and just multiplying with the row/column for each pass! less mults!!!!!!
        //texture = new BlurSampler<>(texture, (d1, w1, d2, w2) -> d1 * w1 + d2 * w2, 2, BlurSampler.kernel5x5);
        //texture =
                //new SpirePattern(0.006, 0.2, 0.2, 1.5, 0.8, 50, 1);
        //.setRecursion(3, 50);

        RampFade fade = new RampFade(MathUtils.random(-1, 1), MathUtils.random(-1, 1), 1, 0.5, 1.0, 1.0, RampFade.SatMode.DYNAMIC);

        /*texture = new SpirePattern(
                0.004,
                0.3,
                0.3,
                1.8,
                0.5,
                50,
                1
        );*/


        HeightMap h =
                //heightMapSupplier.get()
                //controller
                GNoise.simplexNoise(0.005, 1.0, 1.0)
                .toDistorted().domainWarp(texture, 100);

        MapColorSpaceDrawer drawer = new MapColorSpaceDrawer(Colors.HSB_SPACE, null, canvas.width, canvas.height,
                //HeightMaps.constant(1.0),
                //controller,
                //HeightMaps.mult(texture, GNoise.simplexNoise(0.003, 1.0, 1.0), 0.5),
                //GNoise.simplexNoise(0.001, 1.0, 1.0),
                h,
                //base1,
                //HeightMaps.sub(HeightMaps.constant(1), texture),
                //HeightMaps.sub(HeightMaps.constant(1), controller),
                //controller,
                HeightMaps.constant(1.0),
                texture::get
        );

        double hStart = Math.random();
        double hEnd = hStart + (Math.random() > 0.5 ? -1 : 1) * MathUtils.random(0.3, 0.7);

        drawer.setComponentRange(0, hStart, hEnd);
        drawer.setComponentRange(1, 0, 0.28);
        drawer.setComponentRange(2, 0.0, 1);

        drawer.setSuperSampling(superSampling);
        drawer.draw(canvas, frequency * 1.0);

        return canvas;
    }
}
