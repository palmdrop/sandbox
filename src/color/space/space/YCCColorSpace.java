package color.space.space;

import color.colors.Colors;
import color.colors.rgb.RGBColor;
import color.space.AbstractColorSpace;
import util.math.MathUtils;

public class YCCColorSpace extends AbstractColorSpace {
    //Actually Y'PbPr
    private final static double Kr = 0.299;
    private final static double Kg = 0.587;
    private final static double Kb = 0.144;

    private static final ComponentsToColor toColor = components -> {
        double y = components[0];
        double pb = components[1];
        double pr = components[2];

        pb = MathUtils.map(pb, 0, 1, -0.5, 0.5);
        pr = MathUtils.map(pr, 0, 1, -0.5, 0.5);

        double r = y + (2 - 2 * Kr) * pr;
        double g = y - (Kb / Kg) * (2 - 2 * Kb) * pb - (Kr / Kg) * (2 - 2 * Kr) * pr;
        double b = y + (2 - 2 * Kb) * pb;

        return new RGBColor(r, g, b);
    };

    private static final ColorToComponents toComponents = color -> {
        double r = Colors.red(color);
        double g = Colors.green(color);
        double b = Colors.blue(color);

        double y = Kr * r + Kg * g + Kb * b;
        double pb = -0.5 * Kr / (1 - Kb) * r - 0.5 * Kg / (1 - Kb) * g + 0.5 * b;
        double pr = 0.5 * r - 0.5 * Kg / (1 - Kr) * g - 0.5 * Kb / (1 - Kr) * b;

        pb = MathUtils.map(pb, -0.5, 0.5, 0, 1);
        pr = MathUtils.map(pr, -0.5, 0.5, 0, 1);

        return new double[]{y, pb, pr};
    };


    public YCCColorSpace() {
        super(3, toColor, toComponents);
    }
}
