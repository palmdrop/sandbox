package color.palette;

import color.colors.Color;

import java.util.List;

public interface Palette {
    int getNumberOfColors();

    Color getColor(int number);
    default int getRGB(int number) {
        return getColor(number).toRGB();
    }
    List<Color> getColors(); //TODO: List<? extends Color> ???? or Palette<T extends Color> ???
}
