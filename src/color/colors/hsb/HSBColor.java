package color.colors.hsb;

import color.colors.Colors;
import util.math.MathUtils;

import java.awt.*;
import java.io.Serializable;

public class HSBColor implements HSB, Serializable {
    private final double h, s, b;
    private final double alpha;
    private final int rgb;

    public HSBColor(int rgb, double alpha) {
        this.rgb = Colors.withAlpha(rgb, alpha);
        this.alpha = alpha;

        h = Colors.hue(rgb);
        s = Colors.saturation(rgb);
        b = Colors.brightness(rgb);
    }

    public HSBColor(double h, double s, double b) {
        this(h, s, b, 1.0);
    }

    public HSBColor(double h, double s, double b, double alpha) {
        this.h = h;
        this.s = MathUtils.limit(s, 0, 1);
        this.b = MathUtils.limit(b, 0, 1);
        this.alpha = MathUtils.limit(alpha, 0, 1);

        int rgb = Color.HSBtoRGB((float)MathUtils.doubleMod(this.h, 1.0), (float)this.s, (float)this.b);
        this.rgb = Colors.withAlpha(rgb, alpha);
    }


    @Override
    public double getHue() {
        return h;
    }

    @Override
    public double getSaturation() {
        return s;
    }

    @Override
    public double getBrightness() {
        return b;
    }

    @Override
    public double getAlpha() {
        return alpha;
    }

    @Override
    public int toRGB() {
        return rgb;
    }
}
