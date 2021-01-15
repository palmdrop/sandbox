package util.geometry.divider;

import util.geometry.Rectangle;

import java.util.ArrayList;
import java.util.List;

public class AmountDivider implements Divider {
    private double[] vertical;
    private double[] horizontal;

    private double xMargin, yMargin;

    public AmountDivider(double xMargin, double yMargin) {
        this(null, null, xMargin, yMargin);
    }

    public AmountDivider(double[] vertical, double[] horizontal, double xMargin, double yMargin) {
        setVertical(vertical);
        setHorizontal(horizontal);
        if(!valid(this.vertical) || !valid(this.horizontal)) throw new IllegalArgumentException();

        this.xMargin = xMargin;
        this.yMargin = yMargin;
    }

    private boolean valid(double[] arr) {
        double previous = 0.0;
        for(double d : arr) {
            if(d > 1.0 || previous > d) return false;
            previous = d;
        }
        return true;
    }

    @Override
    public List<Rectangle> divide(Rectangle source) {
        List<Rectangle> divisions = new ArrayList<>((vertical.length + 1) * (horizontal.length + 1));

        double pV = 0.0;
        double last = 1.0;

        //TODO: still issue! small space between divisions!
        for(int v = 0; v <= vertical.length; v++) {
            double cV = v < vertical.length ? vertical[v] : last;
            double width = (cV - pV) * source.getWidth() - xMargin;
            double x = pV * source.getWidth() + xMargin/2.0 + source.getX();

            double pH = 0.0;
            for(int h = 0; h <= horizontal.length; h++) {
                double cH = h < horizontal.length ? horizontal[h] : last;
                double height = (cH - pH) * source.getHeight() - yMargin;
                double y = pH * source.getHeight() + yMargin/2.0 + source.getY();

                Rectangle div = new Rectangle(x, y, width, height);
                divisions.add(div);

                pH = cH;
            }
            pV = cV;
        }
        return divisions;
    }

    public void setVertical(double... vertical) {
        this.vertical = nullToEmpty(vertical);
    }
    public void setHorizontal(double... horizontal) {
        this.horizontal = nullToEmpty(horizontal);
    }

    private double[] nullToEmpty(double[] arr) {
        if(arr == null) return new double[0];
        else return arr;
    }

    public void setMargin(double xMargin, double yMargin) {
        this.xMargin = xMargin;
        this.yMargin = yMargin;
    }
}
