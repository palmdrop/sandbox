package color.space.space;

import color.colors.Colors;
import color.colors.rgb.RGBColor;
import color.space.AbstractColorSpace;

public class CMYColorSpace extends AbstractColorSpace {
    private final static ComponentsToColor toColor = components -> {
        double r = 1.0 - components[0];
        double g = 1.0 - components[1];
        double b = 1.0 - components[2];
        return new RGBColor(r, g, b);
    };

    private final static ColorToComponents toComponents = color -> {
        double c = 1 - Colors.red(color);
        double m = 1 - Colors.green(color);
        double y = 1 - Colors.blue(color);
        return new double[]{c, m, y};
    };

    public CMYColorSpace() {
        super(3, toColor, toComponents);
    }
}
