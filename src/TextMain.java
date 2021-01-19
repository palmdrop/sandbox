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

    private double sketchMultiplier = 4.0;
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
                //new PatternSketch(this);
                new TextGridSketch(this, bounds, sketchMultiplier);

        // Create canvas
        canvas = createGraphics((int)bounds.getWidth(), (int)bounds.getHeight());

        GraphicsSampler gSampler =
                new GraphicsSampler(this.loadImage(
                        "sourceImages/collected/liminal1.jpg"
                            //"sourceImages/face1.jpg"
                ), GraphicsSampler.WrapMode.MIRROR_WRAP);

        GraphicsHeightMap ghm = new GraphicsHeightMap(this.loadImage(
                "sourceImages/collected/liminal1.jpg"
                    //"sourceImages/face1.jpg"
        ), GraphicsSampler.WrapMode.MIRROR_WRAP, Colors::brightness);

        Sampler<Double> sampler =
                HeightMaps.stretch(ghm, (double)ghm.getImage().width / canvas.width, (double)ghm.getImage().width / canvas.width);


        Drawer drawer =
                new FadingHeightMapDrawer(sampler, 0, 0);
                //new SampleDrawer(sampler, new Vector());
        //TODO either scale fading heightmap drawer or scale the graphics heightmap sampler to match range of FadingHeightMapDrawer!!!

        canvas.beginDraw();
        //drawer.draw(canvas);

        sketch.draw(canvas);
        canvas.endDraw();
    }



    public void draw() {
        //sketch.draw(canvas);

        /*Sampler<Integer> sampler = new GraphicsSampler(canvas);

        DomainWarp<Integer> warped = new SimpleDomainWarp<>(sampler);
        warped.domainWarp(
                HeightMaps.constant(1.0),
                            p -> Colors.brightness(sampler.get(p.getX(),p.getY())),
                0, 10
                );*/

        //SampleDrawer drawer = new SampleDrawer(warped, canvas.width, canvas.height, new Vector());

        //drawer.draw(this.g);
        //image(g, 0, 0);
        PImage toRender = canvas.get();
        toRender.resize(windowWidth, windowHeight);

        //image(canvas, 0, 0, windowWidth, windowHeight);
        image(toRender, 0, 0);
    }

    public static void main(String[] args) {
        PApplet.main("TextMain");
    }

    private void reset() {
        setup();
        //sketch.draw(canvas);
    }


    public void mousePressed() {
        //reset();
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
