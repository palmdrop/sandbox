package organic.structure.segment;

import organic.Component;
import organic.ComponentHolder;
import organic.structure.Tree;
import util.vector.ReadVector;
import util.vector.Vector;

public interface Segment<T extends Component> extends Tree<Segment<T>>, ComponentHolder<T> {
    Vector getDirection();
    void setDirection(ReadVector direction);

    Vector getPosition();
    void setPosition(ReadVector position);
}
