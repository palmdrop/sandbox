package color.fade.fades;

import color.colors.Color;
import color.fade.ColorFade;
import color.space.ColorSpace;
import sampling.countour.Contour;

public class ComponentColorFade extends SpaceColorFade implements ColorFade {

    private static Contour[] recalculateContours(ColorSpace colorSpace, Color start, Color end, Contour[] contours) {
        if(colorSpace.getNumberOfComponents() != contours.length) throw new IllegalArgumentException();

        double[] components1 = colorSpace.getComponents(start);
        double[] components2 = colorSpace.getComponents(end);

        Contour[] newContours = new Contour[colorSpace.getNumberOfComponents()];

        for(int i = 0; i < colorSpace.getNumberOfComponents(); i++) {
            newContours[i] =
                contours[i].remap(0, 1, components1[i], components2[i]);
        }

        return newContours;
    }

    public ComponentColorFade(ColorSpace colorSpace, Color start, Color end, Contour... componentFades) {
        super(colorSpace, recalculateContours(colorSpace, start, end, componentFades));
    }
}
