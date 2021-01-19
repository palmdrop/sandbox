package sampling;

import color.colors.Colors;
import processing.core.PImage;
import util.math.MathUtils;
import util.vector.Vector;

public class GraphicsSampler implements Sampler<Integer> {

    public enum WrapMode {
        BACKGROUND,  // Fallback background color
        CLAMP,       // Clamp to edge
        ANGLE,       // Sample from edge based on angle
        WRAP,        // Simple wrap
        MIRROR_WRAP, // Mirrored wrap
    }

    private final PImage image;
    private final double frequency;
    private final WrapMode wrapMode;
    private int background;

    private final Vector offset;

    public GraphicsSampler(PImage image) {
        this(image, 1.0, WrapMode.MIRROR_WRAP);
    }

    public GraphicsSampler(PImage image, WrapMode wrapMode) {
        this(image, 1.0, wrapMode);
    }

    public GraphicsSampler(PImage image, double frequency, WrapMode wrapMode) {
        this(image, frequency, new Vector(), wrapMode);
    }

    public GraphicsSampler(PImage image, double frequency, Vector offset, WrapMode wrapMode) {
        this.image = image;
        this.frequency = frequency;
        this.wrapMode = wrapMode;
        background = Colors.RGB_SPACE.getColor(0, 0, 0).toRGB(); // Black
        image.loadPixels();

        this.offset = offset;
    }


    private boolean withinBounds(Vector point) {
        return point.getX() >= 0 && point.getX() < image.width &&
               point.getY() >= 0 && point.getY() < image.height;
    }

    private Integer directSample(Vector point) {
        return image.pixels[(int)point.getX() + (int)point.getY() * image.width];
    }

    private Vector clamp(Vector point) {
        double x = MathUtils.limit(point.getX(), 0, image.width - 1);
        double y = MathUtils.limit(point.getY(), 0, image.height - 1);
        return new Vector(x, y);
    }

    private Vector angle(Vector point) {
        //TODO
        return point;
    }

    private Vector wrap(Vector point) {
        double x = MathUtils.doubleMod(point.getX(), image.width);
        double y = MathUtils.doubleMod(point.getY(), image.height);
        return new Vector(x, y);
    }

    private Vector mirrorWrap(Vector point) {
        double x = point.getX();
        double y = point.getY();

        int xC = (int) (x / image.width);
        int yC = (int) (y / image.height);

        if(x < 0) xC = -xC + 1;
        if(y < 0) yC = -yC + 1;

        x = MathUtils.doubleMod(x, image.width);
        y = MathUtils.doubleMod(y, image.height);

        if(xC % 2 == 1) {
            x = image.width - x - 0.5;
        }
        if(yC % 2 == 1) {
            y = image.height - y - 0.5;
        }
        return clamp(new Vector(x, y));
    }

    private Integer indirectSample(Vector point) {
        switch (wrapMode) {
            case BACKGROUND:
                return background;
            case CLAMP:
                point = clamp(point);
                break;
            case ANGLE:
                point = angle(point);
                break;
            case WRAP:
                point = wrap(point);
                break;
            case MIRROR_WRAP:
                point = mirrorWrap(point);
                break;
        };
        return directSample(point);
    }

    @Override
    public Integer get(Vector point) {
        Vector translatedPoint = Vector.add(point, offset).mult(frequency);
        if(withinBounds(translatedPoint)) {
            return directSample(translatedPoint);
        } else {
            return indirectSample(translatedPoint);
        }
    }

    public int getBackground() {
        return background;
    }

    public void setBackground(int background) {
        this.background = background;
    }

    public PImage getImage() {
        return image;
    }
}
