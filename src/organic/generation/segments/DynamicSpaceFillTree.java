package organic.generation.segments;

import organic.Component;
import organic.generation.Points;
import organic.structure.segment.Segment;
import organic.structure.segment.SimpleSegment;
import sampling.Sampler;
import util.geometry.Rectangle;
import util.math.MathUtils;
import util.space.tree.QuadTree;
import util.vector.ReadVector;
import util.vector.Vector;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DynamicSpaceFillTree<T extends Component> {

    //TODO make dynamic, all fields are variable using different heightmaps!!!!!!!!!!!!!!!!!
    private static final int QUAD_TREE_CAPACITY = 4;

    private final double[] minDistance;
    private final Sampler<Double> minDistanceController;

    private final double[] maxDistance;
    private final Sampler<Double> maxDistanceController;

    private final double[] dynamics;
    private final Sampler<Double> dynamicsController;

    private final double[] stepSize;
    private final Sampler<Double> stepSizeController;

    private final double[] randomDeviation;
    private final Sampler<Double> randomDeviationController;

    private double getValue(double[] values, Sampler<Double> controller, Vector point) {
        double control = controller.get(point);
        int index = (int) ((values.length - 1) * control);
        if(index == values.length - 1) return values[values.length - 1];
        double start = values[index];
        double stop = values[index + 1];
        return MathUtils.map(control, (double)index / (values.length - 1), (double)(index + 1) / (values.length - 1), start, stop);
    }

    public DynamicSpaceFillTree(double[] minDistance, Sampler<Double> minDistanceController,
                                double[] maxDistance, Sampler<Double> maxDistanceController,
                                double[] dynamics, Sampler<Double> dynamicsController,
                                double[] stepSize, Sampler<Double> stepSizeController,
                                double[] randomDeviation, Sampler<Double> randomDeviationController) {
        this.minDistance = minDistance;
        this.minDistanceController = minDistanceController;
        this.maxDistance = maxDistance;
        this.maxDistanceController = maxDistanceController;
        this.dynamics = dynamics;
        this.dynamicsController = dynamicsController;
        this.stepSize = stepSize;
        this.stepSizeController = stepSizeController;
        this.randomDeviation = randomDeviation;
        this.randomDeviationController = randomDeviationController;
    }

    private class SegmentData {
        int interactions;
        Vector newDir;
        Segment<T> segment;

        SegmentData(Segment<T> segment) {
            this.newDir = new Vector(segment.getDirection());
            interactions = 1;
            this.segment = segment;
        }

        void normalize() {
            newDir.div(interactions);
        }

        void reset() {
            newDir = new Vector(segment.getDirection());
            interactions = 1;
        }
    }

    private QuadTree<SegmentData> segments;
    private List<Vector> leaves;
    private List<Vector> leavesToDelete;
    private Rectangle bounds;

    public Segment<T> generate(ReadVector origin, double startAngle, List<Vector> leaves, int iterations) {
        this.exhausted = false;
        this.bounds = Points.getBounds(leaves);
        this.leaves = leaves;
        this.leavesToDelete = new ArrayList<>();

        Segment<T> root = new SimpleSegment<>(origin, Vector.fromAngle(startAngle));
        segments = new QuadTree<>(bounds, QUAD_TREE_CAPACITY);
        segments.insert(origin, new SegmentData(root));

        for (int i = 0; i < iterations && !exhausted; i++) grow();

        return root;
    }

    private boolean exhausted = false;
    public boolean grow() {
        if(exhausted) return true;
        boolean foundOne = false;

        QuadTree<SegmentData> nextSegmentData = new QuadTree<>(bounds, QUAD_TREE_CAPACITY);
        Set<SegmentData> interactingSegmentData = new HashSet<>();


        for(Vector leaf : leaves) {
            SegmentData segmentData = closestSegment(leaf);

            if(segmentData == null) continue;
            foundOne = true;

            double minDistance = getValue(this.minDistance, minDistanceController, segmentData.segment.getPosition());
            double dynamics = getValue(this.dynamics, dynamicsController, segmentData.segment.getPosition());

            double distSq = Vector.distSq(leaf, segmentData.segment.getPosition());
            if(distSq < minDistance * minDistance) {
                leavesToDelete.add(leaf);
            } else {
                Vector dir =
                        Vector.interpolate(
                                segmentData.segment.getDirection(),
                                Vector.sub(leaf, segmentData.segment.getPosition()).normalize(),
                                dynamics);

                segmentData.newDir.add(dir);
                segmentData.interactions++;
                interactingSegmentData.add(segmentData);
            }
        }

        if(!foundOne) {
            exhausted = true;
            return true;
        }

        leaves.removeAll(leavesToDelete);
        leavesToDelete.clear();

        for(SegmentData segmentData : interactingSegmentData) {
            segmentData.normalize();

            double randomDeviation = getValue(this.randomDeviation, randomDeviationController, segmentData.segment.getPosition());
            double stepSize = getValue(this.stepSize, stepSizeController, segmentData.segment.getPosition());

            Vector newPos = Vector.add(segmentData.segment.getPosition(), Vector.mult(segmentData.newDir, stepSize));
            newPos.add(Vector.random(-randomDeviation, randomDeviation, -randomDeviation, randomDeviation));

            Segment<T> newSegment = new SimpleSegment<>(newPos, segmentData.newDir);

            segmentData.reset();
            segmentData.segment.children().add(newSegment);

            nextSegmentData.insert(segmentData.segment.getPosition(), segmentData);
            nextSegmentData.insert(newSegment.getPosition(), new SegmentData(newSegment));
        }

        this.segments = nextSegmentData;
        return false;
    }

    private SegmentData closestSegment(Vector leaf) {
        SegmentData closest = null;

        double maxDistance = getValue(this.maxDistance, maxDistanceController, leaf);

        List<SegmentData> segments = this.segments.query(leaf, maxDistance);

        double minDistSq = maxDistance * maxDistance;
        for(SegmentData segmentData : segments) {
            double distSq = Vector.distSq(segmentData.segment.getPosition(), leaf);
            if(distSq < minDistSq) {
                closest = segmentData;
                minDistSq = distSq;
            }
        }
        return closest;
    }

    public boolean isExhausted() {
        return exhausted;
    }
}
