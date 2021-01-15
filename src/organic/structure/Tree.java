package organic.structure;

import organic.structure.segment.Segment;

import java.util.List;

public interface Tree<T extends Tree<T>> {
    List<T> children();

    default int getDepth() {
        if(children().isEmpty()) return 1;
        return 1 + children().stream().mapToInt(Tree::getDepth).max().getAsInt();
    }
}
