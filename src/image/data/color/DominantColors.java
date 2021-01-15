package image.data.color;

import color.colors.Color;
import color.space.ColorSpace;
import processing.core.PImage;
import sampling.GraphicsSampler;
import sampling.Sampler;
import util.ArrayAndListTools;
import util.math.KMeansClustering;
import util.vector.Vector;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class DominantColors {
    public static class ColorsAndCounts {
        public final List<Color> dominantColors;
        public final List<Integer> counts;

        public ColorsAndCounts(List<Color> dominantColors, List<Integer> counts) {
            this.dominantColors = dominantColors;
            this.counts = counts;
        }
    }

    private DominantColors() {
    }

    public static ColorsAndCounts calculate(PImage image, ColorSpace colorSpace, int numberOfColors, double precision, int iterations) {
        return calculate(new GraphicsSampler(image, GraphicsSampler.WrapMode.BACKGROUND), image.width, image.height, colorSpace, numberOfColors, precision, iterations);
    }

    public static ColorsAndCounts calculate(Sampler<Integer> image, int width, int height, ColorSpace colorSpace, int numberOfColors, double precision, int iterations) {
        List<Vector> data = new ArrayList<>((int)(width * height * precision));
        double xStep = Math.min(1.0 / precision, width);
        double yStep = Math.min(1.0 / precision, height);

        for(double x = 0; x < width; x += xStep) for(double y = 0; y < height; y += yStep) {
            int color = image.get(x, y);
            Vector d = new Vector(colorSpace.getComponents(color));
            data.add(d);
        }

        KMeansClustering kMeansClustering = new KMeansClustering(numberOfColors, iterations, data);
        List<Vector> centroids = kMeansClustering.getCentroids();

        List<Color> dominantColors = new ArrayList<>(numberOfColors);
        for(int i = 0; i < numberOfColors; i++) {
            dominantColors.add(colorSpace.getColor(centroids.get(i).asArray()));
        }

        List<Integer> counts = kMeansClustering.getCounts();
        return new ColorsAndCounts(dominantColors, counts);
    }

    public static ColorsAndCounts calculateOrdered(PImage image, ColorSpace colorSpace, int numberOfColors, double precision, int iterations) {
        return order(calculate(new GraphicsSampler(image, GraphicsSampler.WrapMode.BACKGROUND), image.width, image.height, colorSpace, numberOfColors, precision, iterations));
    }

    public static ColorsAndCounts calculateOrdered(Sampler<Integer> image, int width, int height, ColorSpace colorSpace, int numberOfColors, double precision, int iterations) {
        return order(calculate(image, width, height, colorSpace, numberOfColors, precision, iterations));
    }

    private static ColorsAndCounts order(ColorsAndCounts colorsAndCounts) {
        ArrayAndListTools.sortUsing(colorsAndCounts.dominantColors, colorsAndCounts.counts, Comparator.reverseOrder());
        colorsAndCounts.counts.sort(Comparator.reverseOrder());
        return colorsAndCounts;
    }
}
