package organic.generation.points;

import util.vector.ReadVector;
import util.vector.Vector;

import java.util.ArrayList;
import java.util.List;

public interface PointGenerator {
    default List<Vector> generate(int numberOfPoints) {
        List<Vector> points = new ArrayList<>(numberOfPoints);
        for(int i = 0; i < numberOfPoints; i++) {
            points.add(get());
        }
        return points;
    }
    Vector get();
}
