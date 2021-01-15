package vision;

import color.colors.Color;
import color.colors.Colors;
import processing.core.PGraphics;
import render.AbstractDrawer;
import util.geometry.Rectangle;
import util.math.MathUtils;
import util.vector.Vector;

import java.io.IOException;
import java.io.Reader;

public class ReaderVision extends AbstractDrawer {
    private final Reader reader;
    private final long length;

    public ReaderVision(Rectangle rectangle, Reader reader, long length) {
        super(rectangle);
        this.reader = reader;
        this.length = length;
    }

    private char next;
    private boolean hasNext() {
        int temp;
        try {
            if((temp = reader.read()) != -1) {
                next = (char)temp;
                return true;
            }
        } catch (IOException ignored) {
        }
        return false;
    }

    private char getNext() {
        return this.next;
    }

    private void lineDraw(PGraphics canvas, double frequency) {
        Vector v1 = new Vector();
        Vector v2 = null;
        int count = 0;

        while(hasNext()) {
            char next = getNext();
            if(count == 0) v1.setX(MathUtils.map(next, 0, 255, 0, bounds.width * 2));
            else if(count == 1) v1.setY(MathUtils.map(next, 0, 255, 0, bounds.height * 2));
            count++;

            if(v2 != null) {
                drawLine(canvas, v1, v2, frequency);
            }

            if(count == 2) {
                v2 = new Vector(v1);
                count = 0;
            }
        }
    }

    private void walkDraw(PGraphics canvas, double frequency) {
        Vector current = new Vector(canvas.width/2, canvas.height/2);

        double angleMultiplier = 2*Math.PI;
        double length = 150;
        int count = 0;

        double a = 0;
        double l = 0;

        double hue = 0;
        double sat = 0;
        while(hasNext()) {
            char next = getNext();

            if(count == 0) {
                hue = next;
                a += MathUtils.map(next, 0, 255, -angleMultiplier, angleMultiplier);
            } else if (count == 1){
                sat = next;
                l = MathUtils.map(next, 0, 255, 0, length);
            }
            count++;

            if(count == 2) {

                hue = MathUtils.map(hue, 0, 255, 0.0, 1.0);
                sat = MathUtils.map(sat, 0, 255, 0.3, 0.8);
                Color c = Colors.HSB_SPACE.getColor(hue, sat, 1);

                canvas.stroke(c.toRGB(), (float) (400/l));
                //canvas.strokeWeight((float) (100/l));

                Vector n = Vector.add(current, Vector.fromAngle(a).mult(l));

                if(n.getX() < 0 || n.getX() >= canvas.width || n.getY() < 0 || n.getY() >= canvas.height) {
                    n.setX(MathUtils.doubleMod(n.getX(), canvas.width));
                    n.setY(MathUtils.doubleMod(n.getY(), canvas.height));
                } else {
                    drawLine(canvas, current, n, frequency);
                }


                current = n;

                count = 0;
            }

            //if((double)chars / this.length > 0.03) break;
        }
    }


    private void drawLine(PGraphics canvas, Vector p1, Vector p2, double frequency) {
        if(p1 == null && p2 != null) canvas.point((float)p2.getX(), (float)p2.getY());
        else if(p2 == null && p1 != null) canvas.point((float)p1.getX(), (float)p1.getY());
        else if(p1 == null) return;

        assert p1 != null;
        assert p2 != null;
        canvas.line((float)p1.getX(), (float)p1.getY(), (float)p2.getX(), (float)p2.getY());
    }

    @Override
    public PGraphics draw(PGraphics canvas, double frequency) {
        canvas.beginDraw();
        canvas.fill(255);
        canvas.stroke(255, 255);
        canvas.strokeWeight(3);

        //lineDraw(canvas, frequency);
        //walkDraw(canvas, frequency);
        //walkDrawPrime(canvas, frequency);
        stepDraw(canvas, frequency);

        canvas.endDraw();
        return canvas;
    }

    private void stepDraw(PGraphics canvas, double frequency) {
        char[] buffer = new char[(int) (length / canvas.width)];

        for(int i = 0; i < canvas.width; i++) {
            double sum = 0;
            for(int j = 0; j < buffer.length; j++) {
                if(!hasNext()) break;
                char next = getNext();
                buffer[j] = (char) (next % 255);

                double x = i;
                double y = MathUtils.map(next, 0, 255, 0, canvas.height);

                //canvas.point((float)x, (float)y);
                Color c = Colors.HSB_SPACE.getColor(MathUtils.map(sum, 0, buffer.length * 255, 2, 0.3), 0.7, 1);

                //double size = buffer[0] / 25.0;
                float alpha = (float) MathUtils.map(buffer[0], 0, 255, 0, 23);

                canvas.fill(c.toRGB(), alpha);
                if(next % 2 == 0) {
                    canvas.stroke(Colors.WHITE, alpha);
                } else {
                    canvas.stroke(Colors.BLACK, alpha);
                }
                //canvas.noStroke();
                //canvas.circle((float)x, (float)y, (float)10);
                double px = x;
                double py = MathUtils.map(buffer[j], 0, 255, 0, canvas.height);

                canvas.circle((float) x, (float) y, (float) (x * y % 25));
                //canvas.line((float)px, (float)py, (float)x, (float)y);

                sum += next % 255;
            }
        }

    }
}
