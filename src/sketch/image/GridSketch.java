package sketch.image;

import color.colors.Colors;
import color.palette.drawer.ComparativePaletteDrawer;
import color.palette.drawer.PaletteDrawer;
import color.palette.palettes.balanced.BalancedPalette;
import color.palette.palettes.balanced.BasicPalette;
import color.space.ColorSpace;
import image.GraphicsCombiner;
import image.GridMultiGraphics;
import image.data.ImageData;
import image.data.SortedImageData;
import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PGraphics;
import processing.core.PImage;
import render.Drawer;
import render.SampleDrawer;
import sampling.GraphicsSampler;
import sampling.Sampler;
import sampling.domainWarp.SimpleDomainWarp;
import sampling.domainWarp.SourceDomainWarp;
import sketch.Sketch;
import util.ArrayAndListTools;
import util.file.FileUtils;
import util.geometry.Rectangle;
import util.geometry.divider.SimpleDivider;
import util.math.MathUtils;
import util.vector.Vector;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class GridSketch implements Sketch {
    private final PApplet p;
    private List<PImage> images;
    private List<ImageData> imageData;
    private SortedImageData sortedImageData;

    private final Rectangle bounds;
    private final int rows, columns;

    public GridSketch(PApplet p, int rows, int columns) {
        this.p = p;
        loadImages(false);

        sortedImageData = new SortedImageData(imageData);
        sortedImageData.sortRandom();
        sortedImageData.sortHue();
        sortedImageData.sortSaturation();
        sortedImageData.sortBrightness();

        sortedImageData.sortDominantColorHue();
        sortedImageData.sortDominantColorSaturation();
        sortedImageData.sortDominantColorBrightness();

        bounds = new Rectangle(columns * images.get(0).width, rows * images.get(0).height);
        this.rows = rows;
        this.columns = columns;
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
        File[] paths = FileUtils.listFiles("sourceImages/pattern/", ".png");
        images = new ArrayList<>(paths.length);
        imageData = new ArrayList<>(paths.length);

        int i = 0;
        for(File file : paths) {
            PImage image = p.loadImage(file.getPath());
            images.add(image);

            ImageData data = loadImageData(file.getPath(), image, 0.5, 5, 20, reloadData);
            if(imageData != null) imageData.add(data);

            System.out.println(100 * ((double)i / paths.length) + "%");

            i++;
        }
    }

    private PImage[] getImages(String sortType, int numberOfImages, int offset, boolean reversed) {
        PImage[] images = new PImage[numberOfImages];
        List<Integer> indices = sortedImageData.getSort(sortType);
        for(int i = 0; i < numberOfImages; i++) {
            images[i] = this.images.get(
                    indices.get(
                            reversed ? indices.size() - (i + offset) - 1 : (i + offset)
                    )
            );
        }
        return images;
    }

    private List<Sampler<Integer>> getCombiners(PImage[] images, int numberOfCombiners) {
        List<Sampler<Integer>> combiners = new ArrayList<>(numberOfCombiners);

        //List<List<PImage>> permutations = ArrayAndListTools.getPermutations(images);


        for(int i = 0; i < numberOfCombiners; i++) {

            GraphicsCombiner combiner = new GraphicsCombiner(
                    GraphicsCombiner.intervalAndControl(i % images.length, c -> Colors.YCC_SPACE.getComponents(c)[0]),
                    GraphicsSampler.WrapMode.MIRROR_WRAP,
                    //permutations.get((i * 101) % permutations.size())
                    images
            );

            combiners.add(combiner);
        }

        return combiners;
    }

    @Override
    public PGraphics draw(PGraphics canvas, double frequency) {
        int numberOfImages =
                //images.size();
                rows * columns;

        PImage[] images = getImages("dominantColorHue", numberOfImages, (int) MathUtils.random(0, this.images.size()), false);
        Sampler<Integer> g1 = new GridMultiGraphics(rows, columns, (int)bounds.width, (int)bounds.height,
                images
                //getCombiners(images, 16)
        );
        g1 = new SimpleDomainWarp<>(g1)
                .add(p -> p.add(new Vector(-200, -50)))
                ;

        numberOfImages = rows * columns;
        images = getImages("hue", numberOfImages, (int) MathUtils.random(0, numberOfImages), false);
        Sampler<Integer> g2 = new GridMultiGraphics(rows, columns, (int)bounds.width, (int)bounds.height,
                images
        );

        g2 = new SimpleDomainWarp<>(g2)
                .add(p -> p.add(new Vector(100, 100)))
                ;

        //numberOfImages = 1;
        //images = getImages("hue", numberOfImages, (int) MathUtils.random(0, numberOfImages), true);
        Sampler<Integer> g3 = new GridMultiGraphics(rows, columns, (int)bounds.width, (int)bounds.height,
                images
        );
        g3 = new SimpleDomainWarp<>(g3)
                .add(p -> p.add(new Vector(50, -200)))
                ;


        GraphicsCombiner combiner = new GraphicsCombiner(
                GraphicsCombiner.intervalAndControl(0, c -> Colors.YCC_SPACE.getComponents(c)[0]),
                //permutations.get((i * 101) % permutations.size())
                List.of(
                        g1, g2, g3
                )
        );

        ColorSpace cs = Colors.HSB_SPACE;
        int rotationIndex = 2;
        int amountIndex = 2;

        double rotationMult = 1;

        double min = 0;
        double max = 400;

        /*Sampler<Integer> combiner =
                new SourceDomainWarp<>(
                        g1, g2, g3,
                        (x, y, c) -> cs.getComponents(c)[rotationIndex] * rotationMult,
                        (x, y, c) -> cs.getComponents(c)[amountIndex],
                        min, max
                );*/

        Drawer drawer = new SampleDrawer(combiner, bounds.width, bounds.height, new Vector());

        canvas.beginDraw();
        drawer.draw(canvas);
        canvas.endDraw();

        System.out.println("Printed!");

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
