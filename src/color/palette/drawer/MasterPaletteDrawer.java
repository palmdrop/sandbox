package color.palette.drawer;

import color.colors.Color;
import color.palette.palettes.unbalanced.MasterPalette;
import processing.core.PGraphics;
import render.Drawer;
import util.geometry.Rectangle;
import util.geometry.divider.SimpleDivider;

import java.util.ArrayList;
import java.util.List;

public class MasterPaletteDrawer implements PaletteDrawer {
    private final MasterPalette palette;
    private Rectangle bounds;
    private final double margin;

    private boolean recalculate = true;
    private List<PaletteDrawer> drawers;

    public MasterPaletteDrawer(MasterPalette palette, Rectangle bounds, double margin) {
        this.palette = palette;
        this.bounds = bounds;
        this.margin = margin;

        drawers = new ArrayList<>(palette.getNumberOfRows());
        for(int i = 0; i < palette.getNumberOfRows(); i++) {
            PaletteDrawer drawer = new SimplePaletteDrawer(palette.getRow(i), bounds, Mode.HORIZONTAL, margin);
            drawers.add(drawer);
        }
    }

    @Override
    public Color getColor(int number) {
        return palette.getColor(number);
    }
    public Color getColor(int row, int column) {
        return palette.getColor(row, column);
    }

    @Override
    public Rectangle getSection(int number) {
        int row = number / palette.getNumberOfRows();
        int column = number % palette.getNumberOfColumns();
        return getSection(row, column);
    }
    public Rectangle getSection(int row, int column) {
        if(recalculate) calculateSections();
        return drawers.get(row).getSection(column);
    }

    public Rectangle getRowSection(int number) {
        if(recalculate) calculateSections();
        return drawers.get(number).getBounds();
    }

    @Override
    public MasterPalette getPalette() {
        return palette;
    }

    @Override
    public PGraphics draw(PGraphics canvas, double frequency) {
        if(recalculate) {
            calculateSections();
        }

        for(Drawer drawer : drawers) {
            drawer.draw(canvas);
        }

        return canvas;
    }

    private void calculateSections() {
        List<Rectangle> mainSections = SimpleDivider.divide(1, palette.getNumberOfRows(), 0, 0, bounds);
        for(int i = 0; i < mainSections.size(); i++) {
            drawers.get(i).setBounds(mainSections.get(i));
        }
        recalculate = false;
    }

    @Override
    public Rectangle getBounds() {
        return bounds;
    }

    @Override
    public void setBounds(Rectangle bounds) {
        if(!bounds.equals(this.bounds)) {
            this.bounds = bounds;
            recalculate = true;
        }
    }
}
