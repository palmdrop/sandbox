package util.math;

import util.geometry.Rectangle;
import util.vector.Vector;

import java.util.ArrayList;
import java.util.List;

public class KMeansClustering {
    private final int K;
    private final int iterations;
    private final List<Vector> data;

    private List<Vector> centroids;
    private List<Integer> counts;

    public KMeansClustering(int k, int iterations, List<Vector> data) {
        if(k < 1 || iterations < 1) throw new IllegalArgumentException();

        K = k;
        this.iterations = iterations;
        this.data = data;

        this.centroids = new ArrayList<>(k);

        calculate();
    }

    private void calculate() {
        for(int i = 0; i < K; i++) {
            centroids.add(data.get((int) MathUtils.random(0, data.size())).copy());
        }

        for(int i = 0; i < iterations; i++) {
            cluster();
        }
    }

    private void cluster() {
        List<Vector> newCentroids = new ArrayList<>(K);
        List<Integer> counts = new ArrayList<>(K);
        for(int i = 0; i < K; i++) {
            newCentroids.add(new Vector());
            counts.add(0);
        }
        for(Vector v : data) {
            int index = 0;
            double closest = Vector.distSq(v, centroids.get(index));
            for(int i = 1; i < K; i++) {
                double distSq = Vector.distSq(v, centroids.get(i));
                if(distSq < closest) {
                    closest = distSq;
                    index = i;
                }
            }
            newCentroids.get(index).add(v);
            counts.set(index, counts.get(index) + 1);
        }

        for(int i = 0; i < K; i++) {
            newCentroids.get(i).div(counts.get(i));
        }

        centroids = newCentroids;
        this.counts = counts;
    }

    public List<Vector> getCentroids() {
        return centroids;
    }

    public List<Integer> getCounts() {
        return counts;
    }
}
