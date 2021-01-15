package sketch.color;

import color.colors.Color;
import color.colors.Colors;
import color.colors.rgb.RGBColor;
import color.fade.ColorFade;
import color.fade.fades.RampFade;
import color.palette.Palette;
import color.palette.drawer.PaletteDrawer;
import color.palette.drawer.SimplePaletteDrawer;
import color.palette.palettes.balanced.sample1D.FadePalette;
import color.space.ColorSpace;
import color.space.drawer.MapColorSpaceDrawer;
import processing.core.PGraphics;
import render.AbstractDrawer;
import sampling.heightMap.HeightMap;
import sketch.Sketch;
import texture.drawer.apply.CMYKTextureApplier;
import util.geometry.Rectangle;
import util.geometry.divider.SimpleDivider;
import util.math.MathUtils;
import util.noise.generator.GNoise;
import util.vector.Vector;

import java.util.ArrayList;
import java.util.List;

public class CompositionSketch extends AbstractDrawer implements Sketch {
    public CompositionSketch(Rectangle bounds) {
        super(bounds);
    }

    @Override
    public PGraphics draw(PGraphics canvas, double frequency) {
        //canvas.background(255, 0, 0);

        // RENDER BACKGROUND
        // Base color space
        ColorSpace colorSpace = Colors.RGB_SPACE;
        HeightMap hm1 =
                GNoise.simplexNoise(1.0, 1.0, 1.0);
        HeightMap hm2 =
                GNoise.simplexNoise(2.0, 1.0, 1.0);
        HeightMap hm3 =
                //HeightMaps.circles(0.5, 0.5, 0.0, 0.0, new Vector(), 2);
                GNoise.simplexNoise(0.4, 1.0, 1.0)
                //.toModded()
                //.addRemap(0, 1, 0.2, 0.4)
        ;

        MapColorSpaceDrawer colorSpaceDrawer =
                new MapColorSpaceDrawer(colorSpace, bounds, hm1, hm2, hm3);

        colorSpaceDrawer.draw(canvas);

        // CMYKify
        CMYKTextureApplier cmykTextureApplier = new CMYKTextureApplier(0.006, 0.3, 1, bounds);
        cmykTextureApplier.setAmount(0.8);

        // GENERATE BASE PALETTES
        List<Rectangle> baseSplit = SimpleDivider.divide(1, (int) MathUtils.random(3, 20), MathUtils.random(0), 0, bounds);
        List<Palette> basePalettes = new ArrayList<>(baseSplit.size());

        canvas.loadPixels();
        for(Rectangle section : baseSplit) {
            Vector p = section.getCenter();
            int rgb = canvas.pixels[(int) (p.getX() + p.getY() * canvas.width)];
            Color color = new RGBColor(rgb);

            double shift = MathUtils.random(0.2, 0.5) * (Math.random() > 0.5 ? -1 : 1);
            double contrast = MathUtils.random(0.5, 1.0) * (Math.random() > 0.5 ? -1 : 1);

            ColorFade fade = RampFade.fromColor(color, shift, 1.0, contrast, RampFade.SatMode.DYNAMIC);
            Palette palette = new FadePalette(fade, (int) MathUtils.random(3, 10));
            //ColorFadeDrawer drawer = new SimpleColorFadeDrawer(fade, section, Mode.HORIZONTAL);

            PaletteDrawer drawer = new SimplePaletteDrawer(palette, section, PaletteDrawer.Mode.HORIZONTAL, 20);

            drawer.draw(canvas);
        }

        //cmykTextureApplier.draw(canvas);


        return canvas;
    }
}
