import color.colors.Colors;
import organic.generation.points.PointGenerator;
import organic.generation.points.area.HeightMapPointGenerator;
import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PImage;
import render.Drawer;
import render.heightMap.FadingHeightMapDrawer;
import sampling.GraphicsHeightMap;
import sampling.GraphicsSampler;
import sampling.Sampler1D;
import sampling.countour.Contours;
import sampling.heightMap.HeightMap;
import sampling.heightMap.HeightMaps;
import sampling.heightMap.modified.FractalHeightMap;
import text.TextTools;
import util.geometry.Rectangle;
import util.math.MathUtils;
import util.noise.generator.GNoise;
import util.noise.type.OpenSimplexNoise;
import util.vector.Vector;

import java.util.List;

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
        //PGraphics temp = createGraphics(screenWidth, screenHeight);
        PImage temp = this.loadImage("sourceImages/collected/eye.jpg");

        //temp.beginDraw();
        //temp.endDraw();

        double scaleX = 1.0 * temp.width / canvas.width;
        double scaleY = 1.0 * temp.height / canvas.height;

        HeightMap hm =
                HeightMaps.stretch(new GraphicsHeightMap(temp, GraphicsSampler.WrapMode.MIRROR_WRAP, Colors::brightness),
                scaleX, scaleY);
                //GNoise.simplexNoise(0.005, 1.0, 3.0);
                /*HeightMaps.pow(
                        new FractalHeightMap(0.005, 1.0, 2.0, 0.5, FractalHeightMap.Type.SIMPLEX, 8, System.currentTimeMillis()).setNormalize(true),
                        HeightMaps.constant(4));*/

        PointGenerator pg = new HeightMapPointGenerator(hm, screenWidth, screenHeight);
        List<Vector> points = pg.generate(30000);

        Drawer drawer =
                new FadingHeightMapDrawer(hm, 0, 0);
                //new SimpleContourDrawer(x -> hm.get(x, 0), 1000, Drawer.Mode.HORIZONTAL, new Rectangle(0, 0.0, 1000, 1.0), bounds);
                //new SimpleContourDrawer(contour, 1000, Drawer.Mode.HORIZONTAL, new Rectangle(0, 0.0, 1.0, 1.0), bounds);


        canvas.beginDraw();
        canvas.background(0);

        //drawer.draw(canvas, 1.0);

        canvas.beginDraw();
        canvas.strokeWeight(2);
        canvas.stroke(255);
        canvas.fill(255);
        for(Vector p : points) {
            canvas.point((float)p.getX(), (float)p.getY());
        }

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
