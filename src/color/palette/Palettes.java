package color.palette;

import color.colors.Color;
import color.colors.Colors;
import color.fade.ColorFade;
import color.fade.fades.MultiColorFade;
import color.palette.palettes.balanced.BalancedPalette;
import color.palette.palettes.balanced.BasicPalette;
import color.palette.palettes.balanced.sample1D.FadePalette;
import color.space.ColorSpace;
import sampling.countour.Contours;
import util.math.MathUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.BinaryOperator;

public class Palettes {
    // OPERATIONS
    public static BalancedPalette componentVariation(Palette palette, ColorSpace colorSpace, double[] values, MathUtils.BinaryOpMode binaryOpMode) {
        return componentVariation(palette, colorSpace, values, MathUtils.getBinaryOperator(binaryOpMode));
    }

    public static BalancedPalette componentVariation(Palette palette, ColorSpace colorSpace, double[] values, BinaryOperator<Double> operation) {
        if(colorSpace.getNumberOfComponents() != values.length) throw new IllegalArgumentException();

        List<Color> newColors = new ArrayList<>(palette.getNumberOfColors());
        for(int i = 0; i < palette.getNumberOfColors(); i++) {
            Color newColor = Colors.variation(palette.getColor(i), values, colorSpace, operation);
            newColors.add(newColor);
        }
        return new BasicPalette(newColors);
    }

    public interface ColorOp {
        Color apply(Color c1, Color c2, ColorSpace colorSpace);
    }

    public static BalancedPalette sourceVariation(Palette palette, ColorSpace colorSpace, List<? extends Color> sourceColors, ColorOp colorOp) {
        if(palette.getNumberOfColors() < sourceColors.size()) throw new IllegalArgumentException();
        List<Color> newColors = new ArrayList<>(palette.getNumberOfColors());
        for(int i = 0; i < palette.getNumberOfColors(); i++) {
            Color oldColor = palette.getColor(i);
            Color newColor = colorOp.apply(oldColor, sourceColors.get(i), colorSpace);
            newColors.add(newColor);
            //TODO: something is wrong with RGB to HSB conversion!
        }
        return new BasicPalette(newColors);
    }

    public static BalancedPalette sourceVariation(Palette palette, ColorSpace colorSpace, Palette sourcePalette, ColorOp colorOp) {
        return sourceVariation(palette, colorSpace, sourcePalette.getColors(), colorOp);
    }

    public static BalancedPalette sourceVariation(Palette palette, ColorSpace colorSpace, Color sourceColor, ColorOp colorOp) {
        return sourceVariation(palette, colorSpace, staticPalette(sourceColor, palette.getNumberOfColors()), colorOp);
    }

    public static Color fold(Palette palette, ColorSpace colorSpace, ColorOp colorOp, Color base) {
        Color color = base;
        for(int i = 0; i < palette.getNumberOfColors(); i++) {
            color = colorOp.apply(color, palette.getColor(i), colorSpace);
        }
        return color;
    }

    public static Color average(Palette palette, ColorSpace colorSpace) {
        double[] blank = new double[colorSpace.getNumberOfComponents()];
        //Arrays.fill(blank, 0); //Array is initialized to 0

        return fold(palette,
                colorSpace,
                (c1, c2, cs) -> Colors.binaryOp(c1, c2, cs, (v1, v2) -> v1 + v2 / palette.getNumberOfColors()),
                colorSpace.getColor(blank));
    }

    public static BalancedPalette varyHue(Palette palette, double amount, MathUtils.BinaryOpMode mode) {
        double[] comp = {amount, 0, 0};
        return componentVariation(palette, Colors.HSB_SPACE, comp, MathUtils.getBinaryOperator(mode));
    }

    public static BalancedPalette varySaturation(Palette palette, double amount, MathUtils.BinaryOpMode mode) {
        double[] comp = {0, amount, 0};
        return componentVariation(palette, Colors.HSB_SPACE, comp, MathUtils.getBinaryOperator(mode));
    }

    public static BalancedPalette varyBrightness(Palette palette, double amount, MathUtils.BinaryOpMode mode) {
        double[] comp = {0.0, 0.0, amount};
        return componentVariation(palette, Colors.HSB_SPACE, comp, MathUtils.getBinaryOperator(mode));
    }

    public static BalancedPalette bleach(Palette palette, double amount, MathUtils.BinaryOpMode mode) {
        double[] comp = {amount, amount, amount};
        return componentVariation(palette, Colors.RGB_SPACE, comp, MathUtils.getBinaryOperator(mode));
    }

    // PALETTE CHANGING
    public static BalancedPalette changeNumberOfColors(Palette palette, int numberOfColors) {
        return changeNumberOfColors(palette, 0, 1, numberOfColors);
    }
    public static BalancedPalette changeNumberOfColors(Palette palette, double from, double to, int numberOfColors) {
        return changeNumberOfColors(palette, Colors.XYZ_SPACE, from, to, numberOfColors);
    }

    public static BalancedPalette changeNumberOfColors(Palette palette, ColorSpace colorSpace, double from, double to, int numberOfColors) {
        ColorFade fade = new MultiColorFade(colorSpace, Contours.linear(from, to), palette);
        return new FadePalette(fade, numberOfColors);
    }

    //GENERATION
    public static BalancedPalette staticPalette(Color color, int numberOfColors) {
        List<Color> colors = new ArrayList<>(numberOfColors);
        for(int i = 0; i < numberOfColors; i++) {
            colors.add(color);
        }
        return new BasicPalette(colors);
    }

    public static BalancedPalette rampPalette(double startHue, double endHue, double contrast, int numberOfColors) {
        return null;
    }

    //SORTING
    public static BalancedPalette sort(BalancedPalette palette, Comparator<Color> colorComparator) {
        List<Color> colors = palette.getColors();
        colors.sort(colorComparator);
        return palette;
    }
}
