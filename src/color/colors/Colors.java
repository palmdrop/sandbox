package color.colors;

import color.colors.hsb.HSB;
import color.colors.hsb.HSBColor;
import color.colors.rgb.RGB;
import color.colors.rgb.RGBColor;
import color.space.ColorSpace;
import color.space.space.*;
import util.math.MathUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.BinaryOperator;

public class Colors {
    public final static RGBColorSpace RGB_SPACE = new RGBColorSpace();
    public final static HSBColorSpace HSB_SPACE = new HSBColorSpace();
    public final static CMYColorSpace CMY_SPACE = new CMYColorSpace();
    public final static XYZColorSpace XYZ_SPACE = new XYZColorSpace();
    public final static FakeNCSColorSpace FAKE_NCS_COLOR_SPACE = new FakeNCSColorSpace();
    public final static YUVColorSpace YUV_SPACE = new YUVColorSpace();
    public final static YCCColorSpace YCC_SPACE = new YCCColorSpace();
    //TODO: make use of colorModel as much as possible!?!?! ex use HSB_MODEL.toColor() instead of doing same code again

    //TODO: weighted operations?
    public final static int BLACK = RGB_SPACE.getRGB(0.0, 0.0, 0.0);
    public final static int WHITE = RGB_SPACE.getRGB(1.0, 1.0, 1.0);

    //OPERATIONS
    public static Color add(Color c1, Color c2, ColorSpace colorSpace) {
        return binaryOp(c1, c2, colorSpace, Double::sum);
    }

    public static Color sub(Color c1, Color c2, ColorSpace colorSpace) {
        return binaryOp(c1, c2, colorSpace, (v1, v2) -> v1 - v2);
    }

    public static Color diff(Color c1, Color c2, ColorSpace colorSpace) {
        return binaryOp(c1, c2, colorSpace, (v1, v2) -> Math.abs(v1 - v2));
    }

    public static Color mult(Color c1, Color c2, ColorSpace colorSpace) {
        return binaryOp(c1, c2, colorSpace, (v1, v2) -> v1 * v2);
    }

    public static Color div(Color c1, Color c2, ColorSpace colorSpace) {
        return binaryOp(c1, c2, colorSpace, (v1, v2) -> {
            if(v2 == 0) {
                return v1 > 0 ? Double.MAX_VALUE : Double.MIN_VALUE;
            } else {
                return v1 / v2;
            }
        });
    }

    public static Color div(Color c1, double value, ColorSpace colorSpace) {
        if(value == 0) throw new IllegalArgumentException();
        double[] components = colorSpace.getComponents(c1);
        double[] newComponents = new double[components.length];

        for(int i = 0; i < components.length; i++) {
            newComponents[i] = components[i] / value;
        }

        return colorSpace.getColor(newComponents);
    }

    public static Color pow(Color c1, Color c2, ColorSpace colorSpace) {
        return binaryOp(c1, c2, colorSpace, Math::pow);
    }

    public static Color lerp(Color c1, Color c2, double amount, ColorSpace colorSpace) {
        return binaryOp(c1, c2, colorSpace, (v1, v2) -> MathUtils.map(amount, 0, 1, v1, v2));
    }

    public static Color binaryOp(Color c1, Color c2, ColorSpace colorSpace, BinaryOperator<Double> operator) {
        double[] components1 = colorSpace.getComponents(c1);
        double[] components2 = colorSpace.getComponents(c2);
        double[] result = binaryArrayOp(components1, components2, operator);

        return colorSpace.getColor(result);
    }

    public static double[] binaryArrayOp(double[] arr1, double[] arr2, BinaryOperator<Double> operator) {
        if(arr1.length != arr2.length) throw new IllegalArgumentException();

        double[] res = new double[arr1.length];

        for(int i = 0; i < arr1.length; i++) {
            res[i] = operator.apply(arr1[i], arr2[i]);
        }

        return res;
    }


