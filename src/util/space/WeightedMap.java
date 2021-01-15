package util.space;

import processing.core.PGraphics;
import util.geometry.Rectangle;
import sampling.heightMap.HeightMap;
import util.math.WeightedRandomList;
import util.vector.Vector;

public class WeightedMap {
    public interface ColorToWeight {
        double calculate(int color);
    }

    private final double[] map;
    private final int width, height;
    private final int length;

    private double sum;

    private double[] searchMap;
    private boolean useSearch = false;

    public WeightedMap(int width, int height) {
        if(width <= 0 || height <= 0)
            throw new IllegalArgumentException("invalid argument(s)");

        this.width = width;
        this.height = height;
        this.length = width * height;

        this.map = new double[length];
        this.searchMap = new double[length];

        this.sum = 0;

    }

    public static WeightedRandomMap mapFromNoise(int width, int height, HeightMap heightMap) {
        WeightedRandomMap wm = new WeightedRandomMap(width, height);
        for(int x = 0; x < width; x++) for(int y = 0; y < height; y++) {
            double v = heightMap.get(x, y);
            wm.set(x, y, v);
        }
        wm.normalize();
        wm.calculateSearchMap();
        return wm;
    }

    public static WeightedRandomMap mapFromGraphics(PGraphics g) {
        return mapFromGraphics(g, new Rectangle(0, 0, g.width, g.height));
    }

    public static WeightedRandomMap mapFromGraphics(PGraphics g, Rectangle region) {
        return mapFromGraphics(g, region, c -> (float)(c >> 16 & 255));
    }
    public static WeightedRandomMap mapFromGraphics(PGraphics g, Rectangle region, ColorToWeight converter) {
        g.loadPixels();

        int minX = (int) Math.max(0, region.x);
        int maxX = (int) Math.min(g.width, region.x + region.width);

        int minY = (int) Math.max(0, region.y);
        int maxY = (int) Math.min(g.height, region.y + region.height);

        WeightedRandomMap wm = new WeightedRandomMap(maxX - minX, maxY - minY);
        for(int x = minX; x < maxX; x++) for(int y = minY; y < maxY; y++) {
            double v = converter.calculate(g.pixels[x + y * g.width]);
            wm.set(x - minX, y - minY, v);
        }
        wm.normalize();
        wm.calculateSearchMap();
        return wm;
    }

    public double set(int x, int y, double v) {
        useSearch = false; // Now invalid
        double prev = map[index(x, y)];
        map[index(x, y)] = v;

        sum += v - prev;

        return prev;
    }


    public double get(int x, int y) {
        return map[index(x, y)];
    }

    public Vector getRandom() {
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
            return getPoint(position);
        } else {
            return getPoint(binarySearch(r));
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

    private int index(int x, int y) {
        return x + y * width;
    }

    private Vector getPoint(int index) {
        int x = index % width;
        int y = index / width;
        return new Vector(x, y);
    }

    public void normalize() {
        for(int i = 0; i < map.length; i++) {
            map[i] /= sum;
        }
        this.sum = 1.0;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}


