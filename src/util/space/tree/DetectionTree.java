package util.space.tree;

import processing.core.PApplet;
import util.math.MathUtils;
import util.space.region.Grid;
import util.vector.ReadVector;
import util.vector.StaticVector;
import util.vector.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

public class DetectionTree {
    private final List<DetectionTree> children;

    private double value;

    private final double x, y;
    private final double width, height;

    private double precision;
    private final boolean isBaseLevel;
    private int inserts;

    public DetectionTree(double width, double height, double precision) {
        this(0, 0, width, height, precision);
    }

    public DetectionTree(double x, double y, double width, double height, double precision) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.precision = precision;
        this.inserts = 0;

        children = new ArrayList<>(4);
        isBaseLevel = width <= precision || height <= precision;
    }

    public void noiseInit(PApplet p, float frequency) {
       Grid grid = new Grid(width, height, precision);
       for(float y = 0; y < height; y += grid.getCellHeight()) for(float x = 0; x < width; x += grid.getCellWidth()) {
           float v = p.noise(x * frequency, y * frequency);
           insert(new StaticVector(x, y), v);
       }
    }

    public void insert(final ReadVector point, double value) {
        if (!isInsideBoundry(point)) return;

        if(isBaseLevel) {
            this.value += value;
            return;
        }

        this.value = (this.value * inserts + value) / (inserts + 1);
        inserts++;

        if(children.isEmpty()) {
            subdivide();
        }

        DetectionTree child = getChild(point);
        child.insert(point, value);
    }

    private DetectionTree getChild(ReadVector point) {
       int index = point.getX() < (x + width / 2) ? 0 : 1;
       index += point.getY() < (y + height / 2) ? 0 : 2;
       return children.get(index);
    }

    private void subdivide() {
        for(int j = 0; j <= 1; j++) for(int i = 0; i <= 1; i++) {
            double cx = x + i * (width / 2);
            double cy = y + j * (height /2);
            DetectionTree child = new DetectionTree(cx, cy, width /2, height /2, precision);
            children.add(child);
        }
    }

    private boolean isInsideBoundry(ReadVector point) {
        return MathUtils.inRange(point.getX(), x, x + width) && MathUtils.inRange(point.getY(), y, y + height);
    }

    public Vector randomPoint() {
       if(isBaseLevel) {
           double px = x + width / 2;
           double py = y + height / 2;
           return new Vector(px, py);
       }

       double sum = children.stream().mapToDouble(c -> c.value).sum();

       double v = Math.random() * sum;
       double acc = 0;
       for(DetectionTree child : children) {
           acc += child.value;
           if(v < acc) {
               return child.randomPoint();
           }
       }
       throw new IllegalStateException();
    }

    public double getValue(ReadVector point) {
        if(!isInsideBoundry(point)) throw new NoSuchElementException();
        if(isBaseLevel) return value;
        return getChild(point).getValue(point);
    }

}

