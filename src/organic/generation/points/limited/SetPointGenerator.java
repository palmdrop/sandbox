package organic.generation.points.limited;

import organic.generation.AbstractSeededRandom;
import organic.generation.points.PointGenerator;
import util.math.MathUtils;
import util.vector.Vector;

import java.util.ArrayList;
import java.util.List;

public class SetPointGenerator extends AbstractSeededRandom implements PointGenerator {
    private final List<Vector> points;

    public SetPointGenerator(Vector first, Vector... rest) {
        this(System.nanoTime(), first, rest);
    }
    public SetPointGenerator(long seed, Vector first, Vector... rest) {
        super(seed);
        points = new ArrayList<>(1 + (rest == null ? 0 : rest.length));
        points.add(first);
        if(rest != null) points.addAll(List.of(rest));
    }

    @Override
    public Vector get() {
        int index = (int) MathUtils.random(0, points.size(), random);
        return points.get(index);
    }
}
