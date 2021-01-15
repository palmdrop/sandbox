package util.noise;

import java.util.ArrayList;
import java.util.Collections;

public class CombinedNoise implements Noise {
    private final ArrayList<Noise> noises;

    public CombinedNoise(Noise base, Noise... moreNoises) {
        noises = new ArrayList<>(1 + moreNoises.length);
        noises.add(base);
        Collections.addAll(noises, moreNoises);
    }

    public void addNoise(Noise noise) {
        noises.add(noise);
    }

    @Override
    public Double get(double x) {
        double v = 1;
        for(Noise n : noises) {
            v *= n.get(x);
        }
        return v;
    }

    @Override
    public Double get(double x, double y) {
        double v = 1;
        for(Noise n : noises) {
            v *= n.get(x, y);
        }
        return v;
    }

    @Override
    public Double get(double x, double y, double z) {
        double v = 1;
        for(Noise n : noises) {
            v *= n.get(x, y, z);
        }
        return v;
    }
}