    //MODIFY
    public static AlphaColor withAlpha(Color color, double alpha) {
        if(color instanceof HSB) {
            return new HSBColor(((HSB) color).getHue(), ((HSB) color).getSaturation(), ((HSB) color).getBrightness(), alpha);
        } else {
            return new RGBColor(color.toRGB(), alpha);
        }
    }

    public static int withAlpha(int rgb, double alpha) {
        //return rgb | ((int)(alpha * 255) & 255) << 24;
        return new RGBColor(rgb, alpha).toRGB();
    }

    public static int blend(int c1, int c2) {
        double alpha1 = alpha(c1);
        double alpha2 = alpha(c2);
        double totalAlpha = alpha1 + alpha2;

        double weight0 = alpha1 / totalAlpha;
        double weight1 = alpha2 / totalAlpha;

        double r = weight0 * red(c1) + weight1 * red(c2);
        double g = weight0 * green(c1) + weight1 * green(c2);
        double b = weight0 * blue(c1) + weight1 * blue(c2);
        double a = Math.max(alpha1, alpha2);

        return new RGBColor(r, g, b, a).toRGB();
    }

    //GET
    public static Color fromRGB(int rgb) {
        return new RGBColor(rgb);
    }
    public static List<Color> fromRGBs(int... rgbs) {
        List<Color> colors = new ArrayList<>(rgbs.length);
        for(int rgb : rgbs) {
            colors.add(fromRGB(rgb));
        }
        return colors;
    }

    public static List<Color> fromRGBs(List<Integer> rgbs) {
        List<Color> colors = new ArrayList<>(rgbs.size());
        for(int rgb : rgbs) {
            colors.add(fromRGB(rgb));
        }
        return colors;
    }

    public static Color fromHSB(double h, double s, double b) {
        return new HSBColor(h, s, b);
    }
    public static Color fromHSB(double h, double s, double b, double alpha) {
        return new HSBColor(h, s, b, alpha);
    }


    //RGB VALUES
    public static double alpha(Color Color) {
        return alpha(Color.toRGB());
    }
    public static double alpha(int rgb) {
        return (float) (rgb >> 24 & 255) / 255.0;
    }

    public static double red(Color Color) {
        return red(Color.toRGB());
    }
    public static double red(int rgb) {
        return (float) (rgb >> 16 & 255) / 255.0;
    }

    public static double green(Color Color) {
        return (float)(Color.toRGB() >> 8 & 255) / 255.0;
    }
    public static double green(int rgb) {
        return (float)(rgb >> 8 & 255) / 255.0;
    }

    public static double blue(Color Color) {
        return blue(Color.toRGB());
    }
    public static double blue(int rgb) {
        return (float)(rgb & 255) / 255.0;
    }

    //HSB VALUE
    private static Integer cacheHSBKey = null;
    private static float[] cacheHSBValue = new float[3];

    private static void calculateHSB(int rgb) {
        if(cacheHSBKey == null || rgb != cacheHSBKey) {
            java.awt.Color.RGBtoHSB(rgb >> 16 & 255, rgb >> 8 & 255, rgb & 255, cacheHSBValue);
            cacheHSBKey = rgb;
        }
    }

    public static double hue(Color color) {
        if(color instanceof HSBColor) {
            return ((HSBColor) color).getHue();
        }
        return hue(color.toRGB());
    }
    public static double hue(int rgb) {
        calculateHSB(rgb);
        return cacheHSBValue[0];
    }

    public static double saturation(Color color) {
        if(color instanceof HSBColor) {
            return ((HSBColor) color).getSaturation();
        }
        return saturation(color.toRGB());
    }
    public static double saturation(int rgb) {
        calculateHSB(rgb);
        return cacheHSBValue[1];
    }

    public static double brightness(Color color) {
        if(color instanceof HSBColor) {
            return ((HSBColor) color).getBrightness();
        }

        return brightness(color.toRGB());
    }

    public static double brightness(int rgb) {
        calculateHSB(rgb);
        return cacheHSBValue[2];
    }

    //RANDOM
    public static Color random() {
        return random(RGB_SPACE, new double[]{0, 0, 0}, new double[]{1.0, 1.0, 1.0});
    }

