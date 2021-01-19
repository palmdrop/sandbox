package render.heightMap;

import processing.core.PGraphics;
import render.Drawer;
import sampling.heightMap.HeightMap;
import util.geometry.Rectangle;
import util.math.MathUtils;
import util.vector.Vector;

public class PointHeightMapDrawer implements Drawer {
    private final double xSpacing, ySpacing;
    private final Vector offset;

    private final double minStroke, maxStroke;

    private final double dither;

    private final HeightMap heightMap;

    public PointHeightMapDrawer(HeightMap heightMap, double xSpacing, double ySpacing, double minStroke, double maxStroke, Vector offset, double dither) {
        this.xSpacing = xSpacing;
        this.ySpacing = ySpacing;
        this.offset = offset;
        this.minStroke = minStroke;
        this.maxStroke = maxStroke;
        this.dither = dither;
        this.heightMap = heightMap;
    }

    @Override
    public PGraphics draw(PGraphics canvas, double frequency) {
        //TODO: use frequency somehow???
        canvas.beginDraw();
        canvas.background(255);

        int rows = (int) (canvas.width / xSpacing + 1);
        int column = (int) (canvas.height / ySpacing + 1);

        for(int r = 0; r < rows; r++) {
            for(int c = 0; c < column; c++) {
                Vector p = new Vector(r * xSpacing, c * ySpacing).add(offset);


                float stroke = (float) (0 + MathUtils.random(dither));
                canvas.stroke(stroke);

                double n = heightMap.get(p);
                if(Double.isNaN(n)) {
                    n = 0.0;
                }

                n += MathUtils.random(-dither, dither);
                n = MathUtils.limit(n, 0, 1);

                float sw = (float) MathUtils.map(n, 0, 1, minStroke, maxStroke);
                canvas.strokeWeight(sw);
                canvas.point((float)p.getX(), (float)p.getY());
            }
        }

        canvas.endDraw();
        return null;
    }

    @Override
    public void setBounds(Rectangle bounds) {
        //TODO
    }

    @Override
    public Rectangle getBounds() {
        //TODO
        return null;
    }
}
