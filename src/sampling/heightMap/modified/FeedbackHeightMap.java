package sampling.heightMap.modified;

import sampling.domainWarp.FeedbackDomainWarp;
import sampling.heightMap.HeightMap;

public class FeedbackHeightMap extends FeedbackDomainWarp<Double> implements HeightMap {
    public enum Mode {
        AVERAGE,
        MULT,
    }

    public FeedbackHeightMap(WarpedHeightMap source, int times, Mode mode) {
        super(source, times, values -> {
            double value = mode.equals(Mode.AVERAGE) ? 0 : 1;
            for(double v : values) {
                if(mode.equals(Mode.AVERAGE)) {
                    value += v / times;
                } else {
                    value *= v;
                }
            }
            return value;
        });
    }

    @Override
    public Double get(double x, double y) {
        return super.get(x, y);
    }
}