    public static Color random(ColorSpace colorSpace, double[] componentMin, double[] componentMax) {
        if(colorSpace.getNumberOfComponents() != componentMin.length || colorSpace.getNumberOfComponents() != componentMax.length) {
            throw new IllegalArgumentException();
        }

        double[] components = new double[colorSpace.getNumberOfComponents()];
        for(int i = 0; i < colorSpace.getNumberOfComponents(); i++) {
            components[i] = MathUtils.random(componentMin[i], componentMax[i]);
        }

        return colorSpace.getColor(components);
    }

    public static Color parseHex(String hex) {
        if(hex.startsWith("#")) {
            hex = hex.substring(1);
        }

        int rgb = Colors.withAlpha(Integer.parseInt(hex, 16), 1);
        return new RGBColor(rgb);
    }

    // VARY COLOR
    public static Color varyComponent(Color color, ColorSpace colorSpace, int componentIndex, double value, MathUtils.BinaryOpMode binaryOpMode) {
        return varyComponent(color, colorSpace, componentIndex, value, MathUtils.getBinaryOperator(binaryOpMode));
    }

    public static Color varyComponent(Color color, ColorSpace colorSpace, int componentIndex, double value, BinaryOperator<Double> operator) {
        double[] components = colorSpace.getComponents(color);
        components[componentIndex] = operator.apply(components[componentIndex], value);
        return colorSpace.getColor(components);
    }

    public static Color setComponent(Color color, ColorSpace colorSpace, int componentIndex, double value) {
        return varyComponent(color, colorSpace, componentIndex, value, MathUtils.getBinaryOperator(MathUtils.BinaryOpMode.SECOND));
    }

    public static Color variation(Color color, double[] variation, ColorSpace colorSpace, MathUtils.BinaryOpMode binaryOpMode) {
        BinaryOperator<Double> operator = MathUtils.getBinaryOperator(binaryOpMode);
        return variation(color, variation, colorSpace, operator);
    }

    public static Color variation(Color color, double[] variation, ColorSpace colorSpace, BinaryOperator<Double> operator) {
        if(variation.length != colorSpace.getNumberOfComponents()) throw new IllegalArgumentException();
        double[] components = colorSpace.getComponents(color);
        for(int i = 0; i < colorSpace.getNumberOfComponents(); i++) {
            components[i] = operator.apply(components[i], variation[i]);
        }
        return colorSpace.getColor(components);
    }

    public enum RandomMode {
        LINEAR,
        GAUSSIAN
    }
    public static Color randomVariation(Color color, double[] variation, ColorSpace colorSpace, RandomMode mode, MathUtils.BinaryOpMode binaryOpMode) {
        return randomVariation(color, variation, colorSpace, mode, MathUtils.getBinaryOperator(binaryOpMode));
    }

    public static Color randomVariation(Color color, double[] variation, ColorSpace colorSpace, RandomMode mode, BinaryOperator<Double> operator) {
        double[] randomVariation = new double[variation.length];
        for(int i = 0; i < variation.length; i++) {
            double v;
            if(mode == RandomMode.LINEAR) {
                v = variation[i] * MathUtils.random(-1, 1);
            } else {
                v = MathUtils.randomGaussian(variation[i]);
            }
            randomVariation[i] = v;
        }
        return variation(color, randomVariation, colorSpace, operator);
    }

    // COMPARING
    public static Comparator<Color> colorComponentComparator(ColorSpace colorSpace, int component, boolean reversed) {
        return (c1, c2) -> {
            double cc1 = colorSpace.getComponents(c1)[component];
            double cc2 = colorSpace.getComponents(c2)[component];
            return (int) (255 * (reversed ? cc1 - cc2 : cc2 - cc1));
        };
    }

    public static Comparator<Integer> rgbComponentComparator(ColorSpace colorSpace, int component, boolean reversed) {
        return (c1, c2) -> {
            double cc1 = colorSpace.getComponents(c1)[component];
            double cc2 = colorSpace.getComponents(c2)[component];
            return (int) (255 * (reversed ? cc1 - cc2 : cc2 - cc1));
        };
    }
}
