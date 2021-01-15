package organic.structure.segment;

import organic.Component;
import organic.ComponentHolder;
import organic.generation.points.PointGenerator;
import organic.generation.points.combined.WeightedPointGenerator;
import organic.generation.points.limited.UniformLinePointGenerator;
import organic.structure.branch.Branch;
import organic.structure.branch.SimpleBranch;
import util.math.MathUtils;
import util.vector.ReadVector;
import util.vector.Vector;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

public class Segments {
    public static <T extends Component> Segment<T> toSkeleton(Segment<T> root) {
        //TODO: some sort of characteristic preservation varaible?
        //TODO: enable client to choose how rudimentary skeleton should be? but requires some sense of
        //TODO: how significant an area of the tree is...

        Segment<T> newRoot = new SimpleSegment<>(root);

        if(root.children().isEmpty()) return newRoot;
        else if(root.children().size() > 1) {
            for(Segment<T> child : root.children()) {
                newRoot.children().add(toSkeleton(child));
            }
            return newRoot;
        }

        Segment<T> current = root;
        do {
            current = current.children().get(0);
        } while(current.children().size() == 1);

        newRoot.children().add(toSkeleton(current));

        return newRoot;
    }

    public static <T extends Component> Segment<T> transpose(Segment<T> root, ReadVector amount) {
        root.setPosition(Vector.add(root.getPosition(), amount));

        for(Segment<?> child : root.children()) {
            transpose(child, amount);
        }

        return root;
    }

    public static <T extends Component> Segment<T> offsetSplit(Segment<T> root, double splitStart, double splitEnd, double splitOffset, double lengthPow) {
        Queue<Segment<T>> toProcess = new ArrayDeque<>();
        toProcess.add(root);

        while(!toProcess.isEmpty()) {
            Segment<T> current = toProcess.remove();

            List<Segment<T>> newChildren = new ArrayList<>(current.children().size());
            for (Segment<T> child : current.children()) {
                ReadVector start = current.getPosition();
                ReadVector stop = child.getPosition();
                Vector v = Vector.sub(stop, start);

                double split = MathUtils.random(splitStart, splitEnd);
                Vector splitPoint = Vector.mult(v, split).add(start);

                double offsetAmount = Math.pow(v.length(), lengthPow) * splitOffset;
                Vector newPoint = Vector.add(splitPoint, Vector.randomWithLength(MathUtils.randomGaussian(offsetAmount)));
                Vector newDir = Vector.sub(stop, newPoint).normalize();

                Segment<T> newChild = new SimpleSegment<>(newPoint, newDir);
                newChild.children().add(child);

                newChildren.add(newChild);

                toProcess.add(child);
            }
            current.children().clear();
            current.children().addAll(newChildren);
        }

        return root;
    }

    public static <T extends Component> Branch<T> toBranches(Segment<T> root, ReadVector previousPosition, double previousAngle) {
        ReadVector position = root.getPosition();
        if(previousPosition == null) {
            previousPosition = position;
        }
        ReadVector diff = Vector.sub(position, previousPosition);
        double angle = diff.angle();
        double relativeAngle = angle - previousAngle;

        Branch<T> branchRoot = new SimpleBranch<>(relativeAngle, diff.length());

        for(Segment<T> child : root.children()) {
            branchRoot.children().add(toBranches(child, position, angle));
        }

        return branchRoot;
    }

    public static WeightedPointGenerator toPointGenerator(Segment<?> root) {
        return pointGeneratorBuilder(root, new WeightedPointGenerator.Builder()).build();
    }
    private static WeightedPointGenerator.Builder pointGeneratorBuilder(Segment<?> root, WeightedPointGenerator.Builder generatorBuilder) {
        //TODO can be optimized by using branches instead, since they have length built in! no need to recalculate dist!!!
        ReadVector position = root.getPosition();
        for(Segment<?> child : root.children()) {
            ReadVector nextPosition = child.getPosition();
            PointGenerator generator = new UniformLinePointGenerator(position, nextPosition);
            double weight = Vector.dist(position, nextPosition);
            generatorBuilder.add(weight, generator);
            pointGeneratorBuilder(child, generatorBuilder);
        }

        return generatorBuilder;
    }

    public static Segment<Component> copy(Segment<?> root) {
        Segment<Component> newRoot = new SimpleSegment<>(root);
        for(Segment<?> child : root.children()) {
            newRoot.children().add(copy(child));
        }
        return newRoot;
    }
}
