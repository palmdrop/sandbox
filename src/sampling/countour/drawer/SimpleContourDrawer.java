package sampling.countour.drawer;

import color.palette.Palette;
import processing.core.PGraphics;
import sampling.Sampler1D;
import util.geometry.Rectangle;
import util.math.MathUtils;

public class SimpleContourDrawer extends AbstractContourDrawer {
    private boolean axis = true;
    private double axisWidth = 1.0;
    private boolean drawNumbers = false;
    private double numberDetail = 2;

    private double width = 2.0;
    private boolean lines = true;

    private int samplePoints;

    public SimpleContourDrawer(Sampler1D<Double> contour, int samplePoints, Mode mode, Rectangle section, Rectangle bounds) {
        super(contour, mode, section, bounds);
        this.contour = contour;
        this.samplePoints = samplePoints;
    }

    @Override
    public PGraphics draw(PGraphics canvas, double frequency) {
        drawContour(canvas);
        if(axis) drawAxis(canvas);

        return canvas;
    }

    private void drawContour(PGraphics canvas) {
        canvas.strokeWeight((float) width);

        double pX = 0, pY = 0;
        for(int i = 0; i < samplePoints; i++) {
            double input = MathUtils.map(i , 0, samplePoints - 1, section.x, section.x + section.width);
            double value = contour.get(input);

            double x, y;
            if(mode == Mode.HORIZONTAL) {
                x = MathUtils.map(i, 0, samplePoints - 1, bounds.x, bounds.x + bounds.width);
                y = MathUtils.map(value, section.y, section.y + section.height, bounds.y + bounds.height, bounds.y);
            } else {
                x = MathUtils.map(value, section.y, section.y + section.height, bounds.x + bounds.width, bounds.x);
                y = MathUtils.map(i, 0, samplePoints - 1, bounds.y, bounds.y + bounds.height);
            }

            if(!lines) {
                canvas.point((float)x, (float)y);
            } else if(i != 0) {
                canvas.line((float)pX, (float)pY, (float)x, (float)y);
            }

            pX = x;
            pY = y;
        }
    }

    private void drawAxis(PGraphics canvas) {
        canvas.strokeWeight((float) axisWidth);

        double x0 = MathUtils.map(0, section.x, section.x + section.width, bounds.x, bounds.x + bounds.width);
        double y0 = MathUtils.map(0, section.y + section.height, section.y, bounds.y, bounds.y + bounds.height);

        if(section.x <= 0 && section.x + section.width >= 0) {
            canvas.line((float)x0, (float)bounds.y, (float)x0, (float)(bounds.y + bounds.height));
        }
        if(section.y <= 0 && section.y + section.height >= 0) {
            canvas.line((float)bounds.x, (float)y0, (float)(bounds.x + bounds.width), (float)y0);
        }
    }

    public void asLines(boolean lines) {
        this.lines = lines;
    }

    public void showAxes(boolean axis) {
        this.axis = axis;
    }

    public void setWidth(double width) {
        this.width = width;
    }

}
