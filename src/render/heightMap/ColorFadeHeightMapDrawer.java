package render.heightMap;

import color.colors.Color;
import color.fade.ColorFade;
import processing.core.PGraphics;
import render.Drawer;
import sampling.Sampler;
import util.geometry.Rectangle;


public class ColorFadeHeightMapDrawer implements Drawer {
    private final Sampler<Double> heightMap;
    private final ColorFade fade;

    public ColorFadeHeightMapDrawer(Sampler<Double> heightMap, ColorFade fade) {

        this.heightMap = heightMap;
        this.fade = fade;
    }

    @Override
    public PGraphics draw(PGraphics canvas, double frequency) {
        canvas.beginDraw();
        canvas.background(255);

        canvas.loadPixels();
        for(int x = 0; x < canvas.width; x++) {
            for(int y = 0; y < canvas.height; y++) {
                double nx = x * frequency;
                double ny = y * frequency;

                double n = heightMap.get(nx, ny);

                Color color = fade.get(n);
                canvas.pixels[x + y * canvas.width] = color.toRGB();
            }
            System.out.println(100 * x / canvas.width + "%");
        }
        canvas.updatePixels();
        canvas.endDraw();
        return canvas;
    }

    @Override
    public Rectangle getBounds() {
        return null;
    }

    @Override
    public void setBounds(Rectangle bounds) {
    }
}
