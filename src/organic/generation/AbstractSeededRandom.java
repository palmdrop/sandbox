package organic.generation;

import java.util.Random;

public abstract class AbstractSeededRandom implements SeededRandom {
    private long seed;
    protected final Random random;

    public AbstractSeededRandom() {
        this(System.nanoTime());
    }

    public AbstractSeededRandom(long seed) {
        this.seed = seed;
        random = new Random(seed);
    }


    @Override
    public long getSeed() {
        return seed;
    }

    @Override
    public void setSeed(long seed) {
        this.seed = seed;
        random.setSeed(seed);
    }
}
