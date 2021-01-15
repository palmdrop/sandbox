package image.data;

import color.colors.Color;
import color.colors.Colors;
import image.data.color.DominantColors;
import image.data.histogram.Histogram;
import processing.core.PImage;

import java.io.Serializable;
import java.util.List;

public class ImageData implements Serializable {
    private final List<Color> colors;
    private final List<Integer> counts;
    private final Histogram hsbHistogram;
    private final Histogram rgbHistogram;

    public ImageData(PImage image, double precision, int colors, int iterations) {
        DominantColors.ColorsAndCounts colorsAndCounts = DominantColors.calculateOrdered(image, Colors.RGB_SPACE, colors, precision, iterations);
        this.colors = colorsAndCounts.dominantColors;
        this.counts = colorsAndCounts.counts;
        this.hsbHistogram = new Histogram(image, Colors.HSB_SPACE, 255);
        this.rgbHistogram = new Histogram(image, Colors.RGB_SPACE, 255);
    }

    public Histogram getHsbHistogram() {
        return hsbHistogram;
    }

    public List<Color> getColors() {
        return colors;
    }

    public List<Integer> getCounts() {
        return counts;
    }

    public Histogram getRgbHistogram() {
        return rgbHistogram;
    }
}
