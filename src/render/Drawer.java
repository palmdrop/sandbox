package render;

import processing.core.PGraphics;
import util.geometry.Rectangle;

public interface Drawer {
    enum Mode {
        VERTICAL,
        HORIZONTAL
    }
    default PGraphics draw(PGraphics canvas) {
        return draw(canvas, 1.0);
    }
    PGraphics draw(PGraphics canvas, double frequency);

    Rectangle getBounds();
    void setBounds(Rectangle bounds);
}
