package color.palette.palettes.balanced;

import color.colors.Color;
import color.colors.Colors;

public class ComplementaryPalette extends BasicPalette implements BalancedPalette {
    //TODO study complementary colors!? how to find them
    //TODO: allow use with different color models!

    public ComplementaryPalette(int numberOfColors, double startHue, double randomVariation, double saturation, double brightness) {
        this(numberOfColors, startHue, randomVariation, 1.0, saturation, brightness);
        if(numberOfColors < 1) throw new IllegalArgumentException();
    }

    public ComplementaryPalette(int numberOfColors, double startHue, double randomVariation, double hueSpectrum, double saturation, double brightness) {
        super();

        double angle = hueSpectrum / numberOfColors;
        startHue %= 1.0;

        for(int i = 0; i < numberOfColors; i++) {
            double hue = startHue + i * angle + Math.random() * randomVariation;
            Color color = Colors.fromHSB(hue, saturation, brightness, 255);
            addColor(color);
        }
    }
}
