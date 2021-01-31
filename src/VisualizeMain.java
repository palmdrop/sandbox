import processing.core.PApplet;
import processing.core.PGraphics;
import render.Drawer;
import render.heightMap.FadingHeightMapDrawer;
import sampling.countour.drawer.SimpleContourDrawer;
import sampling.heightMap.HeightMap;
import sampling.heightMap.HeightMaps;
import util.geometry.Rectangle;
import util.noise.ComplexFractalHeightMap;
import util.noise.FractalHeightMap;
import util.noise.generator.GNoise;
import util.vector.Vector;

public class VisualizeMain extends PApplet {
    private int screenSize = 1000;
    private int screenWidth = screenSize;
    private int screenHeight = screenWidth;

    private PGraphics canvas;

    @Override
    public void settings() {
        size(screenWidth, screenHeight);
    }

    @Override
    public void setup() {
        canvas = createGraphics(screenWidth, screenHeight);

        Rectangle bounds = new Rectangle(canvas.width, canvas.height);

        HeightMap hm =
                //new FractalHeightMap(0.005, 1.0, 2.0, 0.5, FractalHeightMap.Type.SIMPLEX, 8, System.currentTimeMillis())
                new ComplexFractalHeightMap(1.0,
                        1.0,
                        2.0,
                        HeightMaps.constant(1.0),
                        0.7,
                        GNoise.simplexNoise(0.005, 1.0, 0.5),
                        () -> HeightMaps.thresholdReverse(GNoise.simplexNoise(0.003, 1.0, 1.0), 1.0, false, false),
                        8)
                .setNormalize(true);
                //.toModded().addMod(4);

        //hm = HeightMaps.mult(hm, GNoise.simplexNoise(0.01, 1.0, 1.0), 0.5);


        Drawer drawer =
                //new FadingHeightMapDrawer(hm, 0, 0);
                new SimpleContourDrawer(x -> hm.get(x, 0), 1000, Drawer.Mode.HORIZONTAL, new Rectangle(0, 0.0, 1000, 0.7), bounds);


        canvas.beginDraw();
        canvas.background(255);

        drawer.draw(canvas, 1.0);

        canvas.endDraw();
    }

    @Override
    public void draw() {
        background(0);
        image(canvas, 0, 0, screenWidth, screenHeight);
    }

    public static void main(String[] args) {
        PApplet.main("VisualizeMain");
    }

    private void reset() {
        setup();
    }


    public void keyPressed() {
        switch(key) {
            case 'r': reset(); break;
            case 's': {
                String name ="output/visualize/" + System.currentTimeMillis() + ".png";
                canvas.save(name);
                System.out.println("saved " + name);
                break;
            }
        }
    }
}
