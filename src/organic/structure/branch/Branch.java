package organic.structure.branch;

import organic.Component;
import organic.ComponentHolder;
import organic.structure.Tree;

public interface Branch<T extends Component> extends Tree<Branch<T>>, ComponentHolder<T> {
    double getAngle();
    void setAngle(double angle);

    double getLength();
    void setLength(double length);
}
