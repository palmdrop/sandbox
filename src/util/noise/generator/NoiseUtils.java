package util.noise.generator;

import processing.core.PGraphics;
import util.math.MathUtils;
import util.noise.Noise;
import util.noise.type.ImprovedPerlinNoise;
import util.noise.type.OpenSimplexNoise;

public class NoiseUtils {
    private static final Noise simplexNoise = new OpenSimplexNoise(System.currentTimeMillis());
    private static final Noise perlinNoise = new ImprovedPerlinNoise(System.currentTimeMillis());

    public static double simplexNoise(double x, double y, double freq) {
        return simplexNoise.get(x * freq, y * freq);
    }

    public static double simplexNoise(double x, double y, double z, double freq) {
        return simplexNoise.get(x * freq, y * freq, z * freq);
    }

    public static double perlinNoise(double x, double y, double freq) {
        return NoiseUtils.perlinNoise(x, y, 0, freq);
    }

    public static double perlinNoise(double x, double y, double z, double freq) {
        return perlinNoise.get(x * freq, y * freq, z * freq);
    }

    public static PGraphics render(PGraphics g, double[][] map, double min, double max) {
        g.beginDraw();
        g.loadPixels();

        for(int x = 0; x < map.length; x++) {
            for(int y = 0; y < map[x].length; y++) {
                double v = MathUtils.map(map[x][y], min, max, 0, 1);
                int color = g.color((int)(v * 255));
                g.pixels[x + y * map.length] = color;
            }
        }

        g.updatePixels();
        g.endDraw();
        return g;
    }

    public static Noise fromHeightMap(sampling.heightMap.HeightMap hm) {
        return new Noise() {
            @Override
            public Double get(double x) {
                return hm.get(x, 0);
            }

            @Override
            public Double get(double x, double y) {
                return hm.get(x, y);
            }

            @Override
            public Double get(double x, double y, double z) {
                return hm.get(x, y);
            }
        };
    }
}
