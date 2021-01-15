package organic.generation.points;

import processing.core.PApplet;
import sampling.heightMap.HeightMap;
import util.math.MathUtils;
import util.space.tree.QuadTree;
import util.vector.Vector;

import java.util.ArrayList;
import java.util.List;

public class PoissonDiskSampling implements PointGenerator {
    private PoissonDiskSampling() {
    }

    private static double getRadius(Vector pos, HeightMap rg, double minRadius, double maxRadius) {
        return MathUtils.map(rg.get(pos), 0, 1, (float)minRadius, (float)maxRadius);
    }

    public static List<Vector> generate(double width, double height, int tries, double radius) {
        return generate(width, height, tries, (x, y) -> 1.0, radius, radius);
    }


    public static List<Vector> generate(double width, double height, int tries, HeightMap rg, double minRadius, double maxRadius) {
        return generate(width, height, tries, rg, minRadius, maxRadius, false);
    }

    public static List<Vector> generate(double width, double height, int tries, HeightMap rg, double minRadius, double maxRadius, boolean minBias) {
        QuadTree<Vector> quadTree = new QuadTree<>(0, 0, width, height, 4);
        List<Vector> points = new ArrayList<>();
        List<Vector> spawnPoints = new ArrayList<>();

        spawnPoints.add(new Vector(width/2.0, height/2.0));

        double maxGeneratedradius = 0;

        while(!spawnPoints.isEmpty()) {
            int spawnIndex = (int) MathUtils.random(0, spawnPoints.size());
            Vector spawnPoint = spawnPoints.get(spawnIndex);
            double spawnRadius = getRadius(spawnPoint, rg, minRadius, maxRadius);

            boolean accepted = false;
            for(int i = 0; i < tries; i++) {
                double angle = MathUtils.random(0, PApplet.TWO_PI);
                Vector dir = Vector.fromAngle(angle);

                double minDist = minBias ? minRadius : spawnRadius;
                double dist = MathUtils.random(minDist, 2 * spawnRadius);
                Vector candidate = Vector.add(spawnPoint, Vector.mult(dir, dist));
                double candidateRadius = getRadius(candidate, rg, minRadius, maxRadius);

                if(isValid(candidate, quadTree, candidateRadius, maxGeneratedradius, rg, minRadius, maxRadius, minBias)) {
                    points.add(candidate);
                    spawnPoints.add(candidate);
                    quadTree.insert(candidate, candidate);

                    maxGeneratedradius = Math.max(maxGeneratedradius, candidateRadius);
                    accepted = true;
                    break;
                }
            }

            if(!accepted) {
                spawnPoints.remove(spawnIndex);
            }
        }
        return points;
    }

    private static boolean isValid(Vector point,
                                   QuadTree<Vector> quadTree,
                                   double radius,
                                   double queryRadius,
                                   HeightMap rg,
                                   double minRadius,
                                   double maxRadius,
                                   boolean minBias) {
        if(point.getX() < 0 || point.getX() >= quadTree.getWidth() ||
                point.getY() < 0 || point.getY() >= quadTree.getHeight())
            return false;

        for(Vector n : quadTree.query(point, queryRadius)) {
            double otherRadius = getRadius(n, rg, minRadius, maxRadius);
            double compareRadius = minBias ? Math.min(radius, otherRadius) : Math.max(radius, otherRadius);
            double distSq = point.distSq(n);
            if (distSq < (compareRadius * compareRadius)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public Vector get() {
        //TODO
        return null;
    }
}
