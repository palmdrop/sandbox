package color.space.space;

import color.colors.Colors;
import color.colors.rgb.RGB;
import color.colors.rgb.RGBColor;
import color.space.AbstractColorSpace;

public class RGBColorSpace extends AbstractColorSpace {
    private final static ComponentsToColor toColor = components -> {
        return new RGBColor(components[0], components[1], components[2]);
    };

    private final static ColorToComponents toComponents = color -> {
        double r, g, b;
        if(color instanceof RGB) {
            r = ((RGB) color).getRed();
            g = ((RGB) color).getGreen();
            b = ((RGB) color).getBlue();
        } else {
            r = Colors.red(color);
            g = Colors.green(color);
            b = Colors.blue(color);
        }
        return new double[]{r, g, b};
    };

    public RGBColorSpace() {
        super(3, toColor, toComponents);
    }
}
