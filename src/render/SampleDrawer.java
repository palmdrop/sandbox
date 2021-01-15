package render;

import color.colors.Color;
import sampling.GraphicsSampler;
import sampling.Sampler;
import util.vector.Vector;

public class SampleDrawer extends AbstractPixelDrawer implements Drawer {
    private final Sampler<Integer> sampler;

    public SampleDrawer(GraphicsSampler sampler, Vector offset) {
        this(sampler, sampler.getImage().width, sampler.getImage().height, offset);
    }

    public SampleDrawer(Sampler<Integer> sampler, double xMultiplier, double yMultiplier, Vector offset) {
        super(null, offset, xMultiplier, yMultiplier, 0.0);
        this.sampler = sampler;
    }

    public SampleDrawer(Sampler<Integer> sampler) {
        this(sampler, 1.0, 1.0, new Vector());
    }

    @Override
    protected int getColor(Vector p) {
        return sampler.get(p);
    }

    public static SampleDrawer fromColorSampler(Sampler<Color> sampler) {
        return fromColorSampler(sampler, 1.0, 1.0, new Vector());
    }
    public static SampleDrawer fromColorSampler(Sampler<Color> sampler, double xMultiplier, double yMultiplier, Vector offset) {
        Sampler<Integer> rgbSampler = p -> sampler.get(p).toRGB();
        return new SampleDrawer(rgbSampler, xMultiplier, yMultiplier, offset);
    }
}
