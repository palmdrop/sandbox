package organic.generation.growth;

import organic.Component;
import organic.generation.points.PointGenerator;
import organic.generation.points.area.HeightMapPointGenerator;
import organic.generation.segments.SpaceFillTree;
import organic.structure.segment.Segment;
import util.ArrayAndListTools;
import util.geometry.Rectangle;
import util.vector.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class LayeredGrowth {
    // Final variables
    private final PointGenerator leafGenerator;
    private final int numberOfLeaves;

    private final int numberOfLayers;
    private final int maxIterations;
    private final Function<Integer, SpaceFillTree<Component>> treeGenerator;
    private final List<Segment<Component>> roots;

    // Working variables
    private int currentLayer;

    public LayeredGrowth(PointGenerator leafGenerator, int numberOfLeaves, int numberOfLayers, int maxIterations, Function<Integer, SpaceFillTree<Component>> treeGenerator) {
        this.leafGenerator = leafGenerator;
        this.numberOfLeaves = numberOfLeaves;
        this.numberOfLayers = numberOfLayers;
        this.maxIterations = maxIterations;
        this.treeGenerator = treeGenerator;

        roots = new ArrayList<>(numberOfLayers);
        currentLayer = 0;

        for(int i = 0; i < numberOfLayers; i++) {
            roots.add(generateLayer());
        }
    }

    private Segment<Component> generateLayer() {
        List<Vector> leaves = leafGenerator.generate(numberOfLeaves);

        SpaceFillTree<Component> tree = treeGenerator.apply(currentLayer);
        Segment<Component> layer = tree.generate(ArrayAndListTools.randomElement(leaves), Math.random() * Math.PI * 2, leaves, maxIterations);

        currentLayer++;

        return layer;
    }


    public int getNumberOfLayers() {
        return numberOfLayers;
    }

    public int getCurrentLayer() {
        return currentLayer;
    }

    public Segment<Component> getRoot(int layer) {
        return roots.get(layer);
    }
}
