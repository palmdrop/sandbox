package color.space.drawer;

import color.colors.Color;
import color.colors.Colors;
import color.space.ColorSpace;
import render.AbstractPixelDrawer;
import sampling.heightMap.HeightMap;
import util.geometry.Rectangle;
import util.math.MathUtils;
import util.vector.Vector;

import java.util.List;

public class MapColorSpaceDrawer extends AbstractPixelDrawer implements ColorSpaceDrawer {
    private final ColorSpace colorSpace;
    private List<HeightMap> componentMaps;

    private double[] componentMin;
    private double[] componentMax;

    private boolean hasAlpha = false;
    private HeightMap alpha = null;

    public MapColorSpaceDrawer(ColorSpace colorSpace, Rectangle bounds, HeightMap... componentMaps) {
        this(colorSpace, bounds, 1, 1, componentMaps);
    }

    public MapColorSpaceDrawer(ColorSpace colorSpace, Rectangle bounds, double xMultiplier, double yMultiplier, HeightMap... componentMaps) {
        super(bounds, new Vector(), xMultiplier, yMultiplier, 0.0);
        if(colorSpace.getNumberOfComponents() != componentMaps.length) throw new IllegalArgumentException();
        this.colorSpace = colorSpace;
        this.componentMaps = List.of(componentMaps);

        componentMin = new double[colorSpace.getNumberOfComponents()];
        componentMax = new double[colorSpace.getNumberOfComponents()];
        for(int i = 0; i < colorSpace.getNumberOfComponents(); i++) {
            componentMin[i] = 0.0;
            componentMax[i] = 1.0;
        }
    }

    public static MapColorSpaceDrawer betweenColors(Color c1, Color c2, ColorSpace colorSpace, Rectangle bounds, HeightMap... componentMaps) {
        MapColorSpaceDrawer drawer = new MapColorSpaceDrawer(colorSpace, bounds, componentMaps);
        double[] components1 = colorSpace.getComponents(c1);
        double[] components2 = colorSpace.getComponents(c2);
        for(int i = 0; i < colorSpace.getNumberOfComponents(); i++) {
            drawer.setComponentRange(i, components1[i], components2[i]);
        }
        return drawer;
    }

    @Override
    public void setComponentRange(int component, double min, double max) {
        if(component < 0 || component >= colorSpace.getNumberOfComponents()) throw new IllegalArgumentException();
        componentMin[component] = min;
        componentMax[component] = max;
    }

    @Override
    protected int getColor(Vector p) {
        double[] components = new double[colorSpace.getNumberOfComponents()];
        for(int i = 0; i < components.length; i++) {
            components[i] = MathUtils.map(componentMaps.get(i).get(p), 0, 1, componentMin[i], componentMax[i]);
        }
        int color = colorSpace.getColor(components).toRGB();
        if(hasAlpha) {
            return Colors.withAlpha(color, alpha.get(p));
        } else {
            return color;
        }

    }

    public MapColorSpaceDrawer addAlpha(HeightMap alpha) {
        hasAlpha = true;
        this.alpha = alpha;
        return this;
    }
}
