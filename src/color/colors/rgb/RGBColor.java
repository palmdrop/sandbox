package color.colors.rgb;

import color.colors.Color;
import color.colors.Colors;
import util.math.MathUtils;

import java.io.Serializable;

public class RGBColor implements RGB, Serializable {
    private final int rgb;

    public RGBColor(int rgb) {
        this.rgb = rgb;
    }

    public RGBColor(int rgb, double alpha) {
        //this.rgb = Colors.withAlpha(rgb, alpha);
        this(Colors.red(rgb), Colors.green(rgb), Colors.blue(rgb), 255 * alpha);
    }

    public RGBColor(int r, int g, int b) {
        this(r, g, b, 255);
    }

    public RGBColor(int r, int g, int b, int alpha) {
        this.rgb = (alpha & 255) << 24 | (r & 255) << 16 | (g & 255) << 8 | (b & 255);
    }

    public RGBColor(double r, double g, double b) {
        this(r, g, b, 1.0);
    }

    public RGBColor(double r, double g, double b, double alpha) {
        this(
                (int)(255 * MathUtils.limit(r, 0, 1)),
                (int)(255 * MathUtils.limit(g, 0, 1)),
                (int)(255 * MathUtils.limit(b, 0, 1)),
                (int)(255 * MathUtils.limit(alpha, 0, 1))
        );
    }

    @Override
    public int toRGB() {
        return rgb;
    }

    @Override
    public double getRed() {
        return Colors.red(rgb);
    }

    @Override
    public double getGreen() {
        return Colors.green(rgb);
    }

    @Override
    public double getBlue() {
        return Colors.blue(rgb);
    }

    @Override
    public double getAlpha() {
        return Colors.alpha(rgb);
    }
}
