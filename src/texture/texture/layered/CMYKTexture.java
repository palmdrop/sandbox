package texture.texture.layered;

import sampling.heightMap.HeightMap;
import sampling.heightMap.HeightMaps;
import texture.texture.AbstractTexture;
import util.math.MathUtils;
import util.vector.Vector;

public class CMYKTexture extends AbstractTexture {
    private static double[] ANGLES = {
            2 * Math.PI * 15.0 / 360.0,
            2 * Math.PI * 75 / 360.0,
            2 * Math.PI * 0 / 360.0,
            2 * Math.PI * 45 / 360
    };

    private static HeightMap[] getHeightMaps(double circleSize) {
        double randomOffset = MathUtils.random(-Math.PI / 8, Math.PI / 8);

        HeightMap[] heightMaps = new HeightMap[ANGLES.length];
        for(int i = 0; i < heightMaps.length; i++) {
            Vector offset =
                    //new Vector(0, 0);
                    Vector.random(50, 50);

            HeightMap heightMap = HeightMaps.circles(circleSize, circleSize, 0, 0, offset, 1.0);
            heightMaps[i] = heightMap
                    .toDistorted().rotate(new Vector(), ANGLES[i] + randomOffset)
                    .toModded()   .addReverse();
        }

        return heightMaps;
    }

    public CMYKTexture(double circleSize) {
        super(getHeightMaps(circleSize));
    }
}
