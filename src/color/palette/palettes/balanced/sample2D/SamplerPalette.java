package color.palette.palettes.balanced.sample2D;

import color.colors.Color;
import color.palette.palettes.balanced.BasicPalette;
import sampling.Sampler;
import sampling.Sampler1D;
import util.vector.Vector;

import java.util.List;

public class SamplerPalette extends BasicPalette {
    public SamplerPalette(Sampler<Color> sampler, List<Vector> sourcePoints) {
        for(Vector p : sourcePoints) {
            addColor(sampler.get(p));
        }
    }

    public SamplerPalette(Sampler<Color> sampler, Sampler1D<Vector> sourceSampler, double[] samples) {
        for(double s : samples) {
            addColor(sampler.get(sourceSampler.get(s)));
        }
    }
}
