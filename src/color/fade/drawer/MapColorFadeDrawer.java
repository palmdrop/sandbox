package color.fade.drawer;

import color.fade.ColorFade;
import render.AbstractPixelDrawer;
import sampling.heightMap.HeightMap;
import util.geometry.Rectangle;
import util.math.MathUtils;
import util.vector.Vector;

public class MapColorFadeDrawer extends AbstractPixelDrawer implements ColorFadeDrawer {
    private final ColorFade colorFade;
    private final HeightMap map;

    private double from, to;
    private boolean reversed;

    public MapColorFadeDrawer(ColorFade colorFade, HeightMap map, Rectangle bounds) {
        super(bounds);
        this.colorFade = colorFade;
        this.map = map;
        reversed = false;
        setRange(0, 1);
    }

    @Override
    protected int getColor(Vector p) {
        double m = map.get(p);
        if(reversed) m = 1 - m;
        double v = MathUtils.map(m, 0, 1, from, to);
        return colorFade.get(v).toRGB();
    }

    @Override
    public void setMode(Mode mode) {
    }

    @Override
    public void setRange(double from, double to) {
        this.from = from;
        this.to = to;
    }

    @Override
    public void setReversed(boolean reversed) {
        this.reversed = reversed;
    }
}
