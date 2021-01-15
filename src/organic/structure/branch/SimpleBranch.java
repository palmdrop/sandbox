package organic.structure.branch;

import organic.Component;

import java.util.ArrayList;
import java.util.List;

public class SimpleBranch<T extends Component> implements Branch<T> {
    private final List<T> components;
    private final List<Branch<T>> children;
    private double angle;
    private double length;

    public SimpleBranch(double angle, double length) {
        this.angle = angle;
        this.length = length;
        components = new ArrayList<>();
        children = new ArrayList<>();
    }

    @Override
    public double getAngle() {
        return angle;
    }

    @Override
    public void setAngle(double angle) {
        this.angle = angle;
    }

    @Override
    public double getLength() {
        return length;
    }

    @Override
    public void setLength(double length) {
        this.length = length;
    }

    @Override
    public List<T> getComponents() {
        return components;
    }

    @Override
    public List<Branch<T>> children() {
        return children;
    }
}
