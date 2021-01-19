package color.palette.palettes.balanced;

import color.colors.Color;
import color.palette.Palette;

public interface BalancedPalette extends Palette {

    BalancedPalette addColor(int index, Color color);
    BasicPalette addColor(Color color);

    Color removeColor(int index);
    boolean removeColor(Color color);
}
