package sketch.genuary;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PGraphics;
import render.AbstractDrawer;
import sketch.Sketch;
import sketch.texture.PatternStudySketch;
import util.geometry.Rectangle;
import util.math.MathUtils;

public class Gen1 extends AbstractDrawer implements Sketch {
    private final PApplet p;

    public Gen1(PApplet p, Rectangle bounds) {
        super(bounds);
        this.p = p;
        p.noiseSeed(System.currentTimeMillis());
    }

    @Override
    public PGraphics draw(PGraphics canvas, double frequency) {
        canvas.beginDraw();

        float size = 70;
        int iterations = 30;
        frequency = 0.005f;
        float threshold = 0.31f;
        int draws = 100;

        float hStart = p.random(255);
        float hEnd = hStart + p.random(-255, 255f);

        canvas.colorMode(PConstants.HSB);
        canvas.background(hStart, 20, 15);

        for(int d = 0; d < draws; d++) {
            p.noiseSeed(System.currentTimeMillis() * d);

            float alpha = PApplet.map(d, 0, draws, 20, 100);
            float bri = PApplet.map(d, 0, draws, 200, 10);

            for (int i = 0; i < iterations; i++) {
                float n = p.noise((float) (i * frequency));
                if (n > threshold) {
                    n = PApplet.abs(1.0f - n);
                }

                float x1 = PApplet.map(n, 0, 1, 0, canvas.width);
                float y2 = canvas.height;

                canvas.fill(PApplet.map((float) Math.random(), 0, 1, hStart, hEnd), 50, bri);
                canvas.stroke(0, 0, 0, alpha);
                canvas.strokeWeight(5);

                for (int j = 0; j < iterations; j++) {
                    float n2 = p.noise((float) (d + i * frequency), (float) (j * frequency));

                    float x = PApplet.map(n2, 0, 1, x1, x1);
                    float y = PApplet.map(n2, 0, 1, 0, y2);

                    canvas.circle(x, y, (float) (size * Math.random()));
                }
            }
        }

        canvas.endDraw();
        return canvas;
    }
}
