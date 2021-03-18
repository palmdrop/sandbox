package organic.generation.growth;

import organic.Component;
import organic.generation.points.PointGenerator;
import organic.generation.segments.DynamicSpaceFillTree;
import organic.generation.segments.SpaceFillTree;
import organic.structure.segment.Segment;
import util.ArrayAndListTools;
import util.vector.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.function.Supplier;

public class LayeredGrowth {
    private final PointGenerator leafGenerator;
    private final int numberOfLeaves;

    private final int numberOfLayers;
    private final int maxIterations;
    private final Supplier<DynamicSpaceFillTree<Component>> treeGenerator;
    private final List<Segment<Component>> roots;

    public LayeredGrowth(PointGenerator leafGenerator, int numberOfLeaves, int numberOfLayers, int maxIterations, Supplier<DynamicSpaceFillTree<Component>> treeGenerator) {
        this.leafGenerator = leafGenerator;
        this.numberOfLeaves = numberOfLeaves;
        this.numberOfLayers = numberOfLayers;
        this.maxIterations = maxIterations;
        this.treeGenerator = treeGenerator;

        roots = new ArrayList<>(numberOfLayers);

        long time = System.currentTimeMillis();
        ThreadPoolExecutor executor =
                (ThreadPoolExecutor) Executors.newFixedThreadPool(numberOfLayers);
        List<Future<Segment<Component>>> futures = new ArrayList<>(numberOfLayers);
        for(int i = 0; i < numberOfLayers; i++) {
            futures.add(executor.submit(this::generateLayer));
        }
        executor.shutdown();
        try {
            if (!executor.awaitTermination(60 * 10, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException ex) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }

        for(int i = 0; i < numberOfLayers; i++) {
            try {
                roots.add(futures.get(i).get());
            } catch (InterruptedException | ExecutionException e) {
            }
        }

        System.out.println(System.currentTimeMillis() - time);
    }

    private Segment<Component> generateLayer() {
        List<Vector> leaves = leafGenerator.generate(numberOfLeaves);

        DynamicSpaceFillTree<Component> tree = treeGenerator.get();

        Segment<Component> root = tree.generate(ArrayAndListTools.randomElement(leaves), Math.random() * Math.PI * 2, leaves, maxIterations);
        System.out.println("Layer generated");
        return root;
    }


    public int getNumberOfLayers() {
        return numberOfLayers;
    }

    public Segment<Component> getRoot(int layer) {
        return roots.get(layer);
    }
}
