package sketch.color;

import color.colors.Colors;
import color.fade.ColorFade;
import color.fade.fades.MultiColorFade;
import color.palette.drawer.SimplePaletteDrawer;
import color.palette.Palette;
import color.palette.drawer.PaletteDrawer;
import color.palette.palettes.balanced.sample1D.FadePalette;
import processing.core.PGraphics;
import render.AbstractDrawer;
import render.Drawer;
import sampling.countour.Contours;
import sketch.Sketch;
import util.geometry.Rectangle;
import util.geometry.divider.SimpleDivider;

import color.colors.Color;

import java.util.ArrayList;
import java.util.List;


public class BreathSketch extends AbstractDrawer implements Sketch {
    private List<Drawer> drawers;

    public BreathSketch(Rectangle bounds, int pSize) {
        super(bounds);

        int size = 100;
        PaletteDrawer.Mode mode = PaletteDrawer.Mode.HORIZONTAL;

        List<Rectangle> sections = new SimpleDivider(
                mode == PaletteDrawer.Mode.VERTICAL ? size : 1,
                mode == PaletteDrawer.Mode.VERTICAL ? 1 : size,
                0.0,
                0.0).divide(bounds);

        Color c1 =
                Colors.RGB_SPACE.getColor(1, 0.5, 0.16);

        Color c3 =
                Colors.RGB_SPACE.getColor(0.7, 0.1, 0.1);

        ColorFade colorFade = new MultiColorFade(Colors.RGB_SPACE, Contours.linear(0, 1), c1, c3);

        drawers = new ArrayList<>(size);
        for(int i = 0; i < size; i++) {
            int samples = (int) (Math.sin(2.0* Math.PI * i / size + 3*Math.PI/2) * pSize + pSize + 2);

            Palette palette2 = new FadePalette(colorFade, samples);

            Drawer drawer =
                    new SimplePaletteDrawer(palette2, sections.get(i), mode, 0.0);
            drawers.add(drawer);
        }
    }

    @Override
    public PGraphics draw(PGraphics canvas, double frequency) {
        for(Drawer drawer : drawers) {
            drawer.draw(canvas, frequency);
        }
        return canvas;
    }
}
