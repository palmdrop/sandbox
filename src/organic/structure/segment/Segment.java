package organic.structure.segment;

import organic.Component;
import organic.ComponentHolder;
import organic.structure.Tree;
import util.vector.ReadVector;

import java.util.List;

public interface Segment<T extends Component> extends Tree<Segment<T>>, ComponentHolder<T> {
    ReadVector getDirection();
    void setDirection(ReadVector direction);

    ReadVector getPosition();
    void setPosition(ReadVector position);
}
