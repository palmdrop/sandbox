package sketch.vision;

import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PImage;
import sketch.Sketch;
import util.ArrayAndListTools;
import util.file.FileUtils;
import util.geometry.Rectangle;
import vision.ReaderVision;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class MisinterpretSketch implements Sketch {
    private Rectangle bounds;
    private PApplet p;

    public MisinterpretSketch(PApplet p) {
        bounds = new Rectangle(2000, 2000);
        this.p = p;
    }

    @Override
    public PGraphics draw(PGraphics canvas, double frequency) {
        canvas.beginDraw();
        canvas.background(0, 0, 0);

        File file = new File(
                //"/home/xan/Pictures/collected/2020-06-13_09-31.png"
                //"sourceData/skuggor_och_spegling.pdf"
                //"sourceData/myller.pdf"
                //"sourceData/this-is-water.txt"
                "sourceData/skuggor_och_spegling.txt"
                //"sourceData/a-tale-of-two-cities.txt"
                //"src/vision/ReaderVision.java"
                //"src/VisionMain.java"
                //"sourceImages/face1.jpg"
        );


        try {
            FileReader fileReader = new FileReader(file);

            long length = file.length();
            ReaderVision vision = new ReaderVision(bounds, fileReader, length);
            vision.draw(canvas);

            fileReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        /*PImage img = p.loadImage(
                ArrayAndListTools.randomElement(FileUtils.listFiles(
                        //"sourceImages/growth/",
                        "sourceImages/",
                        //"photos/test/postable/",
                        new String[]{".png", ".jpg", ".jpeg"})).getPath()
        );

        for(int x = 0; x < img.width; x++) for(int y = 0; y < img.height; y++) {

        }

        canvas.image(img, 0, 0, canvas.width, canvas.height);*/

        canvas.endDraw();
        return canvas;
    }

    @Override
    public Rectangle getBounds() {
        return bounds;
    }

    @Override
    public void setBounds(Rectangle bounds) {
    }
}
