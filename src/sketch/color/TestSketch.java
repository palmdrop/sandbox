package sketch.color;

import color.colors.Color;
import color.colors.Colors;
import color.palette.Palette;
import color.palette.drawer.ComparativePaletteDrawer;
import color.palette.drawer.PaletteDrawer;
import color.palette.file.FilePaletteHandler;
import color.palette.palettes.balanced.BasicPalette;
import image.data.color.DominantColors;
import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PImage;
import render.AbstractDrawer;
import sketch.Sketch;
import util.geometry.Rectangle;

import java.io.IOException;

public class TestSketch extends AbstractDrawer implements Sketch {
    private final PApplet p;

    public TestSketch(Rectangle bounds, PApplet p) {
        super(bounds);
        this.p = p;
    }

    @Override
    public PGraphics draw(PGraphics canvas, double frequency) {

        String path =
                "sourceImages/kandinsky1.jpg";
            /*ArrayAndListTools.randomElement(FileUtils.listFiles(
                    //"sourceImages/collected/",
                    new String[]{".png", ".jpg", ".jpeg"})).getPath();*/
                //"sourceImages/collected/forest.jpg";
        System.out.println(path);

        PImage image = p.loadImage(path);

        Color background =
                Colors.RGB_SPACE.getColor(0, 0, 0);
        canvas.background(background.toRGB());

        DominantColors.ColorsAndCounts cc = DominantColors.calculate(image, Colors.HSB_SPACE, 10, 0.6, 30);

        Palette palette = new BasicPalette(cc.dominantColors);

        PaletteDrawer drawer = new ComparativePaletteDrawer(palette, bounds, PaletteDrawer.Mode.VERTICAL, 0.0);

        try {
            FilePaletteHandler.saveGenericPalette(palette, "palettes/ramp.palette");
        } catch (IOException e) {
            e.printStackTrace();
        }

        drawer.draw(canvas);
        canvas.image(image, 0, 0, 500, 500);

        return canvas;
    }
}
