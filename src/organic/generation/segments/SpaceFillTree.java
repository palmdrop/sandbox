package organic.generation.segments;

import organic.Component;
import organic.generation.Points;
import organic.structure.segment.Segment;
import organic.structure.segment.SimpleSegment;
import util.geometry.Rectangle;
import util.space.tree.QuadTree;
import util.vector.ReadVector;
import util.vector.Vector;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SpaceFillTree<T extends Component> {
    private static final int QUAD_TREE_CAPACITY = 4;

    private final double minDistance, maxDistance;
    private final double dynamics;
    private final double stepSize;
    private final double randomDeviation;

    public SpaceFillTree(double minDistance, double maxDistance, double dynamics, double stepSize, double randomDeviation) {
        this.minDistance = minDistance;
        this.maxDistance = maxDistance;
        this.dynamics = dynamics;
        this.stepSize = stepSize;
        this.randomDeviation = randomDeviation;
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
    private List<? extends ReadVector> leaves;
    private List<ReadVector> leavesToDelete;
    private Rectangle bounds;

    public Segment<T> generate(ReadVector origin, double startAngle, List<? extends ReadVector> leaves, int iterations) {
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

        for(ReadVector leaf : leaves) {
            SegmentData segmentData = closestSegment(leaf);

            if(segmentData == null) continue;
            foundOne = true;

            double distSq = Vector.distSq(leaf, segmentData.segment.getPosition());
            if(distSq < minDistance*minDistance) {
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

    private SegmentData closestSegment(ReadVector leaf) {
        SegmentData closest = null;
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
}
