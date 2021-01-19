package color.palette.palettes.balanced.sample1D;

import color.colors.Color;
import color.fade.ColorFade;
import color.fade.fades.SimpleColorFade;
import color.space.ColorSpace;
import sampling.countour.Contour;
import sampling.countour.Contours;
import util.math.MathUtils;

public class FadePalette extends Sampler1DPalette {
    public FadePalette(ColorFade fade, double[] samples) {
        super(fade, samples);
    }

    public FadePalette(ColorFade fade, int samples) {
        this(fade, 0.0, 1.0, samples);
    }
    public FadePalette(ColorFade fade, double from, double to, int samples) {
        this(fade,
             Contours.toArray(Contours.easing(MathUtils.EasingMode.EASE_IN, 1.0)
                              .remap(0, 1, from, to), samples)
        );
    }

    public static FadePalette fromColor(Color c1, Color c2, ColorSpace colorSpace, int samples) {
        return fromColor(c1, c2, Contours.linear(0, 1), colorSpace, samples);
    }

    public static FadePalette fromColor(Color c1, Color c2, Contour fade, ColorSpace colorSpace, int samples) {
        ColorFade colorFade = new SimpleColorFade(c1, c2, fade, colorSpace);
        return new FadePalette(colorFade, samples);
    }
}
