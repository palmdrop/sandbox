package util;

import java.util.*;

public class ListIndexSort<T> {
    private final List<T> data;
    private final Map<String, List<Integer>> sortingMap;

    public ListIndexSort(List<T> data) {
        this.data = data;
        sortingMap = new HashMap<>();
    }

    private List<Integer> getIndices() {
        List<Integer> indices = new ArrayList<>(data.size());
        for(int i = 0; i < data.size(); i++) {
            indices.add(i);
        }
        return indices;
    }

    public List<Integer> addSort(String name, Comparator<? super T> comparator) {
        List<Integer> indices = getIndices();
        ArrayAndListTools.sortUsing(indices, data, comparator);
        sortingMap.put(name, indices);
        return indices;
    }

    public List<Integer> getSort(String name) {
        return sortingMap.get(name);
    }

    public List<T> getData() {
        return data;
    }
}
