import color.colors.rgb.RGBColor;
import processing.core.PApplet;
import processing.core.PGraphics;
import sketch.Sketch;
import sketch.organic.BushSketch;
import sketch.organic.CropSketch;
import sketch.organic.LeafSketch;
import util.geometry.Rectangle;

public class OrganicMain extends PApplet {
    private int screenSize = 1000;

    private int screenWidth = screenSize;
    private int screenHeight = screenSize;

    private double renderQuality = 1.0;
    private double saveQuality = renderQuality * 2;

    private int sketchWidth = (int) (screenWidth * renderQuality);
    private int sketchHeight = (int) (screenHeight * renderQuality);

    private PGraphics canvas;
    private Sketch sketch;

    @Override
    public void settings() {
        size(screenWidth, screenHeight);
    }


    @Override
    public void setup() {
        Rectangle bounds = new Rectangle(sketchWidth, sketchHeight);
        sketch =
                //new BushSketch(bounds);
                //new CropSketch(bounds);
                new LeafSketch(bounds);

        canvas = createGraphics(sketchWidth, sketchHeight);

        canvas.beginDraw();
        canvas.background(0);
        sketch.draw(canvas);
        canvas.endDraw();
    }

    @Override
    public void draw() {
        image(canvas, 0, 0, screenWidth, screenHeight);
    }

    public static void main(String[] args) {
        PApplet.main("OrganicMain");
    }

    private void reset() {
        setup();
    }


    public void keyPressed() {
        switch(key) {
            case 'r': reset(); break;
            case 's': {
                /*PGraphics render = createGraphics((int)(screenWidth * saveQuality), (int)(screenHeight * saveQuality));

                Rectangle oldBounds = sketch.getBounds();
                sketch.setBounds(new Rectangle(render.width, render.height));
                //sketch.createApplier(blur * saveQuality);

                render.beginDraw();
                sketch.draw(render);
                render.endDraw();

                String name ="photos/texture/" + sketch.getClass().getName() + "-" + System.currentTimeMillis() + ".png";
                render.save(name);

                System.out.println("Saved \"" + name + "\"");

                sketch.setBounds(oldBounds);*/
                String name ="output/organic/" + sketch.getClass().getName() + "-" + System.currentTimeMillis() + ".png";
                canvas.save(name);
                System.out.println("Saved \"" + name + "\"");
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
