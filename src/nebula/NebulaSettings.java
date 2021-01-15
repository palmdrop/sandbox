package nebula;

import render.Drawer;
import sampling.heightMap.HeightMap;

public interface NebulaSettings {
    HeightMap getBaseMap();
    Drawer getDrawer();
}
