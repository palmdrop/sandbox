package nebula.designSettings;

import sampling.heightMap.HeightMap;
import sampling.heightMap.HeightMaps;
import sampling.heightMap.modified.FeedbackHeightMap;
import nebula.AbstractNebulaSettings;
import render.heightMap.CombinedHeightMapDrawer;
import util.geometry.Circle;
import util.geometry.Rectangle;
import util.math.MathUtils;
import util.noise.generator.GNoise;
import util.vector.Vector;

import java.util.stream.IntStream;

public class CombinedSettings extends AbstractNebulaSettings {
    private final int x;
    private final int y;
    private final int width;
    private final int height;

    private enum MapType {
        CONSTANT,
        SIMPLEX,
        PERLIN,
        SIN,
        COS,
        TAN,
        SAW,
        RANDOM,
        RANDOM_GAUSS,
        GRID,
        RECT,
        RECTS,
        CIRCLE,
        CIRCLES,
        FADE,
    }

    private int[] probabilities = {
            1, // Constant
            4, // Simplex
            2, // Perlin
            3, // Sin
            3, // Cos
            2, // Tan
            3, // Saw
            1, // Random
            1, // Random gauss
            2, // Grid
            1, // Rect
            2, // Rects
            1, // Circle
            3, // Circles
            2  // Fade
    };

    private MapType[] mapArray;

    private void init() {
        int length = IntStream.of(probabilities).sum();
        mapArray = new MapType[length];

        MapType[] mapTypes = MapType.values();
        int index = 0;
        for(int i = 0; i < mapTypes.length; i++) {
           for(int j = 0; j < probabilities[i]; j++) {
               mapArray[index] = mapTypes[i];
               index++;
           }
        }
    }

    public CombinedSettings(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        init();

        HeightMap alphaMap = HeightMaps.constant(1.0);
        HeightMap aMap, bMap, cMap;

        aMap = baseMapGenerator(0.0, 0.01, Math.PI * 2 * Math.random(), 1.0);
        bMap = baseMapGenerator(0.0, 0.01, Math.PI * 2 * Math.random(), 1.0);
        cMap = baseMapGenerator(0.0, 0.01, Math.PI * 2 * Math.random(), 1.0);

        aMap.toDistorted()
            .domainWarp(bMap, cMap, 10, 100)
            .toFeedbackHeightMap(3, FeedbackHeightMap.Mode.AVERAGE);
        bMap.toDistorted()
            .domainWarp(aMap, GNoise.simplexNoise(0.01, 1.0, 1.0), 10, 100)
            .toFeedbackHeightMap(3, FeedbackHeightMap.Mode.MULT);
        cMap.toDistorted()
            .domainWarp(aMap, bMap, 0, 40)
            .toFeedbackHeightMap(2, FeedbackHeightMap.Mode.MULT);

        drawer
                = new CombinedHeightMapDrawer(
                        aMap,
                        bMap,
                        cMap,
                        alphaMap,
                        CombinedHeightMapDrawer.Mode.HSB,
                        0.0,
                        0.0
                );

        baseMap = HeightMaps.mult(aMap, HeightMaps.mult(bMap, cMap, 0.5), 0.5);
    }


    private HeightMap getMap(double frequency, double dir, double pow) {
        MapType type = mapArray[(int) (Math.random() * mapArray.length)];

        double xMult = Math.cos(dir) * frequency;
        double yMult = Math.sin(dir) * frequency;

        double size = Math.min(width, height) * Math.random();
        double proportion = MathUtils.limit(MathUtils.randomGaussian(0.5), 0, 1);

        double w = size * proportion;
        double h = size * (1 - proportion);


        switch (type) {
            case CONSTANT:
                return HeightMaps.constant(1.0); //TODO: vary how???
            case SIMPLEX:
                return GNoise.simplexNoise(frequency, 1.0, pow);
            case PERLIN:
                return GNoise.perlinNoise(frequency, 1.0, pow);
            case SIN:
                return HeightMaps.sin(xMult, yMult, Math.random(), pow);
            case COS:
                return HeightMaps.cos(xMult, yMult, Math.random(), pow);
            case TAN:
                return HeightMaps.tan(xMult, yMult, Math.random(), pow);
            case SAW:
                return HeightMaps.saw(xMult, yMult, Math.random(), pow);
            case RANDOM:
                return HeightMaps.random(0, 1);
            case RANDOM_GAUSS:
                return HeightMaps.randomGaussian(0.5, 1.0);
            case GRID:
                return HeightMaps.grid(xMult, yMult, dir, Math.random() * dir, pow, Vector.randomWithLength(Math.random()));
            case RECT:
                return HeightMaps.rect(new Rectangle((float)MathUtils.random(x - w/2, x + width - w/2),
                        (float)MathUtils.random(y - h/2, y + height - h/2),
                        (float)w,
                        (float)h));
            case RECTS:
                return HeightMaps.rects(w * frequency,
                        h * frequency,
                        w * frequency * Math.random(), h * frequency * Math.random(),
                        Vector.randomWithLength(size * Math.random()));
            case CIRCLE:
                return HeightMaps.circle(new Circle(MathUtils.random(x, x + width),
                        MathUtils.random(y ,y + height),
                        size));
            case CIRCLES:
                return HeightMaps.circles(w, h,
                        w * frequency * Math.random(),
                        h * frequency * Math.random(),
                        Vector.randomWithLength(size * Math.random()),
                        pow);
            case FADE:
                return HeightMaps.fade(xMult, yMult, Math.random(), 0, 1, Math.random() * 2 * width, pow);

        };
        return HeightMaps.constant(1.0);
    }

    private HeightMap baseMapGenerator(double variation, double frequency, double dir, double dirVar) {
        int nr = (int) MathUtils.random(1, 3);

        HeightMap hm = null;
        for(int i = 0; i < nr; i++) {
            HeightMap next = getMap(frequency, dir + MathUtils.random(-dirVar, dirVar), 1.0);
            if(hm == null) {
                hm = next;
            } else {
                hm = HeightMaps.mult(hm, next, Math.random());
            }
        }
        return hm;
    }
}
