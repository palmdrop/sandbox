package color.palette.palettes;

import color.colors.Color;
import color.palette.Palette;
import color.palette.file.PaletteFileFormatException;

import java.util.List;

public interface Palette2D extends Palette {
    Palette getRow(int index);
    Color getColor(int row, int column);

    int getNumberOfRows();
    int getNumberOfColumns();
}
