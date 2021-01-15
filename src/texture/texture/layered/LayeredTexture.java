package texture.texture.layered;

import sampling.heightMap.HeightMap;
import texture.texture.AbstractTexture;
import texture.texture.simple.SimpleTexture;

import java.util.function.Supplier;

public class LayeredTexture extends AbstractTexture {
    private static HeightMap[] getHeightMaps(Supplier<? extends HeightMap> heightMapSupplier, int layers) {
        HeightMap[] heightMaps = new HeightMap[layers];
        for(int i = 0; i < layers; i++) {
            heightMaps[i] = heightMapSupplier.get();
        }
        return heightMaps;
    }

    public LayeredTexture(HeightMap... heightMaps) {
        super(heightMaps);
    }

    public LayeredTexture(Supplier<? extends HeightMap> heightMapSupplier, int layers) {
        super(getHeightMaps(heightMapSupplier, layers));
    }

    public LayeredTexture(HeightMap heightMap, int layers) {
        this(() -> heightMap, layers);
    }
}
