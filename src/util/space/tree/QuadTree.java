package util.space.tree;

import util.geometry.Rectangle;
import util.math.MathUtils;
import util.vector.ReadVector;
import util.vector.Vector;

import java.util.ArrayList;
import java.util.List;


public class QuadTree<T> {
    private final List<QuadTree<T>> children;

    private final List<T> data;
    private final List<ReadVector> points;

    private double x;
    private double y;
    private double width;
    private double height;
    private int size = 0;

    private int capacity;
    private double minWidth;

    public QuadTree(Rectangle bounds, int capacity) {
        this(bounds.x, bounds.y, bounds.width, bounds.height, capacity);
    }

    public QuadTree(double x, double y, double width, double height, int capacity) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.capacity = capacity;
        children = new ArrayList<>(4);
        data = new ArrayList<>(capacity);
        points = new ArrayList<>(capacity);
        minWidth = -1;
    }

    public void insertAll(List<ReadVector> points, List<T> values) {
        if(points.size() != values.size()) throw new IllegalArgumentException();
        for(int i = 0; i < points.size(); i++) {
            insert(points.get(i), values.get(i));
        }
    }

    public void insert(final ReadVector point, final T d) {
        if (!isInsideBoundry(point)) return;

        size++;

        if (data.size() < capacity || (minWidth != -1 && width / 2 <= minWidth)) {
            data.add(d);
            points.add(point);
        } else {
            if (children.size() < capacity) {
                subdivide();
            }
            for (final QuadTree<T> child : children) {
                child.insert(point, d);
            }
        }
    }

    private void subdivide() {
        for(int i = 0; i <= 1; i++) {
            for(int j = 0; j <= 1; j++) {
                final QuadTree<T> child =
                        new QuadTree<>(x + i * (width /2), y + j * (height /2), width /2, height /2, capacity);
                child.setMinWidth(minWidth);
                children.add(child);
            }
        }
    }

    private boolean isInsideBoundry(final ReadVector point) {
        return isInsideBoundry(point, x, y, width, height);
    }

    private static boolean isInsideBoundry(final ReadVector point, double x, double y, double w, double h) {
        return MathUtils.inRange(point.getX(), x, x + w) && MathUtils.inRange(point.getY(), y, y + h);
    }

    private boolean isInsideBoundry(double x, double y, double w, double h) { // For rects
        return !(x + w < this.x || this.x + this.width < x || y + h < this.y || this.y + this.height < y);
    }

    private boolean isInsideBoundry(final ReadVector center, double r) {
        final Vector pCenter = new Vector(center);
        final Vector pXY = new Vector(this.x, this.y);

        int dX = (int) (center.getX() - Math.max(pXY.getX(), Math.min(pCenter.getX(), pXY.getX() + this.width)));
        int dY = (int) (center.getY() - Math.max(pXY.getY(), Math.min(pCenter.getY(), pXY.getY() + this.height)));
        return (dX * dX + dY * dY) < (r * r);
    }

    private boolean isPointInCircle(final ReadVector p, final ReadVector center, double r) {
        return MathUtils.isPointInCircle(p, center, r);
    }

    private List<T> query(double qx, double qy, double qw, double qh) {
        final List<T> found = new ArrayList<>();
        if (!isInsideBoundry(qx, qy, qw, qh))
            return found;
        for (int i = 0; i < data.size(); i++) {
            if (isInsideBoundry(points.get(i), qx, qy, qw, qh)) {
                found.add(data.get(i));
            }
        }
        for (final QuadTree<T> child : children) {
            found.addAll(child.query(qx, qy, qw, qh));
        }
        return found;
    }

    public List<T> periodicQuery(final ReadVector center, double r) {
        // TODO: bad method? what if the view reaches in both x directions? change this code!!!
        final List<T> found = query(center, r);

        double xOffset = 0;
        boolean wrapX = false;
        if(center.getX() - r < 0) {
            xOffset = width;
            wrapX = true;
        } else if(center.getX() + r >= width) {
            xOffset = -width;
            wrapX = true;
        }

        double yOffset = 0;
        boolean wrapY = false;
        if(center.getY() - r < 0) {
            yOffset = height;
            wrapY = true;
        } else if(center.getY() + r >= height) {
            yOffset = -height;
            wrapY = true;
        }

        if(wrapX) {
            found.addAll(query(Vector.add(center, new Vector(xOffset, 0)), r));
        }
        if(wrapY) {
            found.addAll(query(Vector.add(center, new Vector(0, yOffset)), r));
        }
        if(wrapX && wrapY) {
            found.addAll(query(Vector.add(center, new Vector(xOffset, yOffset)), r));
        }

        return found;
    }

    public static class DataAndPoint<T> {
        public final T data;
        public final ReadVector point;

        public DataAndPoint(T data, ReadVector point) {
            this.data = data;
            this.point = point;
        }
    }

    public List<DataAndPoint<T>> pointQuery(ReadVector center, double r) {
        final List<DataAndPoint<T>> found = new ArrayList<>();
        if (!isInsideBoundry(center, r))
            return found;
        for (int i = 0; i < data.size(); i++) {
            ReadVector point = points.get(i);
            if (isPointInCircle(point, center, r)) {
                found.add(new DataAndPoint<T>(data.get(i), point));
            }
        }
        for (final QuadTree<T> child : children) {
            found.addAll(child.pointQuery(center, r));
        }
        return found;
    }

    public List<T> query(final ReadVector center, double r) {
        final List<T> found = new ArrayList<>();
        if (!isInsideBoundry(center, r))
            return found;
        for (int i = 0; i < data.size(); i++) {
            if (isPointInCircle(points.get(i), center, r)) {
                found.add(data.get(i));
            }
        }
        for (final QuadTree<T> child : children) {
            found.addAll(child.query(center, r));
        }
        return found;
    }

    public void clear() {
        for(final QuadTree<T> child : children) child.clear();

        children.clear();

        size = 0;
    }

    //Getters and setters
    public void resize(double width, double height) {
        this.width = width;
        this.height = height;
    }

    public void setMinWidth(double minSize) {
        this.minWidth = minSize;
    }

    public List<QuadTree<T>> getChildren() {
        return children;
    }

    public List<T> getData() {
        return data;
    }

    public List<ReadVector> getPoints() {
        return points;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getWidth() {
        return width;
    }

    public double getHeight() {
        return height;
    }

    public int getCapacity() {
        return capacity;
    }

    public double getMinWidth() {
        return minWidth;
    }

    public int getSize() {
        return size;
    }
}
