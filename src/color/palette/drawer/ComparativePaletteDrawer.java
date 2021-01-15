package color.palette.drawer;

import color.colors.Color;
import color.palette.Palette;
import processing.core.PGraphics;
import util.geometry.Rectangle;
import util.geometry.divider.SimpleDivider;

import java.util.List;

public class ComparativePaletteDrawer implements PaletteDrawer {
    private final static double DEFAULT_COMPARISON_SIZE = 0.47;
    private final static double DEFAULT_COMPARISON_MARGIN = 2.4;

    private final SimplePaletteDrawer basicPaletteDrawer;

    private double comparisonSize;
    private double comparisonMargin;

    public ComparativePaletteDrawer(Palette palette, Rectangle bounds, Mode mode, double mainMargin, double comparisonSize, double comparisonMargin) {
        this(palette, bounds, mode, mainMargin);
        this.comparisonSize = comparisonSize;
        this.comparisonMargin = comparisonMargin;
    }

    public ComparativePaletteDrawer(Palette palette, Rectangle bounds, Mode mode, double margin) {
        basicPaletteDrawer = new SimplePaletteDrawer(palette, bounds, mode, margin);
        comparisonSize = DEFAULT_COMPARISON_SIZE;
        comparisonMargin = DEFAULT_COMPARISON_MARGIN;
    }

    @Override
    public Color getColor(int number) {
        return basicPaletteDrawer.getColor(number);
    }

    @Override
    public Rectangle getSection(int number) {
        return basicPaletteDrawer.getSection(number);
    }

    @Override
    public Palette getPalette() {
        return basicPaletteDrawer.getPalette();
    }

    private void drawColorComparison(PGraphics canvas, Rectangle section) {
        List<Rectangle> subsections;
        double effectiveMarginAmount = comparisonMargin / getPalette().getNumberOfColors();

        double length = (int) (comparisonSize * (basicPaletteDrawer.getMode() == Mode.VERTICAL ? section.getWidth() : section.getHeight()));
        double width = length / getPalette().getNumberOfColors();

        double margin = length * effectiveMarginAmount;

        double sx = section.x + section.width / 2.0;
        double sy = section.y + section.height / 2.0;

        double sw, sh;
        int vertical, horizontal;
        if(basicPaletteDrawer.getMode() == Mode.VERTICAL) {
            sx -= length / 2.0;
            sy -= width / 2.0;
            sw = length;
            sh = width;

            vertical = getPalette().getNumberOfColors();
            horizontal = 1;

        } else {
            sx -= width / 2.0;
            sy -= length / 2.0;
            sw = width;
            sh = length;

            vertical = 1;
            horizontal = getPalette().getNumberOfColors();
        }
        Rectangle secondarySection = new Rectangle(sx, sy, sw, sh);
        subsections = SimpleDivider.divide(vertical, horizontal, margin, margin, secondarySection);

        for(int i = 0; i < getPalette().getNumberOfColors(); i++) {
            Color color = getPalette().getColor(i);
            Rectangle sub = subsections.get(i);

            double x = sub.x + sub.width / 2.0;
            double y = sub.y + sub.height / 2.0;

            canvas.fill(color.toRGB());
            canvas.ellipse((float)x, (float)y, (float)sub.width, (float)sub.height);
        }

    }

    @Override
    public PGraphics draw(PGraphics canvas, double frequency) {
        basicPaletteDrawer.draw(canvas, frequency);

        for(int i = 0; i < getPalette().getNumberOfColors(); i++) {
            Rectangle section = getSection(i);
            drawColorComparison(canvas, section);
        }

        return canvas;
    }

    @Override
    public Rectangle getBounds() {
        return basicPaletteDrawer.getBounds();
    }

    @Override
    public void setBounds(Rectangle bounds) {
        basicPaletteDrawer.setBounds(bounds);
    }
}
