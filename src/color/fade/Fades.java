package color.fade;

import color.colors.Color;
import color.colors.Colors;
import color.fade.fades.MultiColorFade;
import color.palette.Palettes;
import color.palette.palettes.balanced.sample1D.FadePalette;
import color.space.ColorSpace;
import sampling.countour.Contours;
import util.math.MathUtils;

public class Fades {

    public static ColorFade modifyHSB(ColorFade fade, ColorSpace space, double[] componentMod, MathUtils.BinaryOpMode operation, int detail) {
        return new MultiColorFade(space, Contours.linear(0, 1),
                Palettes.componentVariation(
                        new FadePalette(fade, detail),
                        Colors.HSB_SPACE,
                        componentMod,
                        operation
                )
        );
    }

    public static ColorFade subfade(ColorFade fade, double from, double to) {
        return amount -> {
            double a = MathUtils.map(amount, 0, 1, from, to);
            return fade.get(a);
        };
    }
}
