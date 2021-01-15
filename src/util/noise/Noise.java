package util.noise;


import sampling.heightMap.HeightMap;

public interface Noise extends sampling.heightMap.HeightMap {
    Double get(double x);
    Double get(double x, double y);
    Double get(double x, double y, double z);
}
