package sketch.color;

import color.colors.Color;
import color.colors.Colors;
import color.space.ColorSpace;
import color.space.space.FakeNCSColorSpace;
import processing.core.PGraphics;
import render.AbstractDrawer;
import sketch.Sketch;
import util.geometry.Rectangle;
import util.geometry.divider.SimpleDivider;

import java.util.ArrayList;
import java.util.List;

public class ColorOpSketch extends AbstractDrawer implements Sketch {
    private Color c1 =
            FakeNCSColorSpace.YELLOW;
            //new RGBColor(1.0, 0.3, 0.6);
            //new RGBColor(0, 0, 0);
    private Color c2 =
            FakeNCSColorSpace.BLUE;
            //new RGBColor(0.2, 0.8,0.1);
            //new RGBColor(1.0, 1.0, 1.0);

    private ColorSpace[] spaces = {
            Colors.RGB_SPACE,
            Colors.HSB_SPACE,
            Colors.CMY_SPACE,
            Colors.XYZ_SPACE,
            Colors.YUV_SPACE,
            Colors.YCC_SPACE
    };

    private List<Rectangle> mainDiv;
    private List<Rectangle> sections;

    public ColorOpSketch(Rectangle bounds) {
        super(bounds);

        sections = new ArrayList<>(spaces.length);
        setBounds(bounds);
    }

    @Override
    public PGraphics draw(PGraphics canvas, double frequency) {
        Rectangle r1 = mainDiv.get(0);
        Rectangle r2 = mainDiv.get(2);

        canvas.fill(c1.toRGB());
        Rectangle.render(canvas, r1);

        canvas.fill(c2.toRGB());
        Rectangle.render(canvas, r2);

        for(int i = 0; i < spaces.length; i++) {
            Rectangle r3 = sections.get(i);

            ColorSpace colorSpace = spaces[i];
            Color c3 =
                    //Colors.add(c1, c2, colorSpace);
                    Colors.pow(c1, c2, colorSpace);
                    //Colors.div(c1, c2, colorSpace);

                    //Colors.lerp(c1, c2, 0.5, colorSpace);
                    //Colors.mult(c1, c2, colorSpace);
                    //Colors.diff(c1, c2, colorSpace);
                    //Colors.sub(c1, c2, colorSpace);

            canvas.fill(c3.toRGB());
            Rectangle.render(canvas, r3);

            canvas.fill(0);
            canvas.text(colorSpace.getClass().getCanonicalName() + "", (float)r3.x + 10f, (float)r3.y + 20f);
        }
        return canvas;
    }


    @Override
    public void setBounds(Rectangle bounds) {
        super.setBounds(bounds);

        mainDiv = new SimpleDivider(3, 1, 0, 0).divide(bounds);
        sections = new SimpleDivider(1, spaces.length, 0, 0).divide(mainDiv.get(1));
    }
}
