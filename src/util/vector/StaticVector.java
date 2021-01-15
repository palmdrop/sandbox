package util.vector;

public class StaticVector implements ReadVector {
    private final Vector vector;

    public StaticVector(final ReadVector vector) {
        this.vector = new Vector(vector);
    }

    public StaticVector(double x, double y) {
        this.vector = new Vector(x, y);
    }

    @Override
    public double get(int index) {
        return vector.get(index);
    }
    @Override
    public double getX() { return get(0); }
    @Override
    public double getY() { return get(1); }
    @Override
    public double getZ() { return get(2); }

    @Override
    public int getDimension() { return vector.getDimension(); }

    @Override
    public double length() {
        return Math.sqrt(lengthSq());
    }

    @Override
    public double lengthSq() {
        return Vector.dot(this, this);
    }

    @Override
    public double angle() { return vector.angle(); }
}
