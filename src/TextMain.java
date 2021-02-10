import color.colors.Colors;
import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PImage;
import render.Drawer;
import render.heightMap.FadingHeightMapDrawer;
import sampling.GraphicsHeightMap;
import sampling.GraphicsSampler;
import sampling.Sampler;
import sampling.heightMap.HeightMaps;
import sketch.Sketch;
import sketch.text.TextGridSketch;
import util.geometry.Rectangle;

public class TextMain extends PApplet {
    // Sketch settings
    private int windowWidth = 1000;
    private int windowHeight = 1000;

    private double sketchMultiplier = 10.0;
    private int sketchWidth  = (int) (windowWidth * sketchMultiplier);
    private int sketchHeight = (int) (windowHeight * sketchMultiplier);

    private Rectangle bounds;
    private Sketch sketch;
    private PGraphics canvas;

    public void settings() {
        size(windowWidth, windowHeight);
        smooth(4);
    }

    public void setup() {
        // Set screen
        bounds = new Rectangle(sketchWidth, sketchHeight);

        sketch =
                new TextGridSketch(this, bounds, sketchMultiplier);

        // Create canvas
        canvas = createGraphics((int)bounds.getWidth(), (int)bounds.getHeight());

        canvas.beginDraw();
        sketch.draw(canvas);
        canvas.endDraw();
    }



    public void draw() {
        PImage toRender = canvas.get();
        toRender.resize(windowWidth, windowHeight);
        image(toRender, 0, 0);
    }

    public static void main(String[] args) {
        PApplet.main("TextMain");
    }

    private void reset() {
        setup();
    }


    public void mousePressed() {
        String name = "output/text/" + System.currentTimeMillis() + ".png";
        canvas.save(name);
        System.out.println("saved " + name);
    }

    public void keyPressed() {
        switch(key) {
            case 'r': {
                reset();
            } break;
            case 's': {
                String name = "output/text/" + System.currentTimeMillis() + ".png";
                canvas.save(name);
                System.out.println("saved " + name);
            } break;
        }
    }
}
