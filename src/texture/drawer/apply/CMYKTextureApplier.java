package texture.drawer.apply;

import color.colors.Colors;
import texture.Texture;
import texture.texture.layered.CMYKTexture;
import util.geometry.Rectangle;
import util.math.MathUtils;
import util.vector.Vector;

import java.util.stream.DoubleStream;

public class CMYKTextureApplier extends AbstractTextureApplier {
    private final double smooth;

    public CMYKTextureApplier(double size, double smooth, double blur, Rectangle bounds) {
        this(new CMYKTexture(size), smooth, blur, bounds);
    }

    public CMYKTextureApplier(Texture texture, double smooth, double blur, Rectangle bounds) {
        super(texture, bounds, blur);
        this.smooth = smooth;
    }

    @Override
    protected int getColor(Vector p, int currentColor) {
        double[] textureComponents = texture.getComponents(p);
        double[] colorComponents = Colors.CMY_SPACE.getComponents(currentColor);

        double dv = DoubleStream.of(colorComponents).sum() / 3;
        double tdv = textureComponents[3];
        double darknessFade = 0.0;

        if(dv > tdv) {
            if((dv - tdv) < smooth) {
                darknessFade = MathUtils.map((dv - tdv), 0, smooth, 0.0, 1.0);
            } else {
                darknessFade = 1.0;
            }
        }

        for(int i = 0; i < Colors.CMY_SPACE.getNumberOfComponents(); i++) {
            double tv = textureComponents[i];
            double cv = colorComponents[i];

            double v = 0.0;

            if(cv > tv) {
                if ((cv - tv) < smooth) {
                    v = MathUtils.map((cv - tv), 0, smooth, 0.0, 1.0);
                } else {
                    v = 1.0;
                }
            }
            double ft = v;
            if(dv == 1.0) ft = 0.0;

            v = MathUtils.map(darknessFade, 0, 1, ft, 1.0);


            colorComponents[i] = v;
        }

        return Colors.CMY_SPACE.getRGB(colorComponents);
    }
}
