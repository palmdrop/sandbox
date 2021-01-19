package text.render;

import color.colors.Color;
import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PGraphics;
import render.AbstractDrawer;
import text.TextTools;
import util.geometry.Rectangle;
import util.math.MathUtils;

public class TextGridDrawer extends AbstractDrawer {
    public interface CharController {
        double getValue(double x, double y);
    }

    public interface ColorController {
        Color getColor(double x, double y, double n);
    }

    private final String fontName;
    private final double padding;
    private final char[] chars;
    private final CharController cc;
    private final ColorController bgc, fgc;

    private final PFont font;
    private final int rows, columns;
    private final double cellSize;

    public TextGridDrawer(PApplet p, Rectangle bounds, String fontName, double fontSize, double padding, char[] chars, CharController cc, ColorController bgc, ColorController fgc) {
        super(bounds);

        this.fontName = fontName;
        this.padding = padding;
        this.chars = chars;
        this.cc = cc;
        this.bgc = bgc;
        this.fgc = fgc;

        font = p.createFont(fontName, (float) fontSize);
        p.textFont(font);
        double w = p.textWidth(chars[0]+"");
        double h = p.textAscent() + p.textDescent();
        cellSize = Math.max(w, h) * (1.0 + padding);

        columns = (int) (bounds.width / cellSize);
        rows = (int) (bounds.height / cellSize);
    }

    @Override
    public PGraphics draw(PGraphics canvas, double frequency) {
        canvas.beginDraw();
        canvas.textFont(font);
        canvas.background(0, 0, 0);
        canvas.noStroke();

        for(int r = 0; r < rows; r++) for(int c = 0; c < columns; c++) {
            double x = c * cellSize + bounds.x;
            double y = r * cellSize + bounds.y;

            double n = cc.getValue(x, y);
            Color fg = fgc.getColor(x, y, n);
            Color bg = bgc.getColor(x, y, n);

            canvas.fill(bg.toRGB());
            canvas.rect((float)x, (float)y, (float)cellSize * 1.1f, (float)cellSize * 1.1f);

            canvas.fill(fg.toRGB());
            int index = (int) MathUtils.map(n, 0, 1, 0, chars.length - 1);
            TextTools.renderCentered(canvas, chars[index] + "", (float)(x + cellSize/2), (float)(y + cellSize/2));
        }

        canvas.endDraw();

        return canvas;
    }
}
