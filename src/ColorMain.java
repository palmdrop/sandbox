import color.colors.rgb.RGBColor;
import processing.core.PApplet;
import processing.core.PGraphics;
import sketch.Sketch;
import sketch.color.TestSketch;
import util.geometry.Rectangle;

public class ColorMain extends PApplet {
    private int screenSize = 1000;

    private int screenWidth = screenSize;
    private int screenHeight = screenWidth;

    private double renderQuality = 2.0;
    private double saveQuality = renderQuality * 1;

    private int sketchWidth = (int) (screenWidth * renderQuality);
    private int sketchHeight = (int) (screenHeight * renderQuality);

    private Sketch sketch;
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
                //new FadeSketch(bounds);
                //new BreathSketch(bounds, 5);
                new TestSketch(bounds, this);
                //new CompositionSketch(bounds);

        canvas.beginDraw();
        sketch.draw(canvas);
        canvas.endDraw();
    }

    @Override
    public void draw() {
        background(0);
        image(canvas, 0, 0, screenWidth, screenHeight);
    }

    public static void main(String[] args) {
        PApplet.main("ColorMain");
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
                sketch.draw(render);
                render.endDraw();

                String name ="output/color/" + sketch.getClass().getName() + "-" + System.currentTimeMillis() + ".png";
                render.save(name);

                System.out.println("Saved \"" + name + "\"");

                sketch.setBounds(oldBounds);
            }
            //case 's': savePalette(palette);
        }
    }

    public void mousePressed() {
        this.g.loadPixels();
        int rgb = this.g.pixels[mouseX + mouseY * width];
        System.out.println(new RGBColor(rgb).toHex());
    }

}
