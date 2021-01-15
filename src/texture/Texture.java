package texture;

import sampling.Sampler;
import sampling.heightMap.HeightMap;
import util.vector.Vector;

public interface Texture extends Sampler<Double> {
    default double[] getComponents(Vector p) {
        return getComponents(p.getX(), p.getY());
    }

    default double[] getComponents(double x, double y) {
        double[] components = new double[getNumberOfComponents()];
        for(int i = 0; i < getNumberOfComponents(); i++) {
            components[i] = get(x, y, i);
        }
        return components;
    }

    default double get(Vector p, int component) {
        setActiveComponent(component);
        return get(p);
    }
    default double get(double x, double y, int component) {
        setActiveComponent(component);
        return get(x, y);
    }

    void setActiveComponent(int component);
    int getActiveComponent();
    int getNumberOfComponents();

    HeightMap getComponent(int index);
}
