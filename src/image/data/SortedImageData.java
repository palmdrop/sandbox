package image.data;

import color.colors.Colors;
import util.ListIndexSort;

import java.util.Comparator;
import java.util.List;

public class SortedImageData extends ListIndexSort<ImageData> {
    public SortedImageData(List<ImageData> data) {
        super(data);
    }

    public List<Integer> sortDominantColorHue() {
        return addSort("dominantColorHue",
                (d1, d2) -> (int) (255.0 * (Colors.hue(d2.getColors().get(0)) - Colors.hue(d1.getColors().get(0))))
        );
    }
    public List<Integer> sortDominantColorSaturation() {
        return addSort("dominantColorSaturation",
                (d1, d2) -> (int) (255.0 * (Colors.saturation(d2.getColors().get(0)) - Colors.saturation(d1.getColors().get(0))))
        );
    }
    public List<Integer> sortDominantColorBrightness() {
        return addSort("dominantColorBrightness",
                (d1, d2) -> (int) (255.0 * (Colors.brightness(d2.getColors().get(0)) - Colors.brightness(d1.getColors().get(0))))
        );
    }

    public List<Integer> sortHue() {
        return addSort("hue", Comparator.comparingDouble(d -> d.getHsbHistogram().getComponentNormalizedSum(0)));
    }

    public List<Integer> sortSaturation() {
        return addSort("saturation", Comparator.comparingDouble(d -> d.getHsbHistogram().getComponentNormalizedSum(1)));
    }

    public List<Integer> sortBrightness() {
        return addSort("brightness", Comparator.comparingDouble(d -> d.getHsbHistogram().getComponentNormalizedSum(2)));
    }

    public List<Integer> sortRandom() {
        return addSort("random", (d1, d2) -> Double.compare(0.5, Math.random()));
    }
}
