package color.fade.drawer;

import render.Drawer;

public interface ColorFadeDrawer extends Drawer {
    void setMode(Mode mode);
    void setRange(double from, double to);
    void setReversed(boolean reversed);
}
