package color.palette.palettes.unbalanced;

import color.colors.Color;
import color.fade.ColorFade;
import color.fade.fades.RampFade;
import color.palette.Palette;
import color.palette.palettes.Palette2D;
import color.palette.palettes.balanced.sample1D.FadePalette;

import java.util.ArrayList;
import java.util.List;

public class MasterPalette implements Palette2D {
    private final int rows, columns;
    private final List<Palette> palettes;

    public MasterPalette(List<Palette> palettes) {
        if(palettes == null || palettes.size() == 0) throw new IllegalArgumentException();
        for (Palette row : palettes) {
            if (palettes.get(0).getNumberOfColors() != row.getNumberOfColors()) throw new IllegalArgumentException();
        }
        this.rows = palettes.size();
        this.columns = palettes.get(0).getNumberOfColors();
        this.palettes = palettes;
    }

    public MasterPalette(int rows, int columns, double hueShift, double hueEase, double saturation, double contrast, RampFade.SatMode mode) {
        this.rows = rows;
        this.columns = columns;
        this.palettes = new ArrayList<>(rows);

        double hue = 0.0;
        for(int i = 0; i < rows; i++) {
            ColorFade fade = new RampFade(hue, hue + hueShift, hueEase, saturation, 1.0, contrast, mode);
            FadePalette palette = new FadePalette(fade, columns);
            palettes.add(palette);
            hue += hueShift / 4;
        }

        //TODO: missing desaturated colors!!?!?!?!?
    }

    @Override
    public int getNumberOfColors() {
        return rows * columns;
    }

    @Override
    public Color getColor(int number) {
        int row = number / getNumberOfRows();
        int column = number % getNumberOfColumns();
        return getColor(row, column);
    }

    @Override
    public List<Color> getColors() {
        List<Color> colors = new ArrayList<>(rows * columns);
        for(Palette palette : palettes) {
            colors.addAll(palette.getColors());
        }
        return colors;
    }

    @Override
    public Palette getRow(int index) {
        return palettes.get(index);
    }

    @Override
    public Color getColor(int row, int column) {
        return palettes.get(row).getColor(column);
    }

    @Override
    public int getNumberOfRows() {
        return rows;
    }

    @Override
    public int getNumberOfColumns() {
        return columns;
    }
}
