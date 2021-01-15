package util.vector;

public interface ReadVector {
    double get(int index);

    double getX();
    double getY();
    double getZ();

    int getDimension();

    double length();
    double lengthSq();

    double angle();
}
