package color.colors;

import java.io.Serializable;

public interface Color extends Serializable {
    int toRGB();

    default String toHex() {
        int r = (int) (255 * Colors.red(this));
        int g = (int) (255 * Colors.green(this));
        int b = (int) (255 * Colors.blue(this));

        return String.format("#%02X%02X%02X", r, g, b);
    }
}
