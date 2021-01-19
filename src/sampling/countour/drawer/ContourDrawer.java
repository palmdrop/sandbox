package sampling.countour.drawer;

import render.Drawer;
import util.geometry.Rectangle;

public interface ContourDrawer extends Drawer {
    void setSection(Rectangle section);
    Rectangle getSection();
    void setMode(Mode mode);
    Mode getMode();
}
