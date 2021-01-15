package color.fade.fades;

import color.colors.Color;
import color.colors.Colors;
import color.fade.ColorFade;
import color.space.ColorSpace;
import sampling.countour.Contour;
import sampling.countour.Contours;

public class SimpleColorFade implements ColorFade {
    private final ColorSpace colorSpace;
    private final Color start, end;
    private final Contour fade;

    public SimpleColorFade(Color start, Color end, ColorSpace colorSpace) {
        this(start, end, Contours.linear(0, 1), colorSpace);
    }

    public SimpleColorFade(Color start, Color end, Contour fade, ColorSpace colorSpace) {
        this.colorSpace = colorSpace;
        this.start = start;
        this.end = end;
        this.fade = fade;
    }

    public Color get(double amount) {
        return Colors.lerp(start, end, fade.get(amount), colorSpace);
    }

    public static SimpleColorFade fromComponents(double[] min, double[] max, ColorSpace colorSpace) {
        return fromComponents(min, max, Contours.linear(0, 1), colorSpace);
    }
    public static SimpleColorFade fromComponents(double[] min, double[] max, Contour fade, ColorSpace colorSpace) {
        Color start = colorSpace.getColor(min);
        Color end   = colorSpace.getColor(max);
        return new SimpleColorFade(start, end, fade, colorSpace);
    }
}
