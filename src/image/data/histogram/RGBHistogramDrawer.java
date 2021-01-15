package image.data.histogram;

import color.space.space.RGBColorSpace;
import processing.core.PConstants;
import processing.core.PGraphics;
import util.geometry.Rectangle;

public class RGBHistogramDrawer implements HistogramDrawer {
    private final SimpleHistogramDrawer histogramDrawer;

    public RGBHistogramDrawer(Histogram histogram, Rectangle bounds) {
        this.histogramDrawer = new SimpleHistogramDrawer(histogram, 0, bounds);
    }

    @Override
    public PGraphics draw(PGraphics canvas, double frequency) {
        canvas.beginDraw();
        canvas.blendMode(PConstants.ADD);
        for(int i = 0; i < 3; i++) {
            switch (i) {
                case 0: canvas.fill(255, 0, 0); break;
                case 1: canvas.fill(0, 255, 0); break;
                case 2: canvas.fill(0, 0, 255); break;
            }
            histogramDrawer.setComponent(i);
            histogramDrawer.draw(canvas);
        }

        canvas.endDraw();
        return canvas;
    }

    @Override
    public Rectangle getBounds() {
        return null;
    }

    @Override
    public void setBounds(Rectangle bounds) {

    }
}
