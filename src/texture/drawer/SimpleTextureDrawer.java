package texture.drawer;

import color.colors.Color;
import color.colors.Colors;
import render.AbstractPixelDrawer;
import texture.Texture;
import util.geometry.Rectangle;
import util.vector.Vector;

public class SimpleTextureDrawer extends AbstractPixelDrawer {
    private final Texture texture;

    public SimpleTextureDrawer(Texture texture, Rectangle bounds) {
        super(bounds);
        this.texture = texture;
    }

    @Override
    protected int getColor(Vector p) {
        double tv = texture.get(p);
        Color color = Colors.RGB_SPACE.getColor(tv, tv, tv);
        return color.toRGB();
    }
}
