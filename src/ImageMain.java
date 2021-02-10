import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PImage;
import sketch.Sketch;
import sketch.image.BiowarpSketch;
import sketch.image.PatternSketch;
import util.geometry.Rectangle;

import java.awt.*;

public class ImageMain extends PApplet {
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
        sketch =
                //new GlitchSketch(this);
                //new CombinedSketch(this);
                //new PatternSketch(this);
                new BiowarpSketch(this);
                //new GridSketch(this, 2, 2);

        // Set screen
        Rectangle bounds = sketch.getBounds();

        desiredSize = (int) Math.min(desiredSize, sketch.getBounds().width);

        // Calculate dimensions
        double imageDivider = bounds.getWidth() / desiredSize;
        windowWidth = desiredSize;
        windowHeight = (int) (bounds.getHeight() / imageDivider);

        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();

        int sketchX = (int) ((screen.getWidth() - windowWidth) / 2);
        int sketchY = (int) ((screen.getHeight() - windowHeight) / 2);

        surface.setSize(windowWidth, windowHeight);
        surface.setLocation(sketchX, sketchY);

        // Create canvas
        canvas = createGraphics((int)bounds.getWidth(), (int)bounds.getHeight());

        canvas.beginDraw();
        sketch.draw(canvas);
        canvas.endDraw();
    }



    public void draw() {
        PImage toDraw = canvas.copy();
        toDraw.resize(windowWidth, windowHeight);

        image(toDraw, 0, 0);
    }

    public static void main(String[] args) {
        PApplet.main("ImageMain");
    }

    private void reset() {
        //setup();
        sketch.draw(canvas);
    }


    public void mousePressed() {
        //reset();
        String name = "output/image/" + System.currentTimeMillis() + ".png";
        canvas.save(name);
        System.out.println("saved " + name);

    }

    public void keyPressed() {
        switch(key) {
            case 'r': {
                reset();
            } break;
            case 's': {
                String name = "output/image/" + System.currentTimeMillis() + ".png";
                canvas.save(name);
                System.out.println("saved " + name);
            } break;
        }
    }
}
