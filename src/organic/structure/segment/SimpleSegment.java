package organic.structure.segment;

import organic.Component;
import util.vector.ReadVector;
import util.vector.Vector;

import java.util.ArrayList;
import java.util.List;

public class SimpleSegment<T extends Component> implements Segment<T> {
    private Vector position;
    private Vector direction;
    private final List<Segment<T>> children;
    private final List<T> components;

    public SimpleSegment(ReadVector position, ReadVector direction) {
        this.position = new Vector(position);
        this.direction = new Vector(direction);
        this.children = new ArrayList<>();
        this.components = new ArrayList<>();
    }

    public SimpleSegment(Segment<?> segment) {
        this(segment.getPosition(), segment.getDirection());
    }

    @Override
    public ReadVector getDirection() {
        return direction;
    }

    @Override
    public void setDirection(ReadVector direction) {
        this.direction.set(direction);
    }

    @Override
    public ReadVector getPosition() {
        return position;
    }

    @Override
    public void setPosition(ReadVector position) {
        this.position.set(position);
    }

    @Override
    public List<Segment<T>> children() {
        return children;
    }

    @Override
    public List<T> getComponents() {
        return components;
    }
}
