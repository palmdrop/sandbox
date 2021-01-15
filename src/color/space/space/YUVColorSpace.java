package color.space.space;

import color.colors.Colors;
import color.colors.rgb.RGBColor;
import color.space.AbstractColorSpace;
import util.math.MathUtils;

public class YUVColorSpace extends AbstractColorSpace {
    private static final double Wr = 0.299;
    private static final double Wb = 0.114;
    private static final double Wg = 1.0 - Wr - Wb;

    private static final double Umax = 0.436;
    private static final double Vmax = 0.615;

    private static final ComponentsToColor toColor = components -> {
        double y = components[0];
        double u = components[1];
        double v = components[2];

        u = MathUtils.map(u, 0, 1, -Umax, Umax);
        v = MathUtils.map(v, 0, 1, -Vmax, Vmax);

        double r = y + v * (1 - Wr) / Vmax;
        double g = y - u * Wb * (1 - Wb) / (Umax * Wg) - v * Wr * (1 - Wr) / (Vmax * Wg);
        double b = y + u * (1 - Wb) / Umax;

        return new RGBColor(r, g, b);
    };
    private static final ColorToComponents toComponents = color -> {
        double r = Colors.red(color);
        double g = Colors.green(color);
        double b = Colors.blue(color);

        double y = Wr * r + Wg * g + Wb * b;
        double u = Umax * (b - y) / (1 - Wb);
        double v = Vmax * (r - y) / (1 - Wr);


        u = MathUtils.map(u, -Umax, Umax, 0, 1);
        v = MathUtils.map(v, -Vmax, Vmax, 0, 1);

        return new double[]{y, u, v};
    };

    public YUVColorSpace() {
        super(3, toColor, toComponents);
    }

}
