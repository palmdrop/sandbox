package color.palette.drawer;

import color.colors.Color;
import color.palette.Palette;
import util.geometry.Rectangle;
import util.geometry.divider.*;

import processing.core.PGraphics;

import java.util.List;


public class SimplePaletteDrawer extends AbstractPaletteDrawer<Palette> implements PaletteDrawer {
    private double margin;
    private Mode mode;
    private List<Rectangle> sections;

    public SimplePaletteDrawer(Palette palette, Rectangle bounds, Mode mode, double margin) {
        super(palette, bounds);
        this.mode = mode;
        this.margin = margin;
        calculateSections();
    }

    @Override
    protected void calculateSections() {
        int numberOfColors = palette.getNumberOfColors();

        int vDiv, hDiv;
        if(mode == Mode.VERTICAL) {
            vDiv = 1;
            hDiv = numberOfColors;
        } else {
            vDiv = numberOfColors;
            hDiv = 1;
        }
        sections = new SimpleDivider(vDiv, hDiv, margin, margin).divide(bounds);
    }


    @Override
    public PGraphics draw(PGraphics canvas, double frequency) {
        return super.draw(canvas, frequency);
    }

    @Override
    public Color getColor(int number) {
        return palette.getColor(number);
    }

    @Override
    public Rectangle getSection(int number) {
        return sections.get(number);
    }

    private void setMode(Mode mode) {
        if(mode != this.mode) {
            this.mode = mode;
            calculateSections();
        }
    }
    public Mode getMode() {
        return mode;
    }

    public void setMargin(double margin) {
        if(margin != this.margin) {
            this.margin = margin;
            calculateSections();
        }
    }
    public double getMargin() {
        return margin;
    }
}
