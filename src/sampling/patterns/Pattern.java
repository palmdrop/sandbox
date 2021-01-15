package sampling.patterns;

import sampling.heightMap.HeightMap;

public interface Pattern extends HeightMap {
    Pattern setRecursion(int numberOfIterations, double amount);
}
