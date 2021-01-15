package text;

import processing.core.PApplet;
import processing.core.PGraphics;
import util.geometry.Rectangle;

public class TextTools {
    public final static char minRenderableAscii = '!';
    public final static char maxRenderableAscii = '~';

    public final static char[] renderableAscii = merge(new char[]{' '}, generateAscii(minRenderableAscii, maxRenderableAscii));

    public final static char[] lowercase = generateAscii('a', 'z');
    public final static char[] uppercase = generateAscii('A', 'Z');
    public final static char[] letters = merge(lowercase, uppercase);

    public final static char[] numbers = generateAscii('0', '9');

    public final static String[] unicode = generateUnicode(0x0000, 0x1FFFF);

    private static String[] generateUnicode(int from, int to) {
        //TODO: unicode might not fit in a char!!! use string instead????
        if(from > to) throw new IllegalArgumentException();

        String[] unicode = new String[to - from + 1];
        for(int i = from; i <= to; i++) {
            String s = String.valueOf(Character.toChars(i));
            unicode[i - from] = s;
        }
        return unicode;
    }

    private static char[] generateAscii(char from, char to) {
        if(from > to) throw new IllegalArgumentException();

        char[] characters = new char[to - from + 1];
        for(char c = from; c <= to; c++) {
            characters[c - from] = c;
        }
        return characters;
    }

    private static char[] merge(char[] a, char[] b) {
        char[] c = new char[a.length + b.length];
        for(int i = 0; i < a.length + b.length; i++) {
            c[i] = i < a.length ? a[i] : b[i - a.length];
        }
        return c;
    }

    public static Rectangle renderCentered(PGraphics g, char[] text, float centerX, float centerY) {
        return renderCentered(g, new String(text), centerX, centerY);
    }

    public static Rectangle renderCentered(PGraphics g, String text, float centerX, float centerY) {
        float textWidth = g.textWidth(text);
        float textAscent = g.textAscent();
        float textDescent = g.textDescent();
        float textHeight = textAscent + textDescent;

        float x = centerX - textWidth / 2;
        float y = centerY - textDescent / 2 + textAscent / 2;

        g.textAlign(PApplet.LEFT);
        g.text(text, x, y);

        return new Rectangle(x, centerY - textHeight/2, textWidth, textHeight);
    }

    public static Rectangle getMaxAsciiDimensions(PGraphics g) {
        return getMaxCharacterDimensions(g, renderableAscii);
    }

    public static Rectangle getMaxCharacterDimensions(PGraphics g, char[] characters) {
        float height = (float) Math.ceil(g.textAscent() + g.textDescent());
        float maxWidth = 0;
        for(char c : characters) {
           maxWidth = (float) Math.ceil(PApplet.max(maxWidth, g.textWidth(c)));
        }
        return new Rectangle(maxWidth * 1.5, height * 1.5);
    }
}
