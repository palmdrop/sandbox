package color.space;

import color.colors.Color;

public abstract class AbstractColorSpace implements ColorSpace {
    protected interface ComponentsToColor {
        Color apply(double... components);
    }
    protected interface ColorToComponents {
        double[] apply(Color color);
    }

    private final int numberOfComponents;
    private final ComponentsToColor toColor;
    private final ColorToComponents toComponents;

    public AbstractColorSpace(int numberOfComponents, ComponentsToColor toColor, ColorToComponents toComponents) {
        this.numberOfComponents = numberOfComponents;
        this.toColor = toColor;
        this.toComponents = toComponents;
    }

    @Override
    public Color getColor(double... components) {
        return toColor.apply(components);
    }

    @Override
    public double[] getComponents(Color color) {
        return toComponents.apply(color);
    }

    @Override
    public int getNumberOfComponents() {
        return numberOfComponents;
    }
}
