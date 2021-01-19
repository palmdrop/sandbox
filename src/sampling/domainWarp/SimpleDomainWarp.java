package sampling.domainWarp;

import sampling.Sampler;
import sampling.domainWarp.warper.AddWarper;
import sampling.domainWarp.warper.Warp;
import sampling.domainWarp.warper.Warper;
import util.vector.Vector;

public class SimpleDomainWarp<V> implements DomainWarp<V> {
    private final Sampler<V> sampler;
    private final AddWarper warp;

    public SimpleDomainWarp(Sampler<V> sampler) {
        this.sampler = sampler;
        warp = new Warp();
    }

    public SimpleDomainWarp(Sampler<V> sampler, AddWarper warp) {
        this.sampler = sampler;
        this.warp = warp;
    }

    public V sample(Vector point) {
        return sampler.get(point);
    }

    @Override
    public V get(Vector point) {
        return sampler.get(warp(point));
    }

    @Override
    public SimpleDomainWarp<V> add(Warper warper) {
        this.warp.add(warper);
        return this;
    }

    @Override
    public Vector warp(Vector point) {
        return warp.warp(point);
    }
}
