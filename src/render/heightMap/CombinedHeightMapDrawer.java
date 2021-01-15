package render.heightMap;

import render.Drawer;
import sampling.heightMap.HeightMap;
import sampling.heightMap.HeightMaps;
import processing.core.PApplet;
import processing.core.PGraphics;
import util.geometry.Rectangle;
import util.math.MathUtils;

public class CombinedHeightMapDrawer implements Drawer {
    public enum Mode {
        RGB,
        HSB
    };

    private final HeightMap hmA;
    private final HeightMap hmB;
    private final HeightMap hmC;
    private final HeightMap hmAlpha;

    private final Mode mode;

    private final double dither;
    private final double blur;

    public CombinedHeightMapDrawer(HeightMap hmA, HeightMap hmB, HeightMap hmC, Mode mode, double dither, double blur) {
        this(hmA, hmB, hmC, HeightMaps.constant(1.0), mode, dither, blur);
    }

    public CombinedHeightMapDrawer(HeightMap hmA, HeightMap hmB, HeightMap hmC, HeightMap hmAlpha, Mode mode, double dither, double blur) {
        this.hmA = hmA;
        this.hmB = hmB;
        this.hmC = hmC;
        this.hmAlpha = hmAlpha;
        this.mode = mode;

        this.dither = dither;
        this.blur = blur;
    }

    @Override
    public PGraphics draw(PGraphics canvas, double frequency) {
        double xOffset = -frequency * canvas.width / 2.0;
        double yOffset = -frequency * canvas.height / 2.0;

        canvas.beginDraw();
        canvas.colorMode(mode.equals(Mode.RGB) ? PApplet.RGB : PApplet.HSB);

        canvas.loadPixels();

        for(int x = 0; x < canvas.width; x++) {
            for(int y = 0; y < canvas.height; y++) {
                double dx = x * frequency + xOffset;
                double dy = y * frequency + yOffset;

                double a = dither(hmA.get(dx, dy));
                double b = dither(hmB.get(dx, dy));
                double c = dither(hmC.get(dx, dy));
                double alpha = hmAlpha.get(dx, dy);

                int color = canvas.color((float) (255 * a), (float) (255 * b), (float) (255 * c), (float) (255 * alpha));
                canvas.pixels[x + y * canvas.width] = color;
            }
            System.out.println(100 * x / canvas.width + "%");
        }

        canvas.updatePixels();
        canvas.filter(PApplet.BLUR, (float) blur);

        canvas.endDraw();
        return canvas;
    }

    private double dither(double v) {
        double d = (Math.random() + Math.random()) / 2;
        d = dither * MathUtils.map(d, 0, 1, -1, 1);
        return MathUtils.limit(v + d, 0, 1);
    }

    @Override
    public void setBounds(Rectangle bounds) {
        //TODO
    }

    @Override
    public Rectangle getBounds() {
        //TODO
        return null;
    }
}
