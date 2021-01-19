package sketch.image;

import color.colors.Color;
import color.colors.Colors;
import image.GraphicsCombiner;
import image.GridMultiGraphics;
import image.data.ImageData;
import image.data.SortedImageData;
import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PImage;
import render.SampleDrawer;
import sampling.CombinedSampler;
import sampling.GraphicsSampler;
import sampling.Sampler;
import sampling.domainWarp.SourceDomainWarp;
import sampling.heightMap.HeightMap;
import sketch.Sketch;
import util.ArrayAndListTools;
import util.file.FileUtils;
import util.geometry.Rectangle;
import util.math.MathUtils;
import util.vector.Vector;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Supplier;

public class PatternSketch implements Sketch {
    private final PApplet p;
    private List<PImage> images;
    private List<ImageData> imageData;
    private SortedImageData sortedImageData;

    private Rectangle bounds;

    private GraphicsSampler controller1;
    private GraphicsSampler controller2;

    private GraphicsSampler loadController() {
        return new GraphicsSampler(
                p.loadImage(
                        ArrayAndListTools.randomElement(FileUtils.listFiles(
                                "sourceImages/creatures/",
                                //"sourceImages/",
                        new String[]{".png", ".jpg", ".jpeg"})).getPath()),
                        GraphicsSampler.WrapMode.MIRROR_WRAP
                );
    }

    public PatternSketch(PApplet p) {
        this.p = p;

        controller1 = loadController();
        controller2 = loadController();
        loadImages(false);

        bounds =
                new Rectangle(controller1.getImage().width, controller1.getImage().height);

        sortedImageData = new SortedImageData(imageData);
        sortedImageData.sortHue();
        sortedImageData.sortSaturation();
        sortedImageData.sortBrightness();

        sortedImageData.sortRandom();

        sortedImageData.sortDominantColorHue();
        sortedImageData.sortDominantColorSaturation();
        sortedImageData.sortDominantColorBrightness();

    }

    private ImageData loadImageData(String path, PImage image, double precision, int colors, int iterations, boolean reload) {
        String cachePath = path + ".data";

        ImageData data = null;
        if(FileUtils.fileExists(cachePath) && !reload) {
            try {
                data = (ImageData) FileUtils.readObject(cachePath);
            } catch (IOException | ClassNotFoundException i) {
                System.out.println("Couldn't load imageData for " + path);
            }
        } else {
            data = new ImageData(image, precision, colors, iterations);
            try {
                FileUtils.writeObject(cachePath, data);
            } catch (IOException i) {
                System.out.println("Couldn't store imageData for " + path);
            }
        }

        return data;
    }


    private void loadImages(boolean reloadData) {
        File[] paths =
                FileUtils.listFiles(
                        "/home/xan/usr/pictures/dada",
                new String[]{
                        ".png", ".jpg", ".jpeg"
                });
        images = new ArrayList<>(paths.length);
        imageData = new ArrayList<>(paths.length);

        int i = 0;
        for(File file : paths) {
            PImage image = p.loadImage(file.getPath());
            if(image == null) continue;
            images.add(image);

            ImageData data = loadImageData(file.getPath(), image, 0.5, 5, 20, reloadData);
            if(imageData != null) imageData.add(data);

            System.out.println(100 * ((double)i / paths.length) + "%");

            i++;
        }

    }

    private Sampler<Integer> combine(Comparator<Integer> pixelComparator, int numberOfImages, String sort, int offset, GraphicsSampler.WrapMode wrapMode, boolean xyOffset, boolean scale) {
        return combine(GraphicsCombiner.toValueCombiner(pixelComparator), numberOfImages, sort, offset, wrapMode, xyOffset, scale);
    }

    private Sampler<Integer> combine(CombinedSampler.ValueCombiner<Integer> combiner, int numberOfImages, String sort, int offset, GraphicsSampler.WrapMode wrapMode, boolean xyOffset, boolean scale) {
        return combine(combiner.toSamplerCombiner(), numberOfImages, sort, offset, wrapMode, xyOffset, scale);
    }

    private Sampler<Integer> combine(CombinedSampler.SamplerCombiner<Integer> combiner, int numberOfImages, String sort, int offset, GraphicsSampler.WrapMode wrapMode, boolean xyOffset, boolean scale) {
        List<Integer> indices = sortedImageData.getSort(sort);
        if(indices == null) throw new IllegalArgumentException();

        List<Sampler<Integer>> samplers = new ArrayList<>(numberOfImages);

        for(int i = 0; i < numberOfImages; i++) {
            int ii = (i + offset) % indices.size();
            PImage img = this.images.get(indices.get(ii));

            double frequency = 1;

            if(scale) {
                frequency = Math.min((double)img.width/controller1.getImage().width, (double)img.height/controller1.getImage().height);
            }

            Vector vectorOffset;
            if(xyOffset) {
                vectorOffset = new Vector(MathUtils.random(-img.width, img.width), MathUtils.random(-img.height, img.height));
            } else {
                vectorOffset = new Vector();
            }
            samplers.add(new GraphicsSampler(this.images.get(indices.get(ii)), frequency, vectorOffset, wrapMode));
        }

        return new GraphicsCombiner(
                combiner,
                samplers
        );
    }

