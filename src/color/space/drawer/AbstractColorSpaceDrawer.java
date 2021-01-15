package color.space.drawer;

import color.space.ColorSpace;
import color.space.InvalidColorComponentException;
import render.AbstractPixelDrawer;
import util.geometry.Rectangle;
import util.math.MathUtils;
import util.vector.Vector;

public abstract class AbstractColorSpaceDrawer extends AbstractPixelDrawer implements ColorSpaceDrawer {
    protected final ColorSpace model;

    protected double[] componentMin, componentMax;
    protected double[] staticValues;

    protected boolean[] isActive;

    public AbstractColorSpaceDrawer(ColorSpace model, double defaultStatic, Rectangle bounds) {
        super(bounds, new Vector(), 1.0, 1.0, 0.0);
        this.model = model;

        componentMin = new double[model.getNumberOfComponents()];
        componentMax = new double[model.getNumberOfComponents()];
        staticValues = new double[model.getNumberOfComponents()];

        isActive = new boolean[model.getNumberOfComponents()];

        for(int i = 0; i < model.getNumberOfComponents(); i++) {
            componentMin[i] = 0.0;
            componentMax[i] = 1.0;
            staticValues[i] = defaultStatic;

            isActive[i] = false;
        }

        colorComponents = new double[model.getNumberOfComponents()];
    }

    protected abstract double getInterpolation(int component, double dx, double dy);

    private double[] colorComponents;

    protected int getColor(Vector p) {
        for(int i = 0; i < model.getNumberOfComponents(); i++) {
            double c;
            if(isActive[i]) {
                double v = getInterpolation(i, p.getX(), p.getY());
                c = MathUtils.map(v, 0, 1, componentMin[i], componentMax[i]);
            } else {
                c = staticValues[i];
            }
            colorComponents[i] = c;
        }
        return model.getRGB(colorComponents);
    }

    @Override
    public void setComponentRange(int component, double min, double max) {
        if(!validComponent(component)) throw new IllegalArgumentException();
        componentMin[component] = min;
        componentMax[component] = max;
    }

    protected boolean validComponent(int component) {
        return component >= 0 || component < model.getNumberOfComponents();
    }
}
