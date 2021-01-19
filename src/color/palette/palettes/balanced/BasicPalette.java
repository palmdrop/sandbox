package color.palette.palettes.balanced;

import color.colors.Color;
import color.colors.Colors;

import java.util.ArrayList;
import java.util.List;

public class BasicPalette implements BalancedPalette {
    private final List<Color> colors;

    public BasicPalette() {
        this.colors = new ArrayList<>();
    }

    public BasicPalette(int... rgbs) {
        this(Colors.fromRGBs(rgbs));
    }

    public BasicPalette(Color... colors) {
        this.colors = new ArrayList<>();
        this.colors.addAll(List.of(colors));
    }

    public BasicPalette(List<Color> colors) {
        this.colors = new ArrayList<>(colors);
    }

    private boolean inRange(int number) {
        return number >= 0 && number < colors.size();
    }

    @Override
    public int getNumberOfColors() {
        return colors.size();
    }

    @Override
    public Color getColor(int number) {
        if(!inRange(number)) throw new IndexOutOfBoundsException();
        return colors.get(number);
    }

    @Override
    public List<Color> getColors() {
        return colors; //TODO return copy??
    }

    @Override
    public BasicPalette addColor(int index, Color color) {
        if(!inRange(index) && index != colors.size()) throw new IndexOutOfBoundsException();
        colors.add(index, color);
        return this;
    }

    @Override
    public BasicPalette addColor(Color color) {
        colors.add(color);
        return this;
    }

    @Override
    public Color removeColor(int index) {
        if(!inRange(index)) throw new IndexOutOfBoundsException();
        return colors.remove(index);
    }

    @Override
    public boolean removeColor(Color color) {
        return colors.remove(color);
    }
}
