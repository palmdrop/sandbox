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
                "sourceData/example_a-tale-of-two-cities.txt"
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
