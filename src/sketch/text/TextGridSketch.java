package sketch.text;

import color.colors.Color;
import color.colors.Colors;
import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PGraphics;
import processing.core.PImage;
import render.Drawer;
import render.SampleDrawer;
import render.heightMap.FadingHeightMapDrawer;
import sampling.GraphicsHeightMap;
import sampling.GraphicsSampler;
import sampling.Sampler;
import sampling.countour.Contour;
import sampling.countour.Contours;
import sampling.domainWarp.DomainWarp;
import sampling.domainWarp.SimpleDomainWarp;
import sampling.heightMap.HeightMap;
import sampling.heightMap.HeightMaps;
import sampling.patterns.CurlWavePattern;
import sampling.patterns.FabricSurfacePattern;
import sketch.Sketch;
import text.SymbolAnalysis;
import text.TextTools;
import text.render.TextGridDrawer;
import util.ArrayAndListTools;
import util.file.FileUtils;
import util.geometry.Rectangle;
import util.math.MathUtils;
import util.noise.Noise;
import util.noise.generator.GNoise;
import util.noise.type.CellularNoise;
import util.noise.type.CraterNoise;
import util.noise.type.NoiseSource;
import util.vector.Vector;


public class TextGridSketch implements Sketch {
    private Rectangle bounds;
    private final PApplet p;

    private final Drawer drawer;
    private final HeightMap heightMap, hm;
    private final PImage image;
    private final double multiplier;
    private final double offset;

