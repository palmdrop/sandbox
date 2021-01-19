package sketch.organic;

import color.colors.Color;
import color.colors.Colors;
import color.fade.ColorFade;
import color.fade.fades.MultiColorFade;
import organic.Component;
import organic.structure.segment.Segment;
import organic.structure.segment.Segments;
import organic.structure.segment.SimpleSegment;
import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PGraphics;
import processing.core.PShape;
import render.SampleDrawer;
import sampling.GraphicsSampler;
import sampling.countour.Contours;
import sampling.domainWarp.SourceDomainWarp;
import sketch.Sketch;
import util.geometry.Rectangle;
import util.math.MathUtils;
import util.noise.generator.GNoise;
import util.vector.ReadVector;
import util.vector.Vector;

public class FrequenciesSketch implements Sketch {
    private Rectangle bounds;
    private final PApplet p;

    public FrequenciesSketch(Rectangle bounds, PApplet p) {
        this.bounds = bounds;
        this.p = p;
    }

    //TODO: overall distort, frequency warp, only slight, but enough to create effect! vibrating!
    //TODO: thick lines made out of many small lines!?

    private Segment<Component> createBase(PGraphics canvas) {
        Vector p1 =
                //new Vector(50, canvas.height/2.0);
                Vector.random(50, 100, 0, canvas.height);
                //Vector.random(canvas.width, canvas.height);
        Vector p2 =
                //new Vector(canvas.width - 50, canvas.height/2.0);
                Vector.random(canvas.width - 100, canvas.width - 50, 0, canvas.height);

        Segment<Component> base = new SimpleSegment<>(p1, new Vector(1, 0));
        base.children().add(new SimpleSegment<>(p2, new Vector(-1, 0)));

        distort(base, 3, 0.1, 0.9, 0.24, 1);
        //TODO: distort main line, and for each copied root, offset every position with some amount!
        return base;
    }

    private ColorFade createFade() {
        Color start = Colors.parseHex(
                //"#ffc778"
                "#fffbc9"
        );
        Color middle = Colors.parseHex(
                //"#ffc778"
                "#ffe9c9"
        );
        Color end = Colors.parseHex("#ff7878");

        ColorFade fade =
                //new SimpleColorFade(start, end, Colors.HSB_SPACE);
                new MultiColorFade(Colors.HSB_SPACE, Contours.easing(MathUtils.EasingMode.EASE_OUT, 3), start, middle, end);
        //new RampFade(20 / 365.0f,  -(365 - 40) / 365.0f, 1.0, -1, RampFade.SatMode.DYNAMIC);
        return fade;
    }

    private PGraphics renderLayer(Segment<Component> base, int width, int height, int iterations, double deviation, double min, double blur, double frequency) {
        PGraphics layer = p.createGraphics(width, height);
        layer.smooth(4);
        layer.beginDraw();
        ColorFade fade = createFade();

        for(int i = 0; i < iterations; i++) {
            Segment<Component> root = Segments.copy(base);
            Segments.transpose(root, Vector.randomWithLength(MathUtils.randomGaussian(5 * frequency)));

            root.children().get(0).setPosition(Vector.add(root.children().get(0).getPosition(), Vector.randomWithLength(MathUtils.randomGaussian(100) * frequency)));

            int splits = (int)MathUtils.random(7, 10);
            distort(root, splits, 0.3, 0.7, 0.16, 1);

            double v = Math.abs(MathUtils.randomGaussian(deviation)) + min;
            //double v = MathUtils.random(min, max);
            double alpha = Math.pow(v, 1.5) * 30;

            Color color = fade.get(MathUtils.limit(1 - 1.2*v, 0, 1));

            layer.noFill();
            layer.stroke(color.toRGB(), (float)alpha);
            layer.strokeWeight((float) (frequency * 6.0 / Math.pow(v, 2)));
            drawAsLine(root, layer);
        }

        layer.filter(PConstants.BLUR, (float)blur);
        layer.endDraw();

        return layer;
    }

