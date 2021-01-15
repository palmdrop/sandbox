package sampling.countour.drawer;

import render.Drawer;
import util.geometry.Rectangle;
import util.space.region.Range;

public interface ContourDrawer extends Drawer {
    void setSection(Rectangle section);
    Rectangle getSection();
    void setMode(Mode mode);
    Mode getMode();
}
