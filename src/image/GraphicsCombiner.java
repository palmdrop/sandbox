package image;

import color.space.ColorSpace;
import processing.core.PImage;
import sampling.CombinedSampler;
import sampling.GraphicsSampler;
import sampling.Sampler;
import util.vector.Vector;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class GraphicsCombiner extends CombinedSampler<Integer> {

    private static List<Sampler<Integer>> toSamplers(GraphicsSampler.WrapMode wrapMode, PImage... images) {
        return Arrays.stream(images).map(g -> new GraphicsSampler(g, wrapMode)).collect(Collectors.toList());
    }

    private static List<Sampler<Integer>> toSamplers(GraphicsSampler.WrapMode wrapMode, List<PImage> images) {
        return images.stream().map(g -> new GraphicsSampler(g, wrapMode)).collect(Collectors.toList());
    }

    public static ValueCombiner<Integer> toValueCombiner(Comparator<Integer> pixelComparator) {
        return s -> s.stream().sorted(pixelComparator).collect(Collectors.toList()).get(0);
    }

    public GraphicsCombiner(Comparator<Integer> pixelComparator, GraphicsSampler.WrapMode wrapMode, PImage... images) {
        this(toValueCombiner(pixelComparator), wrapMode, images);
    }
    public GraphicsCombiner(Comparator<Integer> pixelComparator, GraphicsSampler.WrapMode wrapMode, List<PImage> images) {
        this(toValueCombiner(pixelComparator), wrapMode, images);
    }
    public GraphicsCombiner(Comparator<Integer> pixelComparator, List<Sampler<Integer>> samplers) {
        this(toValueCombiner(pixelComparator), samplers);
    }
    public GraphicsCombiner(Comparator<Integer> pixelComparator, Sampler<Integer>... samplers) {
        this(toValueCombiner(pixelComparator), samplers);
    }


    public GraphicsCombiner(SamplerCombiner<Integer> combiner, GraphicsSampler.WrapMode wrapMode, PImage... images) {
        super(combiner, toSamplers(wrapMode, images));
    }
    public GraphicsCombiner(SamplerCombiner<Integer> combiner, GraphicsSampler.WrapMode wrapMode, List<PImage> images) {
        super(combiner, toSamplers(wrapMode, images));
    }
    public GraphicsCombiner(SamplerCombiner<Integer> combiner, List<Sampler<Integer>> samplers) {
        super(combiner, samplers);
    }
    public GraphicsCombiner(SamplerCombiner<Integer> combiner, Sampler<Integer>... samplers) {
        super(combiner, List.of(samplers));
    }

    public GraphicsCombiner(ValueCombiner<Integer> combiner, GraphicsSampler.WrapMode wrapMode, PImage... images) {
        this(combiner.toSamplerCombiner(), wrapMode, images);
    }
    public GraphicsCombiner(ValueCombiner<Integer> combiner, GraphicsSampler.WrapMode wrapMode, List<PImage> images) {
        this(combiner.toSamplerCombiner(), wrapMode, images);
    }

    public GraphicsCombiner(ValueCombiner<Integer> combiner, Sampler<Integer>... samplers) {
        this(combiner.toSamplerCombiner(), List.of(samplers));
    }

    public GraphicsCombiner(ValueCombiner<Integer> combiner, List<Sampler<Integer>> samplers) {
        this(combiner.toSamplerCombiner(), samplers);
    }

    @Override
    public Integer get(Vector point) {
        return super.get(point);
    }

    public static CombinedSampler.ValueCombiner<Integer> intervalAndControl(int control, Function<Integer, Double> colorFunction) {
        return intervalAndControl(control, colorFunction, false);
    }

    public static CombinedSampler.ValueCombiner<Integer> intervalAndControl(int control, Function<Integer, Double> colorFunction, boolean invert) {
        return samples -> {
            double value = colorFunction.apply(samples.get(control));
            if(invert) value = 1 - value;

            int index = 0;
            double closest = 1.0;
            for (int i = 0; i < samples.size(); i++) {
                if (i == control) continue;

                double dist = Math.abs(value - colorFunction.apply(samples.get(i)));
                if (dist < closest) {
                    closest = dist;
                    index = i;
                }
            }

            return samples.get(index);
        };
    }

    public static CombinedSampler.ValueCombiner<Integer> colorSpaceDivision(ColorSpace cs) {

        return samples -> {
            double[] components = new double[cs.getNumberOfComponents()];
            for(int i = 0; i < cs.getNumberOfComponents(); i++) {
                components[i] = cs.getComponents(samples.get(i % samples.size()))[i];
            }

            return cs.getRGB(components);
        };
    }
}
