package texture.texture;

import sampling.Sampler;
import sampling.heightMap.HeightMap;
import texture.Texture;
import util.vector.Vector;

import java.util.List;

public class AbstractTexture implements Texture {
    private int activeComponent;
    private final List<HeightMap> components;

    public AbstractTexture(HeightMap... components) {
        if(components == null || components.length == 0) throw new IllegalArgumentException();
        this.components =
                List.of(components);
        activeComponent = 0;
    }

    @Override
    public void setActiveComponent(int component) {
        if(component < 0 || component >= components.size()) throw new IllegalArgumentException();
        this.activeComponent = component;
    }

    @Override
    public int getActiveComponent() {
        return activeComponent;
    }

    @Override
    public int getNumberOfComponents() {
        return components.size();
    }

    @Override
    public Double get(Vector point) {
        return components.get(activeComponent).get(point);
    }

    @Override
    public HeightMap getComponent(int index) {
        return components.get(index);
    }
}
