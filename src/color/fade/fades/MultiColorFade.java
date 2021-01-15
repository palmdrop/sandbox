package color.fade.fades;

import color.colors.Color;
import color.colors.Colors;
import color.fade.ColorFade;
import color.palette.Palette;
import color.space.ColorSpace;
import sampling.countour.Contour;
import util.math.MathUtils;

import java.util.List;

public class MultiColorFade implements ColorFade {
    private final ColorSpace colorSpace;
    private final Contour fade;
    private final List<? extends Color> colors;

    public MultiColorFade(ColorSpace colorSpace, Contour fade, Palette palette) {
        this(colorSpace, fade, palette.getColors());
    }

    public MultiColorFade(ColorSpace colorSpace, Contour fade, Color... colors) {
        this(colorSpace, fade, List.of(colors));
    }

    public MultiColorFade(ColorSpace colorSpace, Contour fade, List<? extends Color> colors) {
        this.colorSpace = colorSpace;
        this.fade = fade;
        this.colors = colors;
    }


    @Override
    public Color get(double amount) {
        if(colors.size() == 1) return colors.get(0);

        double v = fade.get(amount);
        double interval = 1.0 / (colors.size() - 1);

        int index = (int) (v / interval);
        index = MathUtils.limit(index, 0, colors.size() - 2);

        v = (v % interval) / interval;
        if(amount >= 1.0) v = 1.0;

        Color c1 = colors.get(index);
        Color c2 = colors.get(index + 1);

        return Colors.lerp(c1, c2, v, colorSpace);
    }
}
