package render;

import color.colors.Colors;
import processing.core.PGraphics;
import util.geometry.Rectangle;
import util.math.MathUtils;
import util.vector.Vector;

public abstract class AbstractPixelDrawer extends AbstractDrawer implements Drawer {
    protected Vector offset;
    protected double xMultiplier;
    protected double yMultiplier;

    protected double blur;
    protected boolean blend;

    protected boolean superSampling;

    public AbstractPixelDrawer(Rectangle bounds) {
        this(bounds, new Vector(), 1.0, 1.0, 0.0);
    }

    public AbstractPixelDrawer(Rectangle bounds, Vector offset, double xMultiplier, double yMultiplier, double blur) {
        super(bounds);
        this.offset = offset;
        this.xMultiplier = xMultiplier;
        this.yMultiplier = yMultiplier;
        this.blur = blur;
        this.blend = false;
        this.superSampling = false;
    }

    protected abstract int getColor(Vector p);

    @Override
    public PGraphics draw(PGraphics canvas, double frequency) {
        Rectangle bounds = getBounds() == null ? new Rectangle(canvas.width, canvas.height) : this.bounds;

        canvas.loadPixels();

        int maxX = (int) Math.min(bounds.getX() + bounds.getWidth(), canvas.width);
        int maxY = (int) Math.min(bounds.getY() + bounds.getHeight(), canvas.height);

        for(int x = (int) bounds.getX(); x < maxX; x++) {
            for(int y = (int) bounds.getY(); y < maxY; y++) {
                double dx = MathUtils.map(x, bounds.getX(), bounds.getX() + bounds.getWidth(), 0, xMultiplier);
                double dy = MathUtils.map(y, bounds.getY(), bounds.getY() + bounds.getHeight(), 0, yMultiplier);

                int color;
                if(superSampling) {
                    //Color c = Colors.RGB_SPACE.getColor(0, 0, 0);
                    double r = 0, g = 0, b = 0;

                    int samples = 4;
                    double offset = 0.06;
                    for (int i = 0; i < samples; i++) {
                        double sx = 0, sy = 0;
                        switch (i) {
                            case 0:
                                sx = 0.25 - offset;
                                sy = 0.25 + offset;
                                break;
                            case 1:
                                sx = 0.75 - offset;
                                sy = 0.25 - offset;
                                break;
                            case 2:
                                sx = 0.75 + offset;
                                sy = 0.75 - offset;
                                break;
                            case 3:
                                sx = 0.25 + offset;
                                sy = 0.75 + offset;
                                break;
                        }
                        //n += heightMap.get(nx + sx * frequency, ny + sy * frequency);
                        Vector p = new Vector((dx + sx) * frequency + this.offset.getX(), (dy + sy) * frequency + this.offset.getY());
                        color = getColor(p);
                        r += Colors.red(color);
                        g += Colors.green(color);
                        b += Colors.blue(color);
                        //c = Colors.add(c, Colors.fromRGB(getColor(p)), Colors.RGB_SPACE);
                    }
                    //n /= samples;
                    r /= samples;
                    g /= samples;
                    b /= samples;
                    //c = Colors.div(c, 4, Colors.RGB_SPACE);
                    color = Colors.RGB_SPACE.getColor(r, g, b).toRGB();
                } else {
                    Vector p = new Vector(dx * frequency + offset.getX(), dy * frequency + offset.getY());
                    //n += heightMap.get(nx, ny);
                    color = getColor(p);
                }

                //int color = getColor(p);
                if(blend) {
                    int previous = canvas.pixels[x + y * canvas.width];
                    color = Colors.blend(previous, color);
                }
                canvas.pixels[x + y * canvas.width] = color;
            }
            System.out.println(100 * x / canvas.width + "%");
        }
        canvas.updatePixels();

        if(blur > 0.0) canvas.filter(PGraphics.BLUR, (float) blur);

        return canvas;
    }

    public void blend(boolean blend) {
        this.blend = blend;
    }

    @Override
    public Rectangle getBounds() {
        return bounds;
    }

    @Override
    public void setBounds(Rectangle bounds) {
        this.bounds = bounds;
    }

    public void setSuperSampling(boolean superSampling) {
        this.superSampling = superSampling;
    }
}