    public TextGridSketch(PApplet p, Rectangle bounds, double multiplier) {
        this.p = p;
        this.bounds = bounds;
        this.multiplier = multiplier;

        String[] fonts = PFont.list();
        for (String font : fonts) {
            System.out.println(font);
        }


        String f =
                "scientifica Bold";
                //"unscii-16";
        int size = (int) (10 * multiplier);
        double padding = -0.2;
        offset = -10;


        String path =
                ArrayAndListTools.randomElement(FileUtils.listFiles(
                "sourceImages/collected/",
                new String[]{".png", ".jpg", ".jpeg"})).getPath();
                //"sourceImages/face1.jpg";

        image = p.loadImage(path);

        GraphicsHeightMap heightMap =
                new GraphicsHeightMap(image, GraphicsSampler.WrapMode.CLAMP, Colors::brightness);

        double stretch =
                Math.max(
                        (double)heightMap.getImage().width / bounds.width,
                        (double)heightMap.getImage().height / bounds.height
                );

        HeightMap hm =
            //HeightMaps.stretch(heightMap, stretch, stretch);
            new FabricSurfacePattern(0.001, 0.3, 0.5, 1.7, 0.5, 40);

        this.hm = hm;



        //GNoise.simplexNoise(0.002, 1.0, 1.0);
        int l = TextTools.renderableAscii.length;
        char[] chars =
                //{'.', 'ö', 'å', ' ', ' ', ' '};
                //{' ', ' ', ' ', ' ', '.', '.', '*', '*', 'o', 'o', 'O', 'O', '0', '0', 'U', 'U'};
                //{' ', ' ', ' ', ' ', '.', '.', '*', '*', 'o', 'o', 'O', 'O', '0', '0', 'U', 'U'};
                //SymbolAnalysis.sortBy(TextTools.renderableAscii, 100, SymbolAnalysis::normalizedFill, p);
                ArrayAndListTools.pick(
                        SymbolAnalysis.sortBy(TextTools.renderableAscii, 100, SymbolAnalysis::normalizedFill, p),
                        0,
                                0,
                                1,
                        (int)(MathUtils.map(0.1, 0, 1, 0, l - 1) + MathUtils.random(-10, 10)),
                        (int)(MathUtils.map(0.1, 0, 1, 0, l - 1) + MathUtils.random(-10, 10)),
                        (int)(MathUtils.map(0.2, 0, 1, 0, l - 1) + MathUtils.random(-10, 10)),
                        (int)(MathUtils.map(0.2, 0, 1, 0, l - 1) + MathUtils.random(-10, 10)),
                        (int)(MathUtils.map(0.3, 0, 1, 0, l - 1) + MathUtils.random(-10, 10)),
                        (int)(MathUtils.map(0.3, 0, 1, 0, l - 1) + MathUtils.random(-10, 10)),
                        (int)(MathUtils.map(0.4, 0, 1, 0, l - 1) + MathUtils.random(-10, 10)),
                        (int)(MathUtils.map(0.4, 0, 1, 0, l - 1) + MathUtils.random(-10, 10)),
                        (int)(MathUtils.map(0.5, 0, 1, 0, l - 1) + MathUtils.random(-10, 10)),
                        (int)(MathUtils.map(0.5, 0, 1, 0, l - 1) + MathUtils.random(-10, 10)),
                        (int)(MathUtils.map(0.6, 0, 1, 0, l - 1) + MathUtils.random(-10, 10)),
                        (int)(MathUtils.map(0.6, 0, 1, 0, l - 1) + MathUtils.random(-10, 10)),
                        (int)(MathUtils.map(0.7, 0, 1, 0, l - 1) + MathUtils.random(-10, 10)),
                        (int)(MathUtils.map(0.7, 0, 1, 0, l - 1) + MathUtils.random(-10, 10)),
                        (int)(MathUtils.map(0.8, 0, 1, 0, l - 1) + MathUtils.random(-10, 10)),
                        (int)(MathUtils.map(0.8, 0, 1, 0, l - 1) + MathUtils.random(-10, 10)),
                        (int)(MathUtils.map(0.9, 0, 1, 0, l - 1) + MathUtils.random(-10, 10)),
                        (int)(MathUtils.map(0.9, 0, 1, 0, l - 1) + MathUtils.random(-10, 10)),
                                l - 1);

        TextGridDrawer.CharController cc = (x,y) -> {
            double n = hm.get(x,y);
            //n += MathUtils.random(-1, 1)/ (chars.length - 1);
            n = Math.pow(n, 2);
            n = MathUtils.limit(n, 0, 1.0);
            return n;
        };

        TextGridDrawer.ColorController fgc = (x, y, n) -> {
            double h = 0.7;
            double s = 0.0;
            double b = 0.8;

            int index = (int) MathUtils.map(n, 0, 1, 0, chars.length - 1);
            /*if(chars[index] == '=' || chars[index] == 'a' || chars[index] == '9') {
                b = 0.7;
                s = 0.2;
            }
            if(chars[index] == '#') {
                b = 0.4;
                h = 0.8;
            }*/

            Color c = Colors.HSB_SPACE.getColor(h, s, b);
            return c;
        };
        TextGridDrawer.ColorController bgc = (x,y,n) -> Colors.HSB_SPACE.getColor(0.0, 0.0, 1.0, 0.0);


        drawer = new TextGridDrawer(p, bounds, f, size, padding, chars, cc, bgc, fgc);
        //heightMap = new CurlWavePattern(0.01, 0.01, Math.random() * 6.3, 0.6, 2.0, 1.0, 8);
        this.heightMap = heightMap;
    }

    @Override
    public PGraphics draw(PGraphics canvas, double frequency) {
        drawer.draw(canvas);
        //canvas.image(image, 0, 0);
        DomainWarp<Integer> graphics = new SimpleDomainWarp<>(new GraphicsSampler(canvas.copy()));
        //graphics.domainWarp(new GraphicsHeightMap(image, GraphicsSampler.WrapMode.MIRROR_WRAP, Colors::brightness), offset);
        graphics.domainWarp(hm, 50);

        Drawer gDrawer = new SampleDrawer(graphics, canvas.width, canvas.height, new Vector());

        gDrawer.draw(canvas);

        return canvas;
    }

    @Override
    public Rectangle getBounds() {
        return bounds;
    }

    @Override
    public void setBounds(Rectangle bounds) {
        this.bounds = bounds;
    }
}
