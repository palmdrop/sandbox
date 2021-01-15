package image.data.histogram;

import color.space.ColorSpace;
import processing.core.PImage;
import sampling.GraphicsSampler;
import sampling.Sampler;
import util.math.MathUtils;

import java.io.Serializable;

public class Histogram implements Serializable {
    private final int detail;
    private final int[][] histogram;
    private final double[] componentNormalizedSum;
    private final int[] componentMinIndex;
    private final int[] componentMaxIndex;

    public Histogram(PImage image, ColorSpace colorSpace, int detail) {
        this(new GraphicsSampler(image, GraphicsSampler.WrapMode.BACKGROUND), image.width, image.height, colorSpace, detail);
    }

    public Histogram(Sampler<Integer> image, int width, int height, ColorSpace colorSpace, int detail) {
        this.detail = detail;
        this.histogram = new int[colorSpace.getNumberOfComponents()][detail];
        this.componentNormalizedSum = new double[colorSpace.getNumberOfComponents()];

        this.componentMinIndex = new int[colorSpace.getNumberOfComponents()];
        this.componentMaxIndex = new int[colorSpace.getNumberOfComponents()];

        analyze(image, width, height, colorSpace);
    }

    private void analyze(Sampler<Integer> image, int width, int height, ColorSpace colorSpace) {
        for(int x = 0; x < width; x++) for(int y = 0; y < height; y++) {
            double[] components = colorSpace.getComponents(image.get(x, y));
            for(int j = 0; j < colorSpace.getNumberOfComponents(); j++) {
                int index = (int) MathUtils.map(components[j], 0, 1, 0, detail - 1);
                histogram[j][index]++;
                componentNormalizedSum[j] += components[j] / (width * height);
            }
        }

        for(int index = 0; index < detail; index++) {
            for (int j = 0; j < colorSpace.getNumberOfComponents(); j++) {
                if(histogram[j][index] < histogram[j][componentMinIndex[j]]) {
                    componentMinIndex[j] = index;
                }
                if(histogram[j][index] > histogram[j][componentMaxIndex[j]]) {
                    componentMaxIndex[j] = index;
                }
            }
        }
    }

    public double getDominantComponentValue(int component) {
        return (double)componentMaxIndex[component] / detail;
    }

    public int getValue(int component, int index) {
        return histogram[component][index];
    }

    public int getMaxValue(int component) {
        return histogram[component][componentMaxIndex[component]];
    }

    public int getMinValue(int component) {
        return histogram[component][componentMinIndex[component]];
    }

    public int getDetail() {
        return detail;
    }

    public double getComponentNormalizedSum(int component) {
        return componentNormalizedSum[component];
    }
}
