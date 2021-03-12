package flow;

import sampling.Sampler;
import util.math.MathUtils;
import util.vector.ReadVector;
import util.vector.Vector;

import java.util.function.Function;

public class FlowField implements Sampler<ReadVector> {
    private double width, height;

    private final Vector[] field;
    private int fieldWidth, fieldHeight;

    public <T> FlowField(Sampler<T> source, Function<T, Double> angleConverter, Function<T, Double> lengthConverter, int width, int height, int detail) {
        this.width = width;
        this.height = height;

        fieldWidth = width * detail;
        fieldHeight = height * detail;
        field = new Vector[fieldWidth * fieldHeight];

        for(int ix = 0; ix < fieldWidth; ix++) for(int iy = 0; iy < fieldHeight; iy++) {
            double x = (double)ix / detail;
            double y = (double)iy / detail;
            T value = source.get(x, y);

            double angle = angleConverter.apply(value);
            double length = lengthConverter.apply(value);

            Vector v = Vector.fromAngle(angle).mult(length);
            field[ix + iy * fieldWidth] = v;
        }

    }

    @Override
    public ReadVector get(Vector point) {
        if(!isInside(point)) {
            throw new IllegalArgumentException("Point outside bounds");
        }

        int x = (int) MathUtils.map(point.getX(), 0, width, 0, fieldWidth);
        int y = (int) MathUtils.map(point.getY(), 0, height, 0, fieldHeight);

        return field[x + y * fieldWidth];
    }

    public boolean isInside(Vector point) {
        return point.getX() >= 0 && point.getX() < width && point.getY() >= 0 && point.getY() < height;
    }

    public double getWidth() {
        return width;
    }
    public double getHeight() {
        return height;
    }
}
