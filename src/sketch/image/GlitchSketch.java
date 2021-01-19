package sketch.image;

import color.colors.Colors;
import color.space.ColorSpace;
import image.PixelModifier;
import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PImage;
import render.SampleDrawer;
import sampling.GraphicsSampler;
import sampling.Sampler;
import sampling.domainWarp.SimpleDomainWarp;
import sketch.Sketch;
import util.geometry.Rectangle;
import util.vector.Vector;

public class GlitchSketch implements Sketch {
    private Rectangle bounds;
    private PImage img;

    public GlitchSketch(PApplet p) {
        //this.bounds = bounds;
        this.img = p.loadImage("sourceImages/numbers.png");
        bounds = new Rectangle(0, 0, img.width, img.height);
    }

    @Override
    public PGraphics draw(PGraphics canvas, double frequency) {
        // Modify image
        Sampler<Integer> imageSampler = new GraphicsSampler(img, 1, GraphicsSampler.WrapMode.MIRROR_WRAP);

        imageSampler = new SimpleDomainWarp<>(imageSampler)
                .add(p -> {
                    double mult = 0.5;
                    return p.mult(mult);
                })
                .add(p -> {
                    //p.add(Vector.randomWithLength(MathUtils.randomGaussian()));
                    //double angle = 0.001*(p.getX() % 131 + p.getY() % 17) % Math.PI * 2;
                    //double amount = (angle % 0.5) / 0.5;

                    //p.add(Vector.fromAngle(angle).mult(amount * canvas.width / 50));

                    double v = 0.01 * p.getX() * p.getY();
                    double angle = 0;
                    double amount = 1;

                    double mod = v % 100;

                    if(mod < 25) {
                        angle = 0;
                    } else if(mod < 50) {
                        angle = PApplet.PI/2;
                    } else if(mod < 75) {
                        angle = PApplet.PI;
                    } else {
                        angle = 3 * PApplet.PI / 2;
                    }

                    double a = 0;
                    if(v % 19 == 0) {
                        a = 1.0;
                    } else if(v % 101 == 0) {
                        a = 0.5;
                    } else {
                        a = 0.25;
                    }
                    //amount = 1.0;
                    p.add(Vector.fromAngle(angle).mult(a * amount * canvas.width / 20));

                    return p;
                })
        ;

        imageSampler = new PixelModifier(imageSampler, c -> {
            ColorSpace colorSpace = Colors.CMY_SPACE;
            ColorSpace colorSpace2 = Colors.XYZ_SPACE;
            double[] components = colorSpace.getComponents(c);

            int min = 0;
            int max = 0;

            for(int i = 0; i < components.length; i++) {
                if(components[i] < components[min]) {
                    min = i;
                }
                if(components[i] > components[max]) {
                    max = i;
                }
            }

            //ArrayAndListTools.swap(components, min, max);

            //return colorSpace.getRGB(components);
            return c; //colorSpace2.getRGB(components);
        });


        // Render
        long time = System.currentTimeMillis();

        canvas.beginDraw();

        SampleDrawer drawer = new SampleDrawer(imageSampler, canvas.width, canvas.height, new Vector());
        drawer.draw(canvas, 1);

        canvas.endDraw();

        System.out.println(System.currentTimeMillis() - time);
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
