package texture.texture.simple;

import sampling.heightMap.HeightMap;
import sampling.heightMap.modified.ModdedHeightMap;
import sampling.heightMap.modified.WarpedHeightMap;
import texture.Texture;
import texture.texture.AbstractTexture;

public class SimpleTexture extends AbstractTexture implements Texture, HeightMap {
    public SimpleTexture(HeightMap map) {
        super(map);
    }

    public HeightMap getHeightMap() {
        return getComponent(0);
    }

    public Double get(double x, double y) {
        return getHeightMap().get(x, y);
    }

    @Override
    public ModdedHeightMap toModded() {
        return getHeightMap().toModded();
    }

    @Override
    public WarpedHeightMap toDistorted() {
        return getHeightMap().toDistorted();
    }

    public SimpleTexture remap(double min, double max, double newMin, double newMax) {
        return new SimpleTexture(getHeightMap().toModded().addRemap(min, max, newMin, newMax));
    }
}
