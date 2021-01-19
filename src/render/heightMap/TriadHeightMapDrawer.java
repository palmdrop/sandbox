package render.heightMap;

import processing.core.PGraphics;
import render.AbstractPixelDrawer;
import render.Drawer;
import sampling.Sampler;
import util.geometry.Rectangle;
import util.math.MathUtils;
import util.vector.Vector;

public class TriadHeightMapDrawer extends AbstractPixelDrawer implements Drawer {
    private final Sampler<Double> fade;
    private final Sampler<Double> brightness;

    private final int bright1, bright2;
    private final int dark;

    private final double dither;
    private final double blur;
    private final int colorMode;

    public TriadHeightMapDrawer(Sampler<Double> fade, Sampler<Double> brightness, int bright1, int bright2, int dark, double dither, double blur, int colorMode) {
        super(null, new Vector(), -1, -1, blur);
        this.fade = fade;
        this.brightness = brightness;
        this.bright1 = bright1;
        this.bright2 = bright2;
        this.dark = dark;
        this.dither = dither;
        this.blur = blur;
        this.colorMode = colorMode;
    }

    //@Override
    protected int getColor(Vector p) {
        double f = fade.get(p);
        double b = brightness.get(p);

        if(dither > 0) {
            f += dither * ((MathUtils.random(-1, 1) + MathUtils.random(-1, 1)) / 2);
            b += dither * ((MathUtils.random(-1, 1) + MathUtils.random(-1, 1)) / 2);
        }

        f = MathUtils.limit(f, 0.0, 1.0);
        b = MathUtils.limit(b, 0.0, 1.0);

        int fadeColor = PGraphics.lerpColor(bright1, bright2, (float)f, colorMode);
        return PGraphics.lerpColor(dark, fadeColor, (float)b, colorMode);
    }

    @Override
    public PGraphics draw(PGraphics canvas, double frequency) {
        bounds = new Rectangle(0, 0, canvas.width, canvas.height);
        offset = new Vector(-frequency * canvas.width / 2.0, -frequency * canvas.width / 2.0);
        xMultiplier = canvas.width;
        yMultiplier = canvas.height;
        return super.draw(canvas, frequency);
    }
}
