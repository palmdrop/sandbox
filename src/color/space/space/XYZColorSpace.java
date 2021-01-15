package color.space.space;

import color.colors.Colors;
import color.colors.rgb.RGBColor;
import color.space.AbstractColorSpace;

public class XYZColorSpace extends AbstractColorSpace {
    private static ComponentsToColor toColor = components -> {
        double x = components[0];
        double y = components[1];
        double z = components[2];

        double r =  3.24096994 * x - 1.53738318 * y - 0.49861076 * z;
        double g = -0.96924364 * x + 1.87596750 * y + 0.04155506 * z;
        double b =  0.05563008 * x - 0.20397696 * y + 1.05687151 * z;

        return new RGBColor(r, g, b);
    };

    private static ColorToComponents toComponents = color -> {
        //TODO: gamma correction? convert to sRGB first?
        double r = Colors.red(color);
        double g = Colors.green(color);
        double b = Colors.blue(color);

        double x = 0.41239080 * r + 0.35758434 * g + 0.18048079 * b;
        double y = 0.21263901 * r + 0.71516868 * g + 0.07219232 * b;
        double z = 0.01933082 * r + 0.11919478 * g + 0.95053215 * b;

        return new double[]{x, y, z};
    };

    public XYZColorSpace() {
        super(3, toColor, toComponents);
    }
}
