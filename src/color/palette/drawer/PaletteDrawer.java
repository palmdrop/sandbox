package color.palette.drawer;

import color.colors.Color;
import color.palette.Palette;
import render.Drawer;
import util.geometry.Rectangle;

public interface PaletteDrawer extends Drawer {
    enum Mode {
        VERTICAL,
        HORIZONTAL
    }

    Color getColor(int number);
    Rectangle getSection(int number);
    Palette getPalette();
}
