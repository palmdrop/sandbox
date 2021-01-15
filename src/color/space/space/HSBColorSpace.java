package color.space.space;

import color.colors.Colors;
import color.colors.hsb.HSB;
import color.colors.hsb.HSBColor;
import color.space.AbstractColorSpace;

public class HSBColorSpace extends AbstractColorSpace {
    private static final ComponentsToColor toColor = components -> {
        return new HSBColor(components[0], components[1], components[2]);
    };

    private static final ColorToComponents toComponents = color -> {
        double h, s, b;
        if(color instanceof HSB) {
            h = ((HSB) color).getHue();
            s = ((HSB) color).getSaturation();
            b = ((HSB) color).getBrightness();
        } else {
            h = Colors.hue(color);
            s = Colors.saturation(color);
            b = Colors.brightness(color);
        }
        return new double[]{h, s, b};
    };

    public HSBColorSpace() {
        super(3, toColor, toComponents);
    }
}
