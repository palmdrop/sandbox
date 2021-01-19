package sketch.color;

import color.colors.Color;
import color.colors.Colors;
import color.colors.hsb.HSBColor;
import color.fade.ColorFade;
import color.fade.drawer.ColorFadeDrawer;
import color.fade.drawer.SimpleColorFadeDrawer;
import color.fade.fades.SimpleColorFade;
import color.space.ColorSpace;
import processing.core.PGraphics;
import render.AbstractDrawer;
import sampling.countour.Contour;
import sampling.countour.Contours;
import sketch.Sketch;
import util.geometry.Rectangle;
import util.geometry.divider.SimpleDivider;
import util.math.MathUtils;

import java.util.ArrayList;
import java.util.List;

public class FadeSketch extends AbstractDrawer implements Sketch {
    private Color start =
            //= FakeNCSColorSpace.GREEN;
            new HSBColor(Math.random(), MathUtils.random(0.3, 0.7), MathUtils.random(0.4, 1.0));
    private Color end =
            new HSBColor(Math.random(), MathUtils.random(0.3, 0.7), MathUtils.random(0.4, 1.0));
            //= FakeNCSColorSpace.RED;

    private ColorSpace colorSpace = Colors.RGB_SPACE;

    private int count = 100;

    private List<Rectangle> sections;
    private List<ColorFadeDrawer> drawers;

    private double margins = 0.0;

    private boolean vertical = true;

    public FadeSketch(Rectangle bounds) {
        super(bounds);

        sections = new ArrayList<>(count);
        drawers = new ArrayList<>(count);

        setBounds(bounds);

        for(int i = 0; i < count; i++) {
            Contour contour =
                    //Contours.easing(MathUtils.EasingMode.EASE_IN, ((float)10*i / count) % 1.0);
                    Contours.sin(5*Math.cos(i*.9), 1).remap(-1, 1, 0, 1).pow(1);

            if(i % 2 == 1) {
                Contour finalContour = contour;
                contour = c -> finalContour.get(1 - c);
            }

            ColorFade fade = new SimpleColorFade(start, end, contour, colorSpace);
            ColorFadeDrawer drawer = new SimpleColorFadeDrawer(fade, null, vertical ? ColorFadeDrawer.Mode.VERTICAL : ColorFadeDrawer.Mode.HORIZONTAL);
            drawers.add(drawer);
        }
    }

    @Override
    public PGraphics draw(PGraphics canvas, double frequency) {
        for(int i = 0; i < drawers.size(); i++) {
            ColorFadeDrawer drawer = drawers.get(i);
            drawer.setBounds(sections.get(i));
            drawer.draw(canvas);
        }
        return canvas;
    }


    @Override
    public void setBounds(Rectangle bounds) {
        super.setBounds(bounds);
        sections = new SimpleDivider(vertical ? 1 : count, vertical ? count : 1, margins, margins).divide(bounds);
    }
}
