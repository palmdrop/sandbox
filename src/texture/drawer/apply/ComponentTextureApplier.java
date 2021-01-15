package texture.drawer.apply;

import color.colors.Color;
import color.space.ColorSpace;
import texture.Texture;
import util.geometry.Rectangle;
import util.math.MathUtils;
import util.vector.Vector;

public class ComponentTextureApplier extends AbstractTextureApplier {
    private final ColorSpace colorSpace;

    public ComponentTextureApplier(Texture texture, ColorSpace colorSpace, Rectangle bounds, double blur) {
        super(texture, bounds, blur);
        if(colorSpace.getNumberOfComponents() != texture.getNumberOfComponents()) throw new IllegalArgumentException();
        this.colorSpace = colorSpace;
    }

    @Override
    protected int getColor(Vector p, int currentColor) {
        double[] colorComponents = colorSpace.getComponents(currentColor);
        double[] textureComponents = texture.getComponents(p);

        for(int i = 0; i < colorComponents.length; i++) {
            colorComponents[i] *= textureComponents[i];
        }

        return colorSpace.getColor(colorComponents).toRGB();
    }
}
