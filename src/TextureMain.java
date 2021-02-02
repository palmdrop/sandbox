import color.colors.rgb.RGBColor;
import processing.core.PApplet;
import processing.core.PGraphics;
import sketch.texture.OrganicPatternSketch;
import sketch.texture.PatternStudySketch;
import util.geometry.Rectangle;

public class TextureMain extends PApplet {
    private int screenSize = 1000;

    private int screenWidth = screenSize;
    private int screenHeight = screenWidth;

    private double renderQuality = 1.0;
    private double saveQuality = renderQuality * 3;

    private int sketchWidth = (int) (screenWidth * renderQuality);
    private int sketchHeight = (int) (screenHeight * renderQuality);

    private
        OrganicPatternSketch sketch;
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
                //new CMYKSketch(bounds);
                //new ContourSketch(bounds);
                //new ColorGrainAndGlitchSketch(bounds);
                //new RecursiveTextureSketch(bounds);
                //new TestTextureSketch(bounds);
                //new PatternStudySketch(bounds);
                new OrganicPatternSketch(bounds);


        //sketch.createApplier(blur);

        canvas.beginDraw();
        //canvas.background(255);
        sketch.draw(canvas);
        canvas.endDraw();

        System.out.println(String.format("%x", color(0, 0, 0, 255)));
    }

    @Override
    public void draw() {
        background(0, 0, 0);
        image(canvas, 0, 0, screenWidth, screenHeight);
    }

    public static void main(String[] args) {
        PApplet.main("TextureMain");
    }

    private void reset() {
        setup();
    }


    public void keyPressed() {
        switch(key) {
            case 'r': reset(); break;
            case 'q': {
                String name ="output/commission/" + System.currentTimeMillis() + ".png";
                canvas.save(name);
                System.out.println("saved " + name);
                break;
            }
            case 's': {
                PGraphics render = createGraphics((int)(screenWidth * saveQuality), (int)(screenHeight * saveQuality));

                Rectangle oldBounds = sketch.getBounds();
                sketch.setBounds(new Rectangle(render.width, render.height));
                //sketch.createApplier(blur * saveQuality);

                render.beginDraw();
                sketch.draw(render, 1.0/saveQuality, true);
                render.endDraw();

                String name ="output/texture/" + System.currentTimeMillis() + ".png";
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
