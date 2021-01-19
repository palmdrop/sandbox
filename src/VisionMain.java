import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PImage;
import sketch.Sketch;
import sketch.vision.MisinterpretSketch;
import util.geometry.Rectangle;

public class VisionMain extends PApplet {
    // Sketch settings
    private int desiredSize = 1000;

    private int windowWidth;
    private int windowHeight;

    private Sketch sketch;
    private PGraphics canvas;

    public void settings() {
        size(10, 10);
        smooth(4);
    }

    public void setup() {
        // Create sketch
        sketch =
                new MisinterpretSketch(this);

        // Set screen
        Rectangle bounds = sketch.getBounds();

        // Calculate dimensions
        double imageDivider = bounds.getWidth() / desiredSize;
        windowWidth = desiredSize;
        windowHeight = (int) (bounds.getHeight() / imageDivider);
        surface.setSize(windowWidth, windowHeight);

        // Create canvas
        canvas = createGraphics((int)bounds.getWidth(), (int)bounds.getHeight());

        sketch.draw(canvas);
    }


    PImage toDraw = null;

    public void draw() {
        if(toDraw == null) {
            toDraw = canvas.copy();
            toDraw.resize(windowWidth, windowHeight);
        }

        image(toDraw, 0, 0);
    }

    public static void main(String[] args) {
        PApplet.main("VisionMain");
    }

    private void reset() {
        sketch.draw(canvas);
        toDraw = null;
    }


    public void mousePressed() {
        //reset();
        String name = "output/vision/" + System.currentTimeMillis() + ".png";
        canvas.save(name);
        System.out.println("saved " + name);

    }

    public void keyPressed() {
        switch(key) {
            case 'r': {
                reset();
            } break;
            case 's': {
                String name = "output/vision/" + System.currentTimeMillis() + ".png";
                canvas.save(name);
                System.out.println("saved " + name);
            } break;
        }
    }
}
