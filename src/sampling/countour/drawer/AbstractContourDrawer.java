package sampling.countour.drawer;

import processing.core.PGraphics;
import render.AbstractDrawer;
import sampling.Sampler1D;
import util.geometry.Rectangle;
import util.space.region.Range;

public abstract class AbstractContourDrawer extends AbstractDrawer implements ContourDrawer {
    protected Sampler1D<Double> contour;
    protected Rectangle section;
    protected Mode mode;

    public AbstractContourDrawer(Sampler1D<Double> contour, Mode mode, Rectangle section, Rectangle bounds) {
        super(bounds);
        this.contour = contour;
        this.section = section;
        this.mode = mode;
        this.bounds = bounds;
    }

    @Override
    public void setSection(Rectangle section) {
        this.section = section;
    }

    @Override
    public Rectangle getSection() {
        return section;
    }

    @Override
    public void setMode(Mode mode) {
        this.mode = mode;
    }

    @Override
    public Mode getMode() {
        return mode;
    }
}
