package util.geometry;

import processing.core.PGraphics;
import util.space.region.Region;
import util.vector.Vector;

public class Rectangle implements Region {
    public final double x, y;
    public final double width, height;

    public Rectangle(double width, double height) {
        this(0, 0, width, height);
    }

    public Rectangle(double x, double y, double width, double height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public static void render(PGraphics g, Rectangle rectangle) {
        float x = //(float) rectangle.x;
                (float) Math.floor(rectangle.x);
        float y = //(float) rectangle.y;
                (float) Math.floor(rectangle.y);
        float width =
                //(float) rectangle.width;
                (float) Math.ceil(rectangle.width);
        float height =
                //(float) rectangle.height;
                (float) Math.ceil(rectangle.height);
        //TODO: fix hack solution!?!?!?!


        g.rect(x, y, width, height);
    }

    @Override
    public boolean isInside(double x, double y) {
        return (x >= this.x) && (x < this.x + width) &&
               (y >= this.y) && (y < this.y + height);
    }

    public double getX() { return x; }
    public double getY() { return y; }
    public double getWidth() { return width; }
    public double getHeight() { return height; }

    public Vector getCenter() {
        return new Vector(x + width/2, y + height/2);
    }

    @Override
    public String toString() {
        return "x=" + x + ", y=" + y + ", width=" + width + ", height=" + height;
    }

    public static Rectangle range(double xMin, double xMax, double yMin, double yMax) {
        return new Rectangle(xMin, yMin, xMax - xMin, yMax - yMin);
    }
}
