package util.space;

import processing.core.PGraphics;
import sampling.heightMap.HeightMap;
import util.geometry.Rectangle;
import util.math.WeightedRandomList;
import util.vector.Vector;

public class WeightedRandomMap {
    public interface ColorToWeight {
        double calculate(int color);
    }

    private final WeightedRandomList<Vector> list;
    private final int width, height;
    private final int length;

    public WeightedRandomMap(int width, int height) {
        if(width <= 0 || height <= 0)
            throw new IllegalArgumentException("invalid argument(s)");

        this.width = width;
        this.height = height;
        this.length = width * height;
        this.list = new WeightedRandomList<>(length);
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

    public void normalize() {
        list.normalize();
    }
    public void calculateSearchMap() {
        list.calculateSearchMap();
    }

    public double set(int x, int y, double v) {
        double prev = get(x, y);
        int i = index(x, y);
        list.set(i, v, getPoint(i));
        return prev;
    }

    public double get(int x, int y) {
        return list.getWeight(index(x, y));
    }

    public Vector getRandom() {
        return list.getRandomElement();
    }

    private int index(int x, int y) {
        return x + y * width;
    }

    private Vector getPoint(int index) {
        int x = index % width;
        int y = index / width;
        return new Vector(x, y);
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
