package util.math;

import java.util.ArrayList;
import java.util.List;

public class WeightedRandomList<T> {
    private final double[] weights;
    private final double[] weightSums;
    private boolean useSearch = false;

    private final int length;
    private double sum;

    private final List<T> data;

    public WeightedRandomList(int length) {
        this.length = length;

        this.weights = new double[length];
        this.weightSums = new double[length];

        data = new ArrayList<>(length);
        for(int i = 0; i < length; i++) {
            data.add(null);
        }

        this.sum = 0;
    }

    public void set(int index, double weight, T element) {
        setWeight(index, weight);
        setElement(index, element);
    }

    public double setWeight(int index, double v) {
        useSearch = false; // Now invalid
        double prev = weights[index];
        weights[index] = v;

        sum += v - prev;

        return prev;
    }


    public double getWeight(int index) {
        return weights[index];
    }

    public int getRandomIndex() {
        double r = Math.random() * sum;

        if(!useSearch) {
            int position = 0;
            for (int i = 0; i < weights.length; i++) {
                r -= weights[i];
                if (r <= 0) {
                    position = i;
                    break;
                }
            }
            return position;
        } else {
            return binarySearch(r);
        }
    }

    private int binarySearch(double r) {
        int min = 0;
        int max = length;

        while(min != max) {
            int middle = (max + min) / 2;
            double v = weightSums[middle];
            if(v > r) {
                max = middle;
            } else if(v < r) {
                min = middle + 1;
            } else {
                return middle;
            }
        }

        return min;
    }

    public void calculateSearchMap() {
        double sum = 0.0;
        for(int i = 0; i < length; i++) {
            sum += weights[i];
            weightSums[i] = sum;
        }
        useSearch = true; // Now valid
    }

    public void normalize() {
        for(int i = 0; i < weights.length; i++) {
            weights[i] /= sum;
        }
        this.sum = 1.0;
    }

    public T getRandomElement() {
        return data.get(getRandomIndex());
    }

    public void setElement(int index, T element) {
        data.set(index, element);
    }
}
