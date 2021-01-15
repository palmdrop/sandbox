package organic;

import java.util.List;

public interface ComponentHolder<T extends Component> {
    List<T> getComponents();
}
