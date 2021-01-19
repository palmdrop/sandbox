package color.palette.palettes.balanced.sample1D;

import color.colors.Color;
import color.palette.palettes.balanced.BalancedPalette;
import color.palette.palettes.balanced.BasicPalette;
import sampling.Sampler1D;

public class Sampler1DPalette extends BasicPalette implements BalancedPalette {
    public Sampler1DPalette(Sampler1D<Color> sampler, double[] samples) {
        if(sampler == null || samples.length == 0) throw new IllegalArgumentException();

        for(double s : samples) {
            if(s < 0.0 || s > 1.0) throw new IllegalArgumentException();
            addColor(sampler.get(s));
        }
    }
}
