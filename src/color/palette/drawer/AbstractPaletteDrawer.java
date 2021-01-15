package color.palette.drawer;

import color.palette.Palette;
import processing.core.PGraphics;
import util.geometry.Rectangle;

public abstract class AbstractPaletteDrawer<T extends Palette> implements PaletteDrawer {
    protected final T palette;
    protected Rectangle bounds;

    protected AbstractPaletteDrawer(T palette, Rectangle bounds) {
        if(palette == null || bounds == null) throw new IllegalArgumentException();
        this.palette = palette;
        this.bounds = bounds;
    }

    protected abstract void calculateSections();

    @Override
    public PGraphics draw(PGraphics canvas, double frequency) {
        canvas.noStroke();

        for(int i = 0; i < palette.getNumberOfColors(); i++) {
            canvas.fill(getColor(i).toRGB());

            Rectangle section = getSection(i);
            Rectangle.render(canvas, section);
        }

        return canvas;
    }

    @Override
    public Rectangle getBounds() {
        return bounds;
    }

    @Override
    public void setBounds(Rectangle bounds) {
        if(!bounds.equals(this.bounds)) {
            this.bounds = bounds;
            calculateSections();
        }
    }

    @Override
    public T getPalette() {
        return palette;
    }
}
