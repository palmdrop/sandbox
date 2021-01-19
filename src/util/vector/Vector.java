package util.vector;

import util.math.MathUtils;

import java.security.InvalidParameterException;

public class Vector implements ReadVector, WriteVector {
    private Double x, y, z;
    private Double[] coords;

    public Vector() { init(0, 0, 0, 3); }

    public Vector(double x) {
        init(x, 0, 0, 3);
    }

    public Vector(double x, double y) {
        init(x, y, 0, 3);
    }

    public Vector(double x, double y, double z) {
        init(x, y, z, 3);
    }

    public Vector(int dimension) {
        init(0, 0, 0, Math.max(3, dimension));
        forCoords(getDimension(), i -> coords[i] = 0D);
    }

    public Vector(final ReadVector v) {
        init(v.getX(), v.getY(), v.getZ(), v.getDimension());
        for(int i = 3; i < getDimension(); i++) {
            set(i, v.get(i));
        }
    }

    public Vector(double[] iCoords) {
        init(0, 0, 0, Math.max(3, iCoords.length));
        forCoords(getDimension(), i -> set(i, iCoords[i]));
    }

    private void init(double x, double y, double z, int dimension) {
        this.x = x;
        this.y = y;
        this.z = z;

        coords = new Double[dimension];
        coords[0] = this.x;
        coords[1] = this.y;
        coords[2] = this.z;
    }

    public static Vector fromAngle(double theta) {
        double x = Math.cos(theta);
        double y = Math.sin(theta);
        return new Vector(x, y);
    }

    //Getters and setters
    public double set(int index, double val) {
        rangeCheck(index);

        double previous = get(index);

        coords[index] = val;
        switch(index) {
            case 1: x = val; break;
            case 2: y = val; break;
            case 3: z = val; break;
        }

        return previous;
    }

    public double setX(double val) {
        return set(0, val);
    }
    public double setY(double val) {
        return set(1, val);
    }
    public double setZ(double val) {
        return set(2, val);
    }

    public void set(final ReadVector v) {
        int vDim = v.getDimension();
        int thisDim = getDimension();

        if(vDim > thisDim) {
            init(0,0,0, vDim);
        }

        forCoords(vDim, i -> set(i, v.get(i)));
    }

    @Override
    public double get(int index) {
        rangeCheck(index);

        return coords[index];
    }
    @Override
    public double getX() { return get(0); }
    @Override
    public double getY() { return get(1); }
    @Override
    public double getZ() { return get(2); }
    @Override
    public int getDimension() { return coords.length; }

    //Arithmetic
    public Vector add(final ReadVector v) {
        return vectorArithmetic(v, i -> set(i, get(i) + v.get(i)));
    }
    public Vector sub(final ReadVector v) {
        return vectorArithmetic(v, i -> set(i, get(i) - v.get(i)));
    }
    public Vector mult(double s) {
        forCoords(getDimension(), i -> set(i, get(i) * s));
        return this;
    }
    public Vector multX(double s) { return multCoord(s, 0); }
    public Vector multY(double s) { return multCoord(s, 1); }
    public Vector multZ(double s) { return multCoord(s, 2); }
    public Vector multCoord(double s, int i) {
        set(i, get(i) * s);
        return this;
    }
    public Vector div(double d) {
        return mult(1.0 / d);
    }

    public static Vector add(final ReadVector v1, final ReadVector v2) {
        return new Vector(v1).add(v2);
    }
    public static Vector sub(final ReadVector v1, final ReadVector v2) {
        return new Vector(v1).sub(v2);
    }
    public static Vector mult(final ReadVector v, double s) {
        return new Vector(v).mult(s);
    }
    public static Vector div(final ReadVector v, double d) {
        return new Vector(v).div(d);
    }

    //Vector operations
    public double dot(final ReadVector v) {
        double d = 0;
        for(int i = 0; i < Math.min(v.getDimension(), getDimension()); i++) {
            d += get(i) * v.get(i);
        }
        return d;
    }

    public Vector cross(final ReadVector v) {
        if(v.getDimension() != 3 || getDimension() != 3) throw new InvalidParameterException();
        double x = this.y * v.getZ() - this.z * v.getY();
        double y = this.z * v.getX() - this.x * v.getZ();
        double z = this.x * v.getY() - this.y * v.getX();
        return new Vector(x, y, z);
    }

    public double angleBetween(final ReadVector v) {
        double l1 =    length();
        double l2 =  v.length();

        double dot = dot(v);

        double cosAng = dot / (l1 * l2);
        return Math.acos(cosAng);
    }

    public static double dot(final ReadVector v1, final ReadVector v2) {
        return new Vector(v1).dot(v2);
    }
    public static Vector cross(final ReadVector v1, final ReadVector v2) {
        return new Vector(v1).cross(v2);
    }
    public static double angleBetween(final ReadVector v1, final ReadVector v2) {
        return new Vector(v1).angleBetween(v2);
    }

