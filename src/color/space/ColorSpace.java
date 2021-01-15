package color.space;

import color.colors.Color;
import color.colors.rgb.RGBColor;

public interface ColorSpace {
    Color getColor(double... components);
    default int getRGB(double... components) {
        return getColor(components).toRGB();
    }

    int getNumberOfComponents();

    double[] getComponents(Color color);
    default double[] getComponents(int rgb) {
        return getComponents(new RGBColor(rgb));
    }
}
