package util.math;

import java.util.ArrayList;
import java.util.List;

public class WeightedRandomList<T> {
    private final double[] map;
    private double[] searchMap;
    private boolean useSearch = false;

    private final int length;
    private double sum;

    private List<T> data;

    public WeightedRandomList(int length) {
        this.length = length;

        this.map = new double[length];
        this.searchMap = new double[length];

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
        double prev = map[index];
        map[index] = v;

        sum += v - prev;

        return prev;
    }


    public double getWeight(int index) {
        return map[index];
    }

    public int getRandomIndex() {
        double r = Math.random() * sum;

        if(!useSearch) {
            int position = 0;
            for (int i = 0; i < map.length; i++) {
                if (r - map[i] <= 0) {
                    position = i;
                    break;
                }
                r -= map[i];
            }
            return position;
                    //getPoint(position);
        } else {
            return binarySearch(r);
            //getPoint(binarySearch(r));
        }
    }

    private int binarySearch(double r) {
        int min = 0;
        int max = length;

        while(min != max) {
            int middle = (max + min) / 2;
            double v = searchMap[middle];
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
            sum += map[i];
            searchMap[i] = sum;
        }
        useSearch = true; // Now valid
    }

    public void normalize() {
        for(int i = 0; i < map.length; i++) {
            map[i] /= sum;
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
