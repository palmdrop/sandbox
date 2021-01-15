import color.colors.rgb.RGBColor;
import processing.core.PApplet;
import processing.core.PGraphics;
import sketch.Sketch;
import sketch.shape.ConstructivismSketch;
import sketch.texture.PatternStudySketch;
import util.geometry.Rectangle;

public class ConstructivismMain extends PApplet {
    private int screenSize = 1000;

    private int screenWidth = screenSize;
    private int screenHeight = screenWidth;

    private double renderQuality = 1.0;
    private double saveQuality = renderQuality * 3;

    private int sketchWidth = (int) (screenWidth * renderQuality);
    private int sketchHeight = (int) (screenHeight * renderQuality);

    private ConstructivismSketch sketch;
    private PGraphics canvas;

    @Override
    public void settings() {
        size(screenWidth, screenHeight);
    }

    @Override
    public void setup() {
        canvas = createGraphics(sketchWidth, sketchHeight);

        Rectangle bounds = new Rectangle(canvas.width, canvas.height);

        sketch =
                new ConstructivismSketch(bounds);

        canvas.beginDraw();

        canvas.background(255);

        sketch.draw(canvas);
        canvas.endDraw();
    }

    @Override
    public void draw() {
        background(0);
        image(canvas, 0, 0, screenWidth, screenHeight);
    }

    public static void main(String[] args) {
        PApplet.main("ConstructivismMain");
    }

    private void reset() {
        setup();
    }


    public void keyPressed() {
        switch(key) {
            case 'r': reset(); break;
            case 's': {
                PGraphics render = createGraphics((int)(screenWidth * saveQuality), (int)(screenHeight * saveQuality));

                Rectangle oldBounds = sketch.getBounds();
                sketch.setBounds(new Rectangle(render.width, render.height));

                render.beginDraw();
                sketch.draw(render, 1.0/saveQuality, true);
                render.endDraw();

                String name ="output/shapes/" + System.currentTimeMillis() + ".png";
                render.save(name);

                System.out.println("Saved \"" + name + "\"");

                sketch.setBounds(oldBounds);
            }
        }
    }
}
