package color.space.sampler;

import color.colors.Color;
import color.space.ColorSpace;
import sampling.Sampler;
import sampling.heightMap.HeightMaps;
import util.vector.Vector;

import java.util.List;

public class ColorSpaceSampler implements Sampler<Color> {
    public static final Sampler<Double> zero = HeightMaps.constant(0.0);
    public static final Sampler<Double> one = HeightMaps.constant(1.0);
    public static Sampler<Double> constant(double constant) {
        return HeightMaps.constant(constant);
    }

    private final ColorSpace colorSpace;
    private final List<Sampler<Double>> componentSamplers;

    public ColorSpaceSampler(ColorSpace colorSpace, Sampler<Double>... componentSamplers) {
        this.colorSpace = colorSpace;
        this.componentSamplers = List.of(componentSamplers);
    }

    @Override
    public Color get(Vector point) {
        double[] components = new double[colorSpace.getNumberOfComponents()];
        for(int i = 0; i < components.length; i++) {
            components[i] = componentSamplers.get(i).get(point);
        }
        return colorSpace.getColor(components);
    }
}
