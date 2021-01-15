package color.space.space;

import color.colors.Color;
import color.colors.Colors;
import color.colors.hsb.HSBColor;
import color.colors.rgb.RGBColor;
import color.space.AbstractColorSpace;
import color.space.ColorSpace;
import util.math.MathUtils;

public class FakeNCSColorSpace extends AbstractColorSpace implements ColorSpace {
    // PRIMARY COLORS
    public static final Color WHITE  = new RGBColor(1.0,  1.0,  1.0);
    public static final Color BLACK  = new RGBColor(0.0,  0.0,  0.0);
    public static final Color GREEN  = new RGBColor(0.0,  0.62, 0.42);
    public static final Color RED    = new RGBColor(0.77, 0.01, 0.2);
    public static final Color YELLOW = new RGBColor(1.0,  0.83, 0.0);
    public static final Color BLUE   = new RGBColor(0.0,  0.54, 0.74);

    public enum HueSection { //TODO: name???
        Y, //0.0
        YR,
        R, //0.25
        RB,
        B, //0.5
        BG,
        G, //0.75
        GY,

        N  // -1
    };

    public static class NCSColor {
        final double blackness;
        final double chromaticness;
        final double hue;

        public NCSColor(double blackness, double chromaticness, double hue) {
            this.blackness = blackness;
            this.chromaticness = chromaticness;
            this.hue = hue;
        }

    }

    // COMPONENTS TO COLOR
    private final static ComponentsToColor toColor = components -> {
        Color c = getBaseHue(components[2]);
        double h = Colors.hue(c);
        double s = 1 - components[1];
        double b = 1 - components[0];
        return new HSBColor(h, s, b);
    };

    private final static ColorToComponents toComponents = color -> {
        return null;//TODO
    };

    public FakeNCSColorSpace() {
        super(3, toColor, toComponents);
    }

    private static Color getBaseHue(double v) {
        v = MathUtils.doubleMod(v, 1.0);

        Color c1, c2;
        double min, max;

        if(v < 0.25) { // Yellow-Red
            c1 = YELLOW;
            c2 = RED;
            min = 0;
            max = 0.25;
        } else if(v < 0.5) { // Red-Blue
            c1 = RED;
            c2 = BLUE;
            min = 0.25;
            max = 0.5;
        } else if(v < 0.75) { // Blue-Green
            c1 = BLUE;
            c2 = GREEN;
            min = 0.5;
            max = 0.75;
        } else { // Green-Yellow
            c1 = GREEN;
            c2 = YELLOW;
            min = 0.75;
            max = 1.0;
        }

        return Colors.lerp(c1, c2, MathUtils.map(v, min, max, 0, 1), Colors.RGB_SPACE);
    }
}
