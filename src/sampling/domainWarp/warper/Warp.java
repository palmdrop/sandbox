package sampling.domainWarp.warper;

import util.vector.Vector;

import java.util.ArrayList;
import java.util.List;

public class Warp implements AddWarper {
    private List<Warper> warpers = new ArrayList<>();

    public Warp() {
    }

    public Warp(Warper warper) {
        if(warper == null) throw new IllegalArgumentException();
        warpers.add(warper);
    }

    public Warp(Warper base, Warper... rest) {
        warpers.add(base);
        warpers.addAll(List.of(rest));
    }

    @Override
    public Vector warp(Vector point) {
        for(Warper warper : warpers) {
            point = warper.warp(point);
        }
        return point;
    }

    @Override
    public Warp add(Warper warper) {
        if(warper == null) throw new IllegalArgumentException();
        warpers.add(warper);
        return this;
    }

    public static Warp add(Warper warper, Warper... warpers) {
        return new Warp(warper, warpers);
    }
}
