package sampling;

import processing.core.PGraphics;
import processing.core.PImage;
import sampling.heightMap.HeightMap;
import util.geometry.Rectangle;
import util.math.MathUtils;

import java.util.function.Function;

public class GraphicsHeightMap implements HeightMap {
    private final GraphicsSampler sampler;
    private final Function<Integer, Double> converter;
    private final Rectangle range;

    public GraphicsHeightMap(GraphicsSampler graphics, Function<Integer, Double> converter, Rectangle range) {
        this.sampler = graphics;
        this.converter = converter;
        this.range = range;
    }

    public GraphicsHeightMap(PImage graphics, GraphicsSampler.WrapMode mode, Function<Integer, Double> converter) {
        this(new GraphicsSampler(graphics, mode), converter, new Rectangle(0, 0, graphics.width, graphics.height));
    }

    @Override
    public Double get(double x, double y) {
        //x = MathUtils.map(x, )
        //x += range.width/2;
        //y += range.height/2;

        int rgb = sampler.get(x, y);
        double v = converter.apply(rgb);
        return v;
    }

    public PImage getImage() {
        return sampler.getImage();
    }
}
