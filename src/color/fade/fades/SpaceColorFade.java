package color.fade.fades;

import color.colors.Color;
import color.fade.ColorFade;
import color.space.ColorSpace;
import sampling.countour.Contour;

import java.util.List;

public class SpaceColorFade implements ColorFade {
    private final ColorSpace colorSpace;
    private final List<Contour> componentContours;

    public SpaceColorFade(ColorSpace colorSpace, Contour... componentContours) {
        this(colorSpace, List.of(componentContours));
    }

    public SpaceColorFade(ColorSpace colorSpace, List<Contour> componentContours) {
        super();
        if(colorSpace.getNumberOfComponents() != componentContours.size()) throw new IllegalArgumentException();
        this.colorSpace = colorSpace;
        this.componentContours = componentContours;
    }

    public Color get(double amount) {
        double[] components = new double[colorSpace.getNumberOfComponents()];
        for(int i = 0; i < colorSpace.getNumberOfComponents(); i++) {
            components[i] = componentContours.get(i).get(amount);
        }
        return colorSpace.getColor(components);
    }
}
