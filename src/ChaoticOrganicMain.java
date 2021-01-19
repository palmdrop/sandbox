import color.colors.rgb.RGBColor;
import processing.core.PApplet;
import processing.core.PGraphics;
import sketch.Sketch;
import sketch.organic.CthulhuSketch;
import util.geometry.Rectangle;

public class ChaoticOrganicMain extends PApplet {
    private static int screenSize = 1000;

    private static int screenWidth = screenSize;
    private static int screenHeight = screenWidth;

    private static double renderQuality = 3.0;

    private static int sketchWidth = (int) (screenWidth * renderQuality);
    private static int sketchHeight = (int) (screenHeight * renderQuality);

    Rectangle bounds = new Rectangle(sketchWidth, sketchHeight);
    private PGraphics canvas;
    private Sketch sketch;

    @Override
    public void settings() {
        size(screenWidth, screenHeight);
        smooth(4);
    }

    @Override
    public void setup() {

        canvas = createGraphics(sketchWidth, sketchHeight);
        canvas.beginDraw();
        canvas.background(0);
        canvas.endDraw();

        sketch =
                //new SegmentDrawSketch(bounds, this);
                new CthulhuSketch(bounds, 1 / renderQuality);
                //new FrequenciesSketch(bounds, this);
        sketch.draw(canvas, renderQuality);
    }

    @Override
    public void draw() {

        for(int i = 0; i < 10; i++) {
            sketch.draw(canvas, renderQuality);
        }

        image(canvas, 0, 0, screenWidth, screenHeight);
    }


    public static void main(String[] args) {
        PApplet.main("ChaoticOrganicMain");
    }

    private void reset() {
        setup();
    }


    public void keyPressed() {
        switch(key) {
            case 'r': reset(); break;
            case 's': {
                PGraphics toSave = createGraphics(sketchWidth, sketchHeight);
                toSave.beginDraw();
                toSave.background(canvas);

                toSave.save("output/test/" + System.nanoTime() + ".png");
                System.out.println("Saved!");
            }
        }
    }

    public void mousePressed() {
        this.g.loadPixels();
        int rgb = this.g.pixels[mouseX + mouseY * width];
        System.out.println(new RGBColor(rgb).toHex());
    }

}