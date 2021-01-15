package sampling.domainWarp;

import sampling.domainWarp.warper.Warper;
import util.vector.Vector;

import java.util.ArrayList;
import java.util.List;

public class FeedbackDomainWarp<V> implements DomainWarp<V> {
    public interface CombinationProtocol<V> {
        V combine(List<V> values);
    }

    private final DomainWarp<V> warpedDomain;
    private final int times;
    private CombinationProtocol<V> combinationProtocol;

    public FeedbackDomainWarp(DomainWarp<V> warpedDomain, int times, CombinationProtocol<V> combinationProtocol) {
        if(times < 1) throw new IllegalArgumentException();
        this.warpedDomain = warpedDomain;
        this.times = times;
        this.combinationProtocol = combinationProtocol;
    }

    @Override
    public V get(Vector point) {
        List<V> values = new ArrayList<>(times);
        for(int i = 0; i < times; i++) {
            values.add(warpedDomain.get(point));
            point = warpedDomain.warp(point);
        }
        return combinationProtocol.combine(values);
    }

    @Override
    public Vector warp(Vector point) {
        for(int i = 0; i < times; i++) {
            point = warpedDomain.warp(point);
        }
        return point;
    }

    @Override
    public FeedbackDomainWarp<V> add(Warper warper) {
        warpedDomain.add(warper);
        return this;
    }
}