    public PGraphics renderLightning(Segment<Component> base, PGraphics canvas, PApplet p, double frequency) {
        PGraphics background = renderLayer(base, canvas.width, canvas.height, 80, 0.4, 0.0, 4.0, frequency);
        PGraphics middle     = renderLayer(base, canvas.width, canvas.height, 100, 0.6, 0.4, 2.0, frequency);
        PGraphics foreground = renderLayer(base, canvas.width, canvas.height, 100, 0.4, 0.8, 0.0, frequency);

        PGraphics c = p.createGraphics(canvas.width, canvas.height);
        c.beginDraw();
        c.image(background, 0, 0);
        c.image(middle, 0, 0);
        c.image(foreground, 0, 0);
        c.endDraw();
        return c;
    }

    @Override
    public PGraphics draw(PGraphics canvas, double frequency) {
        canvas.beginDraw();

        ColorFade fade = createFade();
        canvas.fill(fade.get(0.5).toRGB(), 10);
        canvas.rect(0, 0, canvas.width, canvas.height);

        double offset = canvas.width * 0.2;

        Vector p1 = new Vector(offset, offset);
        Vector p2 = new Vector(canvas.width - offset * 1.2, canvas.height - offset * 1.2);
        Segment<Component> base = new SimpleSegment<>(p1, new Vector(1, 0));
        base.children().add(new SimpleSegment<>(p2, new Vector(-1, 0)));
        //distort(base, 3, 0.1, 0.9, 0.24, 1);

        //Segment<Component> base = createBase(canvas);
        canvas.image(renderLightning(base, canvas, p, frequency), 0, 0);

        p1 = new Vector(offset, canvas.height - offset);
        p2 = new Vector(canvas.width - offset, offset);
        base = new SimpleSegment<>(p1, new Vector(1, 0));
        base.children().add(new SimpleSegment<>(p2, new Vector(-1, 0)));
        distort(base, 3, 0.1, 0.9, 0.24, 1);

        //base = createBase(canvas);
        //canvas.image(renderLightning(base, canvas, p, frequency), 0, 0);

        //base = createBase(canvas);
        //canvas.image(renderLightning(base, canvas, p, frequency), 0, 0);

        canvas.endDraw();

        //Texture
        SourceDomainWarp<Integer> warpedCanvas =
                new SourceDomainWarp<>(new GraphicsSampler(canvas.copy(), 1, GraphicsSampler.WrapMode.BACKGROUND));

        warpedCanvas
                .domainWarp(GNoise.simplexNoise(0.03, 1.0, 1.0), 0.5 * frequency)
                //.domainWarp(HeightMaps.random(0, 1), GNoise.simplexNoise(0.01, 1.0, 1.0), 0.4)
                //.domainWarp(HeightMaps.saw(0.1, 0.1, 0, 1.0), 2)
                ;


        SampleDrawer drawer = new SampleDrawer(warpedCanvas, canvas.width, canvas.height, new Vector());
        drawer.draw(canvas, 1);

        canvas.beginDraw();
        canvas.loadPixels();
        canvas.colorMode(PApplet.HSB);

        for(int i = 0; i < canvas.pixels.length; i++) {
            int c = canvas.pixels[i];

            double h = canvas.hue(c);
            double s = canvas.saturation(c);
            double b = canvas.brightness(c);

            b += 20 * (Math.random() + Math.random()) / 2;

            canvas.pixels[i] = canvas.color((float)h, (float)s, (float)b);
        }
        canvas.updatePixels();


        canvas.endDraw();

        return canvas;
    }

    private void drawAsLine(Segment<Component> root, PGraphics canvas) {
        PShape shape = canvas.createShape();
        shape.beginShape();
        //canvas.beginShape();

        Segment<Component> current = root;
        while(!current.children().isEmpty()) {
            ReadVector position = current.getPosition();
            shape.vertex((float)position.getX(), (float)position.getY());

            current = current.children().get(0);
        }

        //canvas.endShape();
        shape.endShape();
        canvas.shape(shape);
    }

    private Segment<Component> distort(Segment<Component> root, int splits, double splitStart, double splitEnd, double splitOffset, double lengthPow) {
        for (int i = 0; i < splits; i++) {
            Segments.offsetSplit(root, splitStart, splitEnd, splitOffset,lengthPow);
        }
        return root;
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
