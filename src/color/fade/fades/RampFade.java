package color.fade.fades;

import color.colors.Color;
import color.colors.Colors;
import color.fade.ColorFade;
import sampling.countour.Contour;
import sampling.countour.Contours;
import util.math.MathUtils;
import util.vector.Vector;

public class RampFade implements ColorFade {
    public enum SatMode {
        LINEAR,
        DYNAMIC
    }

    private final static double START_SAT =
            0.4;
    private final static double MIDDLE_SAT = 0.73;
    private final static double END_SAT =
            0.2;

    private final static double START_BRI =
            0.1;
    private final static double END_BRI =
            0.95;

    private final ColorFade fade;

    public RampFade(double startHue, double endHue, double hueShift, double contrast, SatMode mode) {
        this(startHue, endHue, hueShift, 1.0, 1.0, contrast, mode);
    }

    public RampFade(double startHue, double endHue, double hueEase, double saturation, double brightness, double contrast, SatMode mode) {
        double satAvg = (START_SAT + MIDDLE_SAT + END_SAT) / 3;
        double briAvg = (START_BRI + END_BRI) / 2;

        double startSat = saturation * MathUtils.map(contrast, 0, 1, satAvg, START_SAT);
        double middleSat = saturation * MathUtils.map(contrast, 0, 1, satAvg, MIDDLE_SAT);
        double endSat = saturation * MathUtils.map(contrast, 0, 1, satAvg, END_SAT);

        double startBri = brightness * MathUtils.map(contrast, 0, 1, briAvg, START_BRI);
        double endBri = brightness * MathUtils.map(contrast, 0, 1, briAvg, END_BRI);

        Contour hueFade =
                Contours.easing(MathUtils.EasingMode.EASE_IN_OUT, hueEase);


        Contour satFade;
        if(mode == SatMode.LINEAR) {
            satFade = Contours.easing(middleSat, endSat, MathUtils.EasingMode.EASE_IN_OUT, 1.0);
        } else {
            satFade = Contours.simpleInterpolate(1.3, new Vector(0.0, startSat),
                    new Vector(0.5, middleSat),
                    new Vector(0.75, middleSat/1.5),
                    new Vector(1.0, endSat));
        }


        Contour briFade =
                Contours.easing(startBri, endBri, MathUtils.EasingMode.EASE_OUT, 1.3);

        fade = new ComponentColorFade(
                Colors.HSB_SPACE,
                Colors.HSB_SPACE.getColor(startHue, 0, 0),
                Colors.HSB_SPACE.getColor(endHue, 1, 1),
                hueFade,
                satFade,
                briFade
        );
    }

    @Override
    public Color get(double amount) {
        return fade.get(amount);
    }


    public static RampFade fromColor(Color color, double hueShift, double hueEase, double contrast, SatMode mode) {
        double hue = Colors.hue(color);
        double startHue = hue - hueShift/2;
        double endHue = hue + hueShift/2;

        double sat = Colors.saturation(color);
        double satMid = MIDDLE_SAT;
        double satMultiplier = sat / satMid;

        double bri = Colors.brightness(color);
        double briMid = (START_BRI + END_BRI) / 2; //TODO: not exact though...
        double briMultiplier = bri / briMid;

        return new RampFade(startHue, endHue, hueEase, satMultiplier, briMultiplier, contrast, mode);
    }
}
