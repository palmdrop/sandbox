package color.palette.drawer;

import color.colors.Color;
import color.palette.palettes.unbalanced.MainColorPalette;
import processing.core.PGraphics;
import util.geometry.Rectangle;
import util.geometry.divider.AmountDivider;
import util.geometry.divider.SimpleDivider;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MainPaletteDrawer extends AbstractPaletteDrawer<MainColorPalette> implements PaletteDrawer {
    public static double[] DEFAULT_LAYOUT = {
            // MAIN COLOR
            // 0.0 - 0.33
            0.33,
            // SECONDARY COLORS
            // 0.33 - 0.9
            0.9,
            // ACCENT COLORS
            // 0.9 - 1.0
    };

    private double mainMargin, secondaryMargin, accentMargin;
    private Mode mode;

    private List<Rectangle> sections;
    private boolean recalculate;

    private double[] layout;

    public MainPaletteDrawer(MainColorPalette palette, Rectangle bounds, Mode mode) {
        this(palette, bounds, mode, DEFAULT_LAYOUT);
    }

    public MainPaletteDrawer(MainColorPalette palette, Rectangle bounds, Mode mode, double[] layout) {
        super(palette, bounds);

        this.mainMargin = 0.0;
        this.secondaryMargin = 0.0;
        this.accentMargin = 0.0;

        this.mode = mode;
        this.layout = layout;
        recalculate = true;
    }

    @Override
    public Color getColor(int number) {
        return palette.getColor(number);
    }

    @Override
    public Rectangle getSection(int number) {
        calculateSections();
        return sections.get(number);
    }

    @Override
    public PGraphics draw(PGraphics canvas, double frequency) {
        calculateSections();
        return super.draw(canvas, frequency);
    }


    @Override
    protected void calculateSections() {
        if(!recalculate) return;

        sections = new ArrayList<>(palette.getNumberOfColors());

        // Main division (split main section, secondary section and accent section)
        double xMargin, yMargin;
        AmountDivider divider = new AmountDivider(0, 0);
        if(mode == Mode.VERTICAL) {
            divider.setHorizontal(layout);
            xMargin = 0;
            yMargin = mainMargin;
        } else {
            divider.setVertical(layout);
            xMargin = mainMargin;
            yMargin = 0;
        }
        divider.setMargin(xMargin, yMargin);

        List<Rectangle> mainDiv = divider.divide(bounds);

        sections.add(mainDiv.get(0));

        // Secondary division (split secondary section)
        int v = 1, h = 1;
        if(mode == Mode.VERTICAL) {
            h = palette.getSecondaryColors().getNumberOfColors();
            xMargin = 0;
            yMargin = secondaryMargin;
        } else {
            v = palette.getSecondaryColors().getNumberOfColors();
            xMargin = secondaryMargin;
            yMargin = 0;
        }

        divider = new SimpleDivider(v, h, xMargin, yMargin);

        sections.addAll(divider.divide(mainDiv.get(1)));

        // Accent division (split accent section)
        if(mode == Mode.VERTICAL) {
            v = palette.getAccentColors().getNumberOfColors();
            h = 1;

            xMargin = accentMargin;
            yMargin = 0;
        } else {
            v = 1;
            h = palette.getAccentColors().getNumberOfColors();

            xMargin = 0;
            yMargin = accentMargin;
        }

        divider = new SimpleDivider(v, h, xMargin, yMargin);

        sections.addAll(divider.divide(mainDiv.get(2)));

        recalculate = false;
    }

    public void setMode(Mode mode) {
        if(!Objects.equals(mode, this.mode)) return;

        recalculate = true;
        this.mode = mode;
    }

    public void setMainMargin(double mainMargin) {
        if(mainMargin == this.mainMargin) return;

        recalculate = true;
        this.mainMargin = mainMargin;
    }

    public void setSecondaryMargin(double secondaryMargin) {
        if(secondaryMargin == this.secondaryMargin) return;

        recalculate = true;
        this.secondaryMargin = secondaryMargin;
    }

    public void setAccentMargin(double accentMargin) {
        if(accentMargin == this.accentMargin) return;

        recalculate = true;
        this.accentMargin = accentMargin;
    }

    @Override
    public void setBounds(Rectangle bounds) {
        if(!bounds.equals(this.bounds))return;

        recalculate = true;
        this.bounds = bounds;
    }
}
