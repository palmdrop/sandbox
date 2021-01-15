package organic.generation;

import java.util.Random;

public interface SeededRandom {
    long getSeed();
    void setSeed(long seed);
}
