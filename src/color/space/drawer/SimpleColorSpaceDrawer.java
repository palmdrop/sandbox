package color.space.drawer;

import color.space.ColorSpace;
import color.space.InvalidColorComponentException;
import util.geometry.Rectangle;

public class SimpleColorSpaceDrawer extends AbstractColorSpaceDrawer implements ColorSpaceDrawer {
    public SimpleColorSpaceDrawer(ColorSpace model, double defaultStatic, Rectangle bounds) {
        this(model, 0, model.getNumberOfComponents() > 1 ? 1 : 0, defaultStatic, bounds);
    }


    public SimpleColorSpaceDrawer(ColorSpace model, int activeComponent1, int activeComponent2, double defaultStatic, Rectangle bounds) {
        super(model, defaultStatic, bounds);
        isActive[activeComponent1] = true;
        isActive[activeComponent2] = true;

    }

    private boolean isFirst = true;

    @Override
    protected double getInterpolation(int component, double dx, double dy) {
        double v;
        if(isFirst) {
            isFirst = false;
            v =  dx;
        } else {
            isFirst = true;
            v =  dy;
        }
        return v;
    }

    private void setActiveComponents(int activeComponent1, int activeComponent2) throws InvalidColorComponentException {
        if(!validComponent(activeComponent1) || !validComponent(activeComponent2)) throw new InvalidColorComponentException();
        for(int i = 0; i < isActive.length; i++) {
            isActive[i] = i == activeComponent1 || i == activeComponent2;
        }
    }

    public void setStaticComponentValue(int component, double value) throws InvalidColorComponentException {
        if(!validComponent(component)) throw new InvalidColorComponentException();
        staticValues[component] = value;
    }

}
