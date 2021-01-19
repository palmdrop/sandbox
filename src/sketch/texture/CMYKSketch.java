package sketch.texture;

import color.colors.Color;
import color.colors.Colors;
import color.fade.ColorFade;
import color.fade.drawer.ColorFadeDrawer;
import color.fade.drawer.SimpleColorFadeDrawer;
import color.fade.fades.RampFade;
import color.palette.Palette;
import color.palette.palettes.balanced.sample1D.FadePalette;
import processing.core.PGraphics;
import render.AbstractDrawer;
import render.Drawer;
import sketch.Sketch;
import texture.drawer.apply.CMYKTextureApplier;
import util.geometry.Rectangle;
import util.math.MathUtils;

public class CMYKSketch extends AbstractDrawer implements Sketch {
    public CMYKSketch(Rectangle bounds) {
        super(bounds);
        Color c =
                //Colors.random();
                Colors.random(Colors.HSB_SPACE, new double[]{0, 0.0, 0.5}, new double[]{1.0, 0.9, 1.0});

        double hueShift = MathUtils.random(0.2, 0.5) * (Math.random() < 0.5 ? -1 : 1);

        ColorFade fade = RampFade.fromColor(c, hueShift, 1, -0.5, RampFade.SatMode.DYNAMIC);
        Palette palette = new FadePalette(fade, 7);
        drawer =
                //new SimplePaletteDrawer(palette, bounds, PaletteDrawer.Mode.VERTICAL, 0.0);
                //new ComparativePaletteDrawer(palette, bounds, PaletteDrawer.Mode.VERTICAL, 60);
                new SimpleColorFadeDrawer(fade, bounds, ColorFadeDrawer.Mode.HORIZONTAL);

        createApplier(1.3);
    }

    Drawer drawer;
    CMYKTextureApplier textureApplier;

    public void createApplier(double blur) {
        textureApplier = new CMYKTextureApplier(0.006, 0.2, blur,
                bounds
        );
    }

    @Override
    public PGraphics draw(PGraphics canvas, double frequency) {
        long time = System.currentTimeMillis();


        //canvas.background(c.toRGB());
        canvas.background(0);
        canvas.updatePixels();

        drawer.draw(canvas);
        textureApplier.draw(canvas);

        System.out.println(System.currentTimeMillis() - time);

        return canvas;
    }

    @Override
    public void setBounds(Rectangle bounds) {
        super.setBounds(bounds);
        drawer.setBounds(bounds);
        textureApplier.setBounds(bounds);
    }
}