    public double length() {
        return Math.sqrt(lengthSq());
    }

    public double lengthSq() {
        return dot(this);
    }

    public Vector normalize() {
        return div(length());
    }
    public Vector setLength(double newLength) {
        if(length() == 0) return this;
        return mult(newLength / length());
    }
    public Vector limit(double minLength, double maxLength) {
        double length = length();
        if(length < minLength) setLength(minLength);
        else if(length >= maxLength) setLength(maxLength);

        return this;
    }
    public Vector limit(double maxLength) {
        return limit(0, maxLength);
    }

    public double dist(final ReadVector v) {
        return new Vector(v).sub(this).length();
    }

    public double distSq(final ReadVector v) {
        return new Vector(v).sub(this).lengthSq();
    }

    public double angle() {
        double a = Math.atan2(getY(),  getX());
        return (2 * Math.PI + a) % (2 * Math.PI);
    }


    public boolean isZero() {
        for(int i = 0; i < getDimension(); i++) {
            double v = coords[i];
            if(v != 0.0) return false;
        }
        return true;
    }

    public static Vector normalize(final ReadVector v) {
        return new Vector(v).normalize();
    }
    public static Vector setLength(final ReadVector v, double newLength) {
        return new Vector(v).setLength(newLength);
    }
    public static Vector limit(final ReadVector v, double minLength, double maxLength) { return new Vector(v).limit(minLength, maxLength); }
    public static Vector limit(final ReadVector v, double maxLength) { return new Vector(v).limit(maxLength); } //TODO: avoid copy???
    public static double dist(final ReadVector v1, final ReadVector v2) { return new Vector(v1).dist(v2); }     //TODO: move some functionality to abstractClass???
    public static double distSq(final ReadVector v1, final ReadVector v2) { return new Vector(v1).distSq(v2); }
    public static boolean isZero(final ReadVector v1) { return new Vector(v1).isZero(); }


    //Random
    public static Vector randomWithLength(double length) { //Will always make a 2D vector
        double angle = Math.random() * 2 * Math.PI;
        double x = Math.cos(angle) * length;
        double y = Math.sin(angle) * length;
        return new Vector(x, y);
    }

    public static Vector random(double xMax, double yMax) {
        return random(xMax, yMax, 0);
    }

    public static Vector random(double xMax, double yMax, double zMax) {
        return random(0, xMax, 0, yMax, 0, zMax);
    }

    public static Vector random(double xMin, double xMax, double yMin, double yMax) {
        return random(xMin, xMax, yMin, yMax, 0, 0);
    }

    public static Vector random(double xMin, double xMax, double yMin, double yMax, double zMin, double zMax) {
        double x = MathUtils.unitMap(Math.random(), xMin, xMax);
        double y = MathUtils.unitMap(Math.random(), yMin, yMax);
        double z = MathUtils.unitMap(Math.random(), zMin, zMax);
        return new Vector(x, y, z);
    }

    public static Vector random(int dimension, double min, double max) {
        Vector vector = new Vector(dimension);
        vector.forCoords(dimension, i -> {
            vector.set(i, MathUtils.unitMap(Math.random(), min, max));
        });
        return vector;
    }

    public static Vector random(double[] ranges) {
        int dimension = ranges.length / 2;
        Vector vector = new Vector(dimension);
        vector.forCoords(dimension, i -> {
            vector.set(i, MathUtils.unitMap(Math.random(), ranges[i*2], ranges[i*2 + 1]));
        });
        return vector;
    }

    //Interpolate
    public static Vector interpolate(ReadVector a, ReadVector b, double n) {
        return Vector.sub(b, a).mult(n).add(a);
    }

    public static Vector of(double value, int dimension) {
        Vector v = new Vector(dimension);
        for(int i = 0; i < dimension; i++) {
            v.set(i, value);
        }
        return v;
    }

    //Util
    public Vector copy() {
        return new Vector(this);
    }

    public double[] asArray() {
        double[] array = new double[getDimension()];
        for(int i = 0; i < coords.length; i++) {
            array[i] = get(i);
        }
        return array;
    }

    private interface CoordOperation {
        void call(int index);
    }
    private void forCoords(int to, final CoordOperation co) {
        for(int i = 0; i < to; i++) {
            co.call(i);
        }
    }
    private Vector vectorArithmetic(final ReadVector v, final CoordOperation co) {
        int d = Math.min(v.getDimension(), getDimension());
        forCoords(d, co);
        return this;
    }

    private void rangeCheck(int index) {
        if(index < 0 || index >= coords.length) throw new IndexOutOfBoundsException();
    }

    @Override
    public String toString() {
        final StringBuilder s = new StringBuilder("(");
        for(int i = 0; i < getDimension(); i++) {
            s.append(get(i));
            if(i != getDimension() - 1) s.append(", ");
        }
        s.append(")");
        return s.toString();
    }
}
