package color.palette.palettes.unbalanced;

import color.colors.Color;
import color.colors.Colors;
import color.fade.fades.SimpleColorFade;
import color.palette.Palette;
import color.palette.palettes.balanced.BasicPalette;
import color.palette.palettes.balanced.sample1D.FadePalette;
import util.math.MathUtils;

import java.util.ArrayList;
import java.util.List;

public class MainColorPalette implements Palette {
    private final Color mainColor;
    private final Palette secondaryColors;
    private final Palette accentColors;

    public MainColorPalette(Color mainColor, Palette secondaryColors, Palette accentColors) {
        if(secondaryColors.getNumberOfColors() == 0 || accentColors.getNumberOfColors() == 0) throw new IllegalArgumentException();

        this.mainColor = mainColor;
        this.secondaryColors = secondaryColors;
        this.accentColors = accentColors;
    }

    public MainColorPalette(Color mainColor, List<Color> secondaryColors, List<Color> accentColors) {
        this(mainColor, new BasicPalette(secondaryColors), new BasicPalette(accentColors));
    }

    public MainColorPalette(Color mainColor, double secondaryHueVariation, double secondarySaturationMultiple, double secondaryBrightnessMultiple, double secondaryBrightnessPower, int numberOfSecondaryColors, int numberOfAccentColors) {
        if(mainColor == null || numberOfSecondaryColors < 1 || numberOfAccentColors < 1) throw new IllegalArgumentException();

        this.mainColor = mainColor;

        double mainHue = Colors.hue(mainColor);
        double secondaryHue = mainHue + secondaryHueVariation;

        double secondarySaturation = Colors.saturation(mainColor) * secondarySaturationMultiple;
        double secondaryBriStart = Colors.brightness(mainColor) * secondaryBrightnessMultiple;
        double secondaryBriEnd = secondaryBriStart / Math.pow(numberOfSecondaryColors, secondaryBrightnessPower);

        secondaryColors = new FadePalette(
                SimpleColorFade.fromComponents(
                    new double[]{secondaryHue, secondarySaturation, secondaryBriStart},
                    new double[]{secondaryHue, secondarySaturation * secondaryBrightnessPower, secondaryBriEnd},
                    Colors.HSB_SPACE
                ), numberOfSecondaryColors);

        BasicPalette accentColors = new BasicPalette();

        for(int i = 0; i < numberOfAccentColors; i++) {
            if(i % 3 == 0) {
                accentColors.addColor(accented(secondaryColors.getColor(0)));
            } else if(i % 3 == 1) {
                accentColors.addColor(accented(variation(mainColor)));
            } else {
                accentColors.addColor(accented(contrast(accentColors.getColor(0))));
            }
        }

        this.accentColors = accentColors;
    }

    private Color accented(Color color) {
        double[] hsb = Colors.HSB_SPACE.getComponents(color);
        return Colors.HSB_SPACE.getColor(hsb[0], 1.0, hsb[2]);
    }

    private Color variation(Color color) {
        double min = 0.06;
        double max = 0.1;
        double[] hsb = Colors.HSB_SPACE.getComponents(color);

        if(Math.random() < 0.5) hsb[0] += MathUtils.random(min, max);
        else                    hsb[0] -= MathUtils.random(min, max);

        return Colors.HSB_SPACE.getColor(hsb);
    }

    private Color contrast(Color color) {
        double[] hsb = Colors.HSB_SPACE.getComponents(color);
        double b = Math.random() > 0.5 ? 1 : 0;
        double h = (Math.random() > 0.5) ? 1 : 0;
        return variation(Colors.HSB_SPACE.getColor(hsb[0], h, b));
    }

    @Override
    public int getNumberOfColors() {
        return 1 + secondaryColors.getNumberOfColors() + accentColors.getNumberOfColors();
    }

    @Override
    public Color getColor(int number) {
        if(number == 0) {
            return mainColor;
        } else if((number - 1) < secondaryColors.getNumberOfColors()) {
            return secondaryColors.getColor(number - 1);
        } else if((number - 1 - secondaryColors.getNumberOfColors()) < accentColors.getNumberOfColors()) {
            return accentColors.getColor(number - 1 - secondaryColors.getNumberOfColors());
        }
        throw new IndexOutOfBoundsException();
    }

    @Override
    public List<Color> getColors() {
        List<Color> allColors = new ArrayList<>(getNumberOfColors());
        allColors.add(mainColor);
        allColors.addAll(secondaryColors.getColors());
        allColors.addAll(accentColors.getColors());
        return allColors;
    }

    public Color getMainColor() {
        return mainColor;
    }
    public Palette getSecondaryColors() {
        return secondaryColors;
    }
    public Palette getAccentColors() {
        return accentColors;
    }


    public static MainColorPalette fromMainColor(Color mainColor) {
        double hueVar = MathUtils.random(0.36, 0.45);
        double secSatMult = MathUtils.map(Colors.saturation(mainColor), 0, 1, 0.8, 0.1);

        double secBriMult = 0.9;
        double secBriPow = MathUtils.random(1.3, 2.0);


        return new MainColorPalette(mainColor,
                hueVar * (Math.random() < 0.5 ? -1 : 1),
                secSatMult,
                secBriMult,
                secBriPow,
                3,
                3);
    }

    public static MainColorPalette fromSecondaryColor(Color secondaryColor) {
        double hueVar = MathUtils.random(0.36, 0.45);
        double secSatMin = 0.8;
        double secSatMax = 0.1;

        double secBriMult = 0.9;
        double secBriPow = MathUtils.random(1.3, 2.0);

        double mainHue = Colors.hue(secondaryColor) - hueVar;
        double mainSat = Colors.saturation(secondaryColor) * 2.0;
        double mainBri = Colors.brightness(secondaryColor);

        Color mainColor = Colors.HSB_SPACE.getColor(mainHue, mainSat, mainBri);

        return new MainColorPalette(mainColor,
                hueVar * (Math.random() < 0.5 ? -1 : 1),
                Colors.saturation(secondaryColor) / mainSat,//MathUtils.map(Colors.saturation(mainColor), 0, 1, secSatMin, secSatMax),
                secBriMult,
                secBriPow,
                3,
                3);

    }




}
