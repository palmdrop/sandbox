package sketch.image;

import color.colors.Colors;
import color.space.drawer.MapColorSpaceDrawer;
import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PImage;
import render.AbstractDrawer;
import render.SampleDrawer;
import sampling.GraphicsSampler;
import sampling.Sampler;
import sampling.domainWarp.DomainWarp;
import sampling.domainWarp.SimpleDomainWarp;
import sampling.heightMap.HeightMap;
import sampling.heightMap.HeightMaps;
import sampling.heightMap.modified.WarpedHeightMap;
import sketch.Sketch;
import util.ArrayAndListTools;
import util.file.FileUtils;
import util.geometry.Rectangle;
import util.math.MathUtils;
import util.noise.ComplexFractalHeightMap;
import util.noise.FractalHeightMap;
import util.noise.generator.GNoise;
import util.vector.Vector;

import java.util.function.Supplier;

public class BiowarpSketch implements Sketch {
    private PApplet p;
    private Rectangle bounds;
    private PImage image;

    public BiowarpSketch(PApplet p) {
        this.p = p;
        this.image =
                p.loadImage(
                        ArrayAndListTools.randomElement(FileUtils.listFiles(
                                "sourceImages/collected/",
                                new String[]{".png", ".jpg", ".jpeg"})).getPath());
        this.bounds = new Rectangle(0, 0, image.width, image.height);
    }

    @Override
    public PGraphics draw(PGraphics canvas, double frequency) {
        return draw(canvas, frequency, false);
    }

    public PGraphics draw(PGraphics canvas, double frequency, boolean superSampling) {
        double f = 20 / bounds.width;

        Supplier<HeightMap> heightMapSupplier = () -> {
            HeightMap h = (x, y) -> {
                x *= f;
                y *= f;

                x -= f * bounds.width/2;
                x = MathUtils.limit(x, -Math.PI, Math.PI);
                y -= f * bounds.height/bounds.height/2;
                y = MathUtils.limit(y, -Math.PI, Math.PI);

                return Math.pow(0.5 + Math.cos(x)/2, 5.5);
            };

            return h.toDistorted().domainWarp(
                    new ComplexFractalHeightMap(0.002, 1.0,
                            1.8, HeightMaps.constant(1.0),
                            0.5, HeightMaps.constant(1.0),
                            FractalHeightMap.Type.SIMPLEX, 8, (long) (Math.random() * 10000))
                    , 200);
        };

        Sampler<Double> texture = heightMapSupplier.get();

        double warpAmount = 5;
        int recursionSteps = 1;

        for(int i = 0; i < recursionSteps; i++) {
            texture =
                    new WarpedHeightMap(texture).domainWarp(
                            texture,
                            HeightMaps.constant(1),
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

        double hStart = Math.random();
        double hEnd = hStart + (Math.random() > 0.5 ? -1 : 1) * MathUtils.random(0.4, 0.55);

        drawer.setComponentRange(0, hStart, hEnd);
        drawer.setComponentRange(1, 0, 0.2);
        drawer.setComponentRange(2, 0.0, 1);

        drawer.setSuperSampling(superSampling);

        DomainWarp<Integer> imageSampler =
                new SimpleDomainWarp<>(new GraphicsSampler(image, GraphicsSampler.WrapMode.MIRROR_WRAP));

        imageSampler.domainWarp(texture, texture, 40);

        SampleDrawer imageDrawer = new SampleDrawer(imageSampler, bounds.width, bounds.height, new Vector());

        imageDrawer.draw(canvas);


        return canvas;
    }

    public Rectangle getBounds() {
        return bounds;
    }
    public void setBounds(Rectangle bounds) {
        this.bounds = bounds;
    }
}
