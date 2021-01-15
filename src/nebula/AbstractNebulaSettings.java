package nebula;

import render.Drawer;
import sampling.heightMap.HeightMap;

public class AbstractNebulaSettings implements NebulaSettings {
    protected HeightMap baseMap = null;
    protected Drawer drawer = null;

    public AbstractNebulaSettings() {
    }

    @Override
    public HeightMap getBaseMap() {
        return baseMap;
    }

    @Override
    public Drawer getDrawer() {
        return drawer;
    }
}
