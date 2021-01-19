import nebula.NebulaSettings;
import nebula.designSettings.HighwaySettings;
import nebula.designSettings.toExplore.ShapeSettings;
import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PImage;
import render.Drawer;

public class NebulaMain extends PApplet {
    // Sketch settings
    private int screenWidth = 1000;
    private int screenHeight = screenWidth;

    private float viewQuality = 1;
    private float renderQuality = 3;

    private int sketchWidth = (int) (screenWidth * viewQuality);
    private int sketchHeight = (int) (screenHeight * viewQuality);

    //private PGraphics baseLayer;
    private PGraphics canvas;

    // Modded noise
    private Drawer drawer;

    public void settings() {
        size(screenWidth, screenHeight);
        smooth(4);
    }

    public void setup() {
        canvas = createGraphics(sketchWidth, sketchHeight);
        NebulaSettings settings =
                new HighwaySettings(this);
                //new TestSettings(this);
                //new ShapeSettings(this, sketchWidth, sketchHeight);
                //new GemSettings(this);
                //new CombinedSettings(-sketchWidth/2, -sketchHeight/2, sketchWidth, sketchHeight);
                //new ExperimentalSettings(this);
        drawer = settings.getDrawer();

        canvas.beginDraw();

        drawer.draw(canvas, 1 / viewQuality);

        canvas.endDraw();
    }

    public void draw() {
        if(viewQuality != 1) {
            PImage img = canvas.get();
            img.resize(screenWidth, screenHeight);
            image(img, 0, 0);
        } else {
            image(canvas, 0, 0);
        }
    }

    public static void main(String[] args) {
        PApplet.main("NebulaMain");
    }

    private void reset() {
        setup();
    }

    public void mousePressed() {
        reset();
    }

    public void keyPressed() {
        switch(key) {
            case 'r': {
                reset();
            } break;
            case 's': {
                String name = "output/nebula/" + System.currentTimeMillis() + ".png";

                PGraphics render = createGraphics((int)(screenWidth * renderQuality), (int)(screenHeight * renderQuality));
                drawer.draw(render, 1 / renderQuality);

                render.save(name);

                System.out.println("saved " + name);
            } break;
        }
    }
}
