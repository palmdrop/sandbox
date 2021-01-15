package sketch.contour;

import color.colors.Colors;
import processing.core.PGraphics;
import render.AbstractDrawer;
import sampling.countour.Contour;
import sampling.countour.Contours;
import sampling.countour.drawer.SimpleContourDrawer;
import sketch.Sketch;
import texture.drawer.apply.CMYKTextureApplier;
import util.geometry.Rectangle;
import util.math.MathUtils;

public class ContourSketch extends AbstractDrawer implements Sketch {
    public ContourSketch(Rectangle bounds) {
        super(bounds);
    }

    @Override
    public PGraphics draw(PGraphics canvas, double frequency) {
        canvas.background(Colors.HSB_SPACE.getColor(0.1, 0.1, 1.0).toRGB());

        Contour contour =
                Contours
                //.easing(MathUtils.EasingMode.EASE_IN_OUT, 2.0);
                .sin(100, 1.0);


        Rectangle drawSection =
                Rectangle.range(-0.1, 1, -2, 2);

        SimpleContourDrawer drawer = new SimpleContourDrawer(contour, 1000, Mode.HORIZONTAL, drawSection, bounds);
        drawer.setWidth(3);
        drawer.draw(canvas);

        CMYKTextureApplier textureApplier = new CMYKTextureApplier(0.01, 0.1, 1, bounds);
        //textureApplier.draw(canvas);



        return canvas;
    }
}
