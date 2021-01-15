package image.data.histogram;

import processing.core.PGraphics;
import util.geometry.Rectangle;
import util.math.MathUtils;

public class SimpleHistogramDrawer implements HistogramDrawer {
    private final Histogram histogram;
    private Rectangle bounds;
    private int component;

    public SimpleHistogramDrawer(Histogram histogram, int component, Rectangle bounds) {
        this.histogram = histogram;
        this.bounds = bounds;
        this.component = component;
    }

    @Override
    public PGraphics draw(PGraphics canvas, double frequency) {
        double barWidth = bounds.width / histogram.getDetail();
        int componentMax = histogram.getMaxValue(component);

        canvas.beginDraw();

        double offset = bounds.x;
        for(int i = 0; i < histogram.getDetail(); i++) {
            int value = histogram.getValue(component, i);
            double barHeight = MathUtils.map(value, 0, componentMax, 0, bounds.height);

            canvas.rect((float)offset, (float)(bounds.getY() + (bounds.getHeight() - barHeight)), (float)barWidth, (float)barHeight);

            offset += barWidth;
        }

        canvas.endDraw();
        return canvas;
    }

    public void setComponent(int component) {
        this.component = component;
    }

    @Override
    public Rectangle getBounds() {
        return bounds;
    }

    @Override
    public void setBounds(Rectangle bounds) {
        this.bounds = bounds;
    }
}
