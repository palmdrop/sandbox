package color.fade.drawer;

import color.fade.ColorFade;
import render.AbstractPixelDrawer;
import util.geometry.Rectangle;
import util.math.MathUtils;
import util.vector.Vector;

public class SimpleColorFadeDrawer extends AbstractPixelDrawer implements ColorFadeDrawer {
    private Mode mode;
    private final ColorFade colorFade;
    private double from, to;

    private boolean reversed;

    public SimpleColorFadeDrawer(ColorFade colorFade, Rectangle bounds, Mode mode) {
        this(colorFade, bounds, 0.0, 1.0, mode);
    }
    public SimpleColorFadeDrawer(ColorFade colorFade, Rectangle bounds, double from, double to, Mode mode) {
        super(bounds);
        setRange(from, to);
        this.colorFade = colorFade;
        this.mode = mode;
        this.reversed = false;
    }

    @Override
    public void setRange(double from, double to) {
        if(from < 0 || from > 1.0 || to < 0 || to > 1.0) throw new IllegalArgumentException();
        this.from = from;
        this.to = to;
    }

    @Override
    public void setReversed(boolean reversed) {
        this.reversed = reversed;
    }

    @Override
    public void setMode(Mode mode) {
        this.mode = mode;
    }

    @Override
    protected int getColor(Vector p) {
        double v = mode == Mode.HORIZONTAL ? p.getX() : p.getY();
        if(reversed) v = 1 - v;
        v = MathUtils.map(v, 0, 1, from, to);

        return colorFade.get(v).toRGB();
    }
}