    private Sampler<Integer> combine(CombinedSampler.ValueCombiner<Integer> combiner, int numberOfImages, Supplier<Sampler<Integer>> toSampler) {
        return combine(combiner.toSamplerCombiner(), numberOfImages, toSampler);
    }

    private Sampler<Integer> combine(CombinedSampler.SamplerCombiner<Integer> combiner, int numberOfImages, Supplier<Sampler<Integer>> toSampler) {
        List<Sampler<Integer>> samplers = new ArrayList<>(numberOfImages);
        for(int i = 0; i < numberOfImages; i++) samplers.add(toSampler.get());
        return new GraphicsCombiner(
                combiner,
                samplers
        );
    }

    private Sampler<Integer> makeGrid(int rows, int columns, HeightMap value, double frequency, String sort) {
        List<Integer> indices = sortedImageData.getSort(sort);
        if(indices == null || rows < 1 || columns < 1) throw new IllegalArgumentException();

        PImage[] images = new PImage[rows * columns];

        for(int r = 0; r < rows; r++) for(int c = 0; c < columns; c++) {
            double v = value.get(frequency * r, frequency * c);
            int index = indices.get((int) MathUtils.map(v, 0, 1, 0, indices.size()));
            images[r * columns + c] = this.images.get(index);
        }

        return new GridMultiGraphics(rows, columns, controller1.getImage().width, controller1.getImage().height, images);
    }

    @Override
    public PGraphics draw(PGraphics canvas, double frequency) {
        loadController();

        canvas.beginDraw();
        Color background = Colors.random(Colors.HSB_SPACE, new double[]{0.0, 0.0, 0.0}, new double[]{1.0, 0.5, 0.15});

        Sampler<Integer> imageSampler =
                new GraphicsCombiner(
                        GraphicsCombiner.intervalAndControl(0, c -> Colors.HSB_SPACE.getComponents(c)[2], false),
                        controller1,
                        p -> Colors.HSB_SPACE.getColor(0.0, 0.0, controller1.get(p)).toRGB(),
                        p -> Colors.HSB_SPACE.getColor(0.0, 0.0, controller1.get(p)).toRGB(),
                        p -> Colors.HSB_SPACE.getColor(0.0, 0.0, controller1.get(p)).toRGB(),
                        p -> Colors.HSB_SPACE.getColor(0.0, 0.0, controller1.get(p)).toRGB(),
                        combine(
                                Colors.rgbComponentComparator(Colors.HSB_SPACE, 2, true),
                                3, "random", (int) (MathUtils.random(100)), GraphicsSampler.WrapMode.MIRROR_WRAP,
                                false, false//Math.random() > 0.5
                        ),
                        combine(
                                GraphicsCombiner.intervalAndControl(0, c -> Colors.HSB_SPACE.getComponents(c)[2]),
                                3, "random", (int) (MathUtils.random(100)), GraphicsSampler.WrapMode.MIRROR_WRAP,
                                false, false//Math.random() > 0.5
                        ),
                        combine(
                                GraphicsCombiner.intervalAndControl(0, c -> Colors.HSB_SPACE.getComponents(c)[2]),
                                3, "random", (int) (MathUtils.random(100)), GraphicsSampler.WrapMode.MIRROR_WRAP,
                                false, false//Math.random() > 0.5
                        ),
                        combine(
                                GraphicsCombiner.intervalAndControl(0, c -> Colors.HSB_SPACE.getComponents(c)[2]),
                                3, "random", (int) (MathUtils.random(100)), GraphicsSampler.WrapMode.MIRROR_WRAP,
                                false, false//Math.random() > 0.5
                        )
                    );

        Sampler<Integer> toRender
                //= imageSampler;
                = new SourceDomainWarp<>(imageSampler, controller1, (x, y, v) -> Colors.brightness(v), (x, y, v) -> Colors.brightness(v), 0, MathUtils.random(40, 100));

        SampleDrawer drawer = new SampleDrawer(toRender, bounds.width, bounds.height, new Vector(0, 0));
        drawer.draw(canvas);

        canvas.endDraw();

        return canvas;
    }

    @Override
    public Rectangle getBounds() {
        return bounds;
    }

    @Override
    public void setBounds(Rectangle bounds) {
    }
}
