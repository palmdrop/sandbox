package color.colors.hsb;

import color.colors.AlphaColor;

public interface HSB extends AlphaColor {
    double getHue();
    double getSaturation();
    double getBrightness();
}
