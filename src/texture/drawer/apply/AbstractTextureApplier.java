package texture.drawer.apply;

import processing.core.PConstants;
import processing.core.PGraphics;
import render.AbstractDrawer;
import sampling.GraphicsSampler;
import sampling.Sampler;
import texture.Texture;
import util.geometry.Rectangle;
import util.math.MathUtils;
import util.vector.Vector;

import java.util.Objects;

public abstract class AbstractTextureApplier extends AbstractDrawer implements TextureApplier {
    protected final Texture texture;
    protected final double blur;
    protected final Sampler<Integer> sourceElement;
    protected double amount;

    public AbstractTextureApplier(Texture texture, Rectangle bounds, double blur) {
        this(texture, null, bounds, blur);
    }

    public AbstractTextureApplier(Texture texture, Sampler<Integer> sourceElement, Rectangle bounds, double blur) {
        super(bounds);
        this.sourceElement = sourceElement;
        this.texture = texture;
        this.blur = blur;
        amount = 1.0;
    }

    protected abstract int getColor(Vector p, int currentColor);

    @Override
    public PGraphics draw(PGraphics canvas, double frequency) {
        Sampler<Integer> sourceElement =
                Objects.requireNonNullElseGet(this.sourceElement, () -> new GraphicsSampler(canvas, GraphicsSampler.WrapMode.BACKGROUND));

        canvas.loadPixels();
        int maxX = (int) Math.min(bounds.getX() + bounds.getWidth(), canvas.width);
        int maxY = (int) Math.min(bounds.getY() + bounds.getHeight(), canvas.height);

        for(int x = (int) bounds.getX(); x < maxX; x++) {
            for(int y = (int) bounds.getY(); y < maxY; y++) {
                double dx = MathUtils.map(x, bounds.getX(), bounds.getX() + bounds.getWidth(), 0, 1);
                double dy = MathUtils.map(y, bounds.getY(), bounds.getY() + bounds.getHeight(), 0, 1);

                Vector p = new Vector(dx * frequency, dy * frequency);

                int currentColor = sourceElement.get(new Vector(x, y));
                int color = getColor(p, currentColor);

                color = canvas.lerpColor(currentColor, color, (float) amount); //TODO: good way to determine amount?

                canvas.pixels[x + y * canvas.width] = color;
            }
        }
        canvas.updatePixels();

        if(blur > 0.0) canvas.filter(PConstants.BLUR, (float) blur);
        return null;
    }

    @Override
    public void setAmount(double amount) {
        this.amount = amount;
    }
}
