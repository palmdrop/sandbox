package render.heightMap;

import color.colors.Colors;
import processing.core.PApplet;
import processing.core.PGraphics;
import render.Drawer;
import sampling.Sampler;
import util.geometry.Rectangle;
import util.math.MathUtils;

public class FadingHeightMapDrawer implements Drawer {
    private final int minColor, maxColor;
    private final double dither;
    private final int colorMode;
    private final double blur;

    private final Sampler<Double> heightMap;
    private boolean superSampling = false;

    public FadingHeightMapDrawer(Sampler<Double> heightMap, double dither, double blur) {
        this(heightMap, Colors.BLACK, Colors.WHITE, dither, blur, PApplet.RGB);
    }

    public FadingHeightMapDrawer(Sampler<Double> heightMap, int minColor, int maxColor, double dither, double blur, int colorMode) {
        this.minColor = minColor;
        this.maxColor = maxColor;
        this.dither = dither;
        this.blur = blur;
        this.colorMode = colorMode;
        this.heightMap = heightMap;
    }

    @Override
    public PGraphics draw(PGraphics canvas, double frequency) {
        double xOffset = 0;//-frequency * canvas.width / 2.0;
        double yOffset = 0;//-frequency * canvas.height / 2.0;

        canvas.beginDraw();
        canvas.colorMode(colorMode);
        canvas.loadPixels();

        for(int x = 0; x < canvas.width; x++) {
            for(int y = 0; y < canvas.height; y++) {
                double n = 0;
                double nx = x * frequency + xOffset;
                double ny = y * frequency + yOffset;

                if(superSampling) {
                    int samples = 4;
                    double offset = 0.06;
                    for (int i = 0; i < samples; i++) {
                        double sx = 0, sy = 0;
                        switch (i) {
                            case 0:
                                sx = 0.25 - offset;
                                sy = 0.25 + offset;
                                break;
                            case 1:
                                sx = 0.75 - offset;
                                sy = 0.25 - offset;
                                break;
                            case 2:
                                sx = 0.75 + offset;
                                sy = 0.75 - offset;
                                break;
                            case 3:
                                sx = 0.25 + offset;
                                sy = 0.75 + offset;
                                break;
                        }
                        n += heightMap.get(nx + sx * frequency, ny + sy * frequency);
                    }
                    n /= samples;
                } else {
                    n += heightMap.get(nx, ny);
                }

                if(dither > 0) n += dither * ((MathUtils.random(-1, 1) + MathUtils.random(-1, 1)) / 2 );

                n = MathUtils.limit(n, 0.0, 1.0);

                int color = canvas.lerpColor(minColor, maxColor, (float) n);
                canvas.pixels[x + y * canvas.width] = color;
            }
            System.out.println(100 * x / canvas.width + "%");
        }
        canvas.updatePixels();

        canvas.filter(PApplet.BLUR, (float) blur);

        canvas.endDraw();
        return canvas;
    }

    public void setSuperSampling(boolean superSampling) {
        this.superSampling = superSampling;
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
