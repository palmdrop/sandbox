package image;

import sampling.Sampler;
import util.vector.Vector;

import java.util.function.Function;

public class PixelModifier implements Sampler<Integer> {
    private final Sampler<Integer> source;
    private final Function<Integer, Integer> modification;

    public PixelModifier(Sampler<Integer> source, Function<Integer, Integer> modification) {
        this.source = source;
        this.modification = modification;
    }

    @Override
    public Integer get(Vector point) {
        return modification.apply(source.get(point));
    }
}
