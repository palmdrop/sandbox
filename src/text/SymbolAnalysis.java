package text;

import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PVector;
import util.geometry.Rectangle;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class SymbolAnalysis {
    public interface PixelEvaluator {
        double evaluate(int color, int x, int y, int background);
    }

    public interface SymbolEvaluator {
        double evaluate(PGraphics g, Rectangle space, int background);
    }

    public static double evaluateGraphics(PGraphics g, Rectangle space, int background, PixelEvaluator evaluator) {
        double value = 0;

        g.beginDraw();
        g.loadPixels();
        for(int x = (int)space.x; x < (int)(space.x + space.width); x++) {
            for(int y = (int)space.y; y < (int)(space.y + space.height); y++) {
                int color = g.pixels[x + y * g.width];
                value += evaluator.evaluate(color, x, y, background);
            }
        }
        g.endDraw();

        return value;
    }

    public static double totalFill(PGraphics g, Rectangle space, int background) {
        return evaluateGraphics(g, space, background, (color, x, y, bg) -> bg != color ? 1.0 : 0.0);
    }

    public static double normalizedFill(PGraphics g, Rectangle space, int background) {
        int size = (int)(space.width * space.height);
        return evaluateGraphics(g, space, background, (color, x, y, bg) -> bg != color ? 1.0 / size : 0.0);
    }

    public static double orientation(PGraphics g, Rectangle space, int background) {
        return evaluateGraphics(g, space, background, (color, x, y, background1) -> {
            if(color == background) return 0;

            PVector vector = new PVector(x - g.width/2, y - g.height/2);

            float absAng = Math.abs(vector.heading());

            double v; // -1 if horizontal, 1 if vertical
            if(absAng < PApplet.PI / 2) {
                v = PApplet.map(absAng, 0, PApplet.PI/2, -1, 1);
            } else {
                v = PApplet.map(absAng, PApplet.PI/2, PApplet.PI, 1, -1);
            }

            return v;// * util.vector.dist(new PVector(g.width/2, g.height/2));
        });
    }

    private static class CharValue {
        final char c;
        final double value;

        public CharValue(char c, double value) {
            this.c = c;
            this.value = value;
        }
    }

    public static char[] sortBy(char[] symbols, int precision, SymbolEvaluator evaluator, PApplet p) {
        p.textSize(precision);
        Rectangle space = TextTools.getMaxCharacterDimensions(p.g, symbols);

        PGraphics canvas = p.createGraphics((int)space.width, (int)space.height);
        int background = p.color(0);
        int textColor  = p.color(255);

        List<CharValue> charValues = new ArrayList<>(symbols.length);
        for(char c : symbols) {
            canvas.beginDraw();
            canvas.textSize(precision);
            canvas.background(background);

            canvas.fill(textColor);
            Rectangle r = TextTools.renderCentered(canvas, c + "",
                    (float)space.width/2, (float)space.height/2);

            double value = evaluator.evaluate(canvas, r, background);
            charValues.add(new CharValue(c, value));

            canvas.endDraw();
        }

        charValues.sort(new Comparator<>() {
            @Override
            public int compare(CharValue c1, CharValue c2) {
                return Double.compare(c1.value, c2.value);
            }

            @Override
            public boolean equals(Object o) {
                return false;
            }
        });

        char[] sortedSymbols = new char[symbols.length];
        for(int i = 0; i < charValues.size(); i++) {
            sortedSymbols[i] = charValues.get(i).c;
        }

        return sortedSymbols;
    }

}
