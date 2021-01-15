package image;

import processing.core.PImage;
import sampling.GraphicsSampler;
import sampling.GridMultiSampler;
import sampling.Sampler;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class GridMultiGraphics extends GridMultiSampler<Integer> {
    private static List<Sampler<Integer>> toSamplers(GraphicsSampler.WrapMode wrapMode, PImage... images) {
        return Arrays.stream(images).map(g -> new GraphicsSampler(g, wrapMode)).collect(Collectors.toList());
    }

    public GridMultiGraphics(int rows, int columns, int width, int height, PImage... images) {
        this(rows, columns, width, height, toSamplers(GraphicsSampler.WrapMode.MIRROR_WRAP, images));
    }

    public GridMultiGraphics(int rows, int columns, int width, int height, List<Sampler<Integer>> samplers) {
        super(rows, columns, width, height, samplers);
    }
}
