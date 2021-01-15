package sketch.image;

import color.colors.Colors;
import color.space.ColorSpace;
import image.GraphicsCombiner;
import image.PixelModifier;
import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PImage;
import render.Drawer;
import render.SampleDrawer;
import sampling.CombinedSampler;
import sampling.GraphicsSampler;
import sampling.Sampler;
import sampling.domainWarp.DomainWarp;
import sampling.domainWarp.SimpleDomainWarp;
import sampling.heightMap.HeightMap;
import sampling.heightMap.HeightMaps;
import sketch.Sketch;
import util.ArrayAndListTools;
import util.geometry.Rectangle;
import util.vector.Vector;

import java.util.function.Function;

public class CombinedSketch implements Sketch {
    private Rectangle bounds;
    private final Drawer drawer;

    private PImage[] init(PApplet p, String folder, String[] files) {
        // Load images
        PImage[] images = new PImage[files.length];
        for(int i = 0; i < files.length; i++) {
            images[i] = p.loadImage(folder + files[i]);
        }

        // Set bounds
        this.bounds = new Rectangle(images[0].width, images[0].height);

        return images;
    }


    public CombinedSketch(PApplet p){
        PImage[] images = init(p, "sourceImages/", new String[]{
                //"bush1.png",
                //"bush3.png",
                //"bush2.png",
                "f1.png",
                "f2.png",
                "f3.png",
                "pond1.png",
                //"f5.png",
                //"face2.jpg",
                //"face1.jpg",
                //"selfie1.jpg",
                //"selfie3.jpg",
                //"selfie4.jpg",
                //"pond2.png",
                //"spray1.png",
                //"spray2.png",
                //"hud.jpg",
        });

        // Create image
        Sampler<Integer> text = new GraphicsSampler(p.loadImage("sourceImages/text1.jpg"), GraphicsSampler.WrapMode.BACKGROUND);
        HeightMap textMap = (x,y) -> Colors.red(text.get(x,y));

        ColorSpace cs = Colors.HSB_SPACE;
        Sampler<Integer> sampler = new GraphicsCombiner(
                //GraphicsCombiner.intervalAndControl(0, Colors::hue),
                //GraphicsCombiner.colorSpaceDivision(cs),
                Colors.rgbComponentComparator(cs, 2, false),
                /*values -> {
                    for (Integer v : values) {
                        double b = Colors.brightness(v);
                        if (b > 0.10) return v;
                    }
                    return values.get(values.size() - 1);
                },*/
                GraphicsSampler.WrapMode.MIRROR_WRAP, images
        );

        DomainWarp<Integer> warped = new SimpleDomainWarp<>(sampler);
        //warped.domainWarp(GNoise.simplexNoise(0.01, 1.0, 1.0), textMap, 137.315);
        warped.domainWarp(
                //GNoise.simplexNoise(0.001, 1.0, 1.0),
                HeightMaps.constant(1.0 / 2),
                textMap, 0, 300);
        //warped.domainWarp(GNoise.simplexNoise(0.001, 1.0, 1.0), GNoise.simplexNoise(0.01, 1.0, 1.0), 0, 100);

        Sampler<Integer> modified = new PixelModifier(sampler, c -> {
            ColorSpace colorSpace = Colors.HSB_SPACE;
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

            //ArrayTools.swap(components, min, max);
            ArrayAndListTools.swap(components, 0, 2);
            //ArrayTools.swap(components, 1, 2);

            return colorSpace.getRGB(components);
        });

        drawer
                = new SampleDrawer(sampler, bounds.width, bounds.height, new Vector());
                //= new FadingHeightMapDrawer(textMap, 0, 1);

    }

    @Override
    public PGraphics draw(PGraphics canvas, double frequency) {
        canvas.beginDraw();
        drawer.draw(canvas);
        canvas.endDraw();
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
