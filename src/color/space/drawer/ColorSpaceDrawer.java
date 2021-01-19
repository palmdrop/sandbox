package color.space.drawer;

import render.Drawer;

public interface ColorSpaceDrawer extends Drawer {
    void setComponentRange(int component, double min, double max);
}
