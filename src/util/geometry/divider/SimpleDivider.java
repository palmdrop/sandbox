package util.geometry.divider;

import util.geometry.Rectangle;

import java.util.List;

public class SimpleDivider extends AmountDivider implements Divider {
    public SimpleDivider(int vertical, int horizontal, double xMargin, double yMargin) {
        super(xMargin, yMargin);
        double[] v = new double[vertical - 1];
        double[] h = new double[horizontal - 1];

        for(int i = 1; i < vertical; i++) {
            v[i - 1] = (double)i / vertical;
        }
        for(int i = 1; i < horizontal; i++) {
            h[i - 1] = (double)i / horizontal;
        }

        setVertical(v);
        setHorizontal(h);
    }

    public static List<Rectangle> divide(int vertical, int horizontal, double xMargin, double yMargin, Rectangle source) {
        return new SimpleDivider(vertical, horizontal, xMargin, yMargin).divide(source);
    }

}
