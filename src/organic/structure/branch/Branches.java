package organic.structure.branch;

import organic.Component;
import organic.generation.points.combined.WeightedPointGenerator;
import organic.structure.segment.Segment;
import organic.structure.segment.Segments;
import organic.structure.segment.SimpleSegment;
import util.math.MathUtils;
import util.vector.ReadVector;
import util.vector.Vector;

import java.util.function.Function;

public class Branches {
    public static Branch<?> gaussianAngleOffset(Branch<?> root, double deviation) {
        return angleOffset(root, i -> MathUtils.randomGaussian(deviation));
    }
    public static Branch<?> randomAngleOffset(Branch<?> root, double minAmount, double maxAmount) {
        return angleOffset(root, i -> MathUtils.random(minAmount, maxAmount));
    }

    public static Branch<?> angleOffset(Branch<?> root, Function<Integer, Double> offsetFunction) {
        return angleOffset(root, offsetFunction, 0);
    }

    public static Branch<?> angleOffset(Branch<?> root, Function<Integer, Double> offsetFunction, int iterator) {
        double angle = root.getAngle();
        angle += offsetFunction.apply(iterator);
        root.setAngle(angle);

        iterator++;
        for(Branch<?> child : root.children()) {
            angleOffset(child, offsetFunction, iterator);
        }

        return root;
    }

    public static <T extends Component> Branch<T> curve(Branch<T> root, double angle, double angleIncrement, int steps, double branchLength) {
        Branch<T> current = root;
        for(int i = 0; i < steps; i++) {
            Branch<T> child = new SimpleBranch<>(angle, branchLength);
            current.children().add(child);

            angle += angleIncrement;
            current = child;
        }
        return root;
    }

    public static <T extends Component> Segment<T> toSegments(Branch<T> root, double baseAngle, ReadVector origin) {
        double angle = root.getAngle() + baseAngle;
        Vector dir = Vector.fromAngle(angle);
        Segment<T> segmentRoot = new SimpleSegment<>(origin, dir);

        Vector position = Vector.add(origin, Vector.mult(dir, root.getLength()));

        for(Branch<T> child : root.children()) {
            segmentRoot.children().add(toSegments(child, angle, position));
        }

        if(root.children().isEmpty()) {
            segmentRoot.children().add(new SimpleSegment<>(position, dir));
        }

        return segmentRoot;
    }

    public static WeightedPointGenerator toPointGenerator(Branch<?> root, ReadVector origin) {
        return Segments.toPointGenerator(toSegments(root, 0, origin));
    }
}
