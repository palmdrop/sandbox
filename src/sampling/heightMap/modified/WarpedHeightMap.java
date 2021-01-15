package sampling.heightMap.modified;

import sampling.Sampler;
import sampling.domainWarp.SimpleDomainWarp;
import sampling.domainWarp.warper.AddWarper;
import sampling.domainWarp.warper.Warp;
import sampling.heightMap.HeightMap;
import util.vector.Vector;

public class WarpedHeightMap extends SimpleDomainWarp<Double> implements HeightMap /*, DomainWarp<Double, WarpedHeightMap>*/ {
    public WarpedHeightMap(Sampler<Double> heightMap, AddWarper warper) {
        super(heightMap, warper);
    }

    public WarpedHeightMap(Sampler<Double> heightMap) {
        this(heightMap, new Warp());
    }

    @Override
    public Double get(Vector p) {
        return sample(warp(p));
    }

    @Override
    public Double get(double x, double y) {
        return sample(warp(x, y));
    }

    @Override
    public WarpedHeightMap rotate(Vector center, double amount, double distPow) {
        super.rotate(center, amount, distPow);
        return this;
    }

    @Override
    public WarpedHeightMap rotate(Vector center, double angle) {
        super.rotate(center, angle);
        return this;
    }

    @Override
    public WarpedHeightMap zoom(Vector center, double amount, double distPow) {
        super.zoom(center, amount, distPow);
        return this;
    }

    @Override
    public WarpedHeightMap trigFunc(double offsetAngle, double xMultiplier, double yMultiplier, double amplitude, TrigFunc trigFunc) {
        super.trigFunc(offsetAngle, xMultiplier, yMultiplier, amplitude, trigFunc);
        return this;
    }

    @Override
    public WarpedHeightMap sin(double offsetAngle, double xMultiplier, double yMultiplier, double amplitude) {
        super.sin(offsetAngle, xMultiplier, yMultiplier, amplitude);
        return this;
    }

    @Override
    public WarpedHeightMap cos(double offsetAngle, double xMultiplier, double yMultiplier, double amplitude) {
        super.cos(offsetAngle, xMultiplier, yMultiplier, amplitude);
        return this;
    }

    @Override
    public WarpedHeightMap domainWarp(Sampler<Double> rotation, double offset) {
        super.domainWarp(rotation, offset);
        return this;
    }

    @Override
    public WarpedHeightMap domainWarp(Sampler<Double> rotation, Sampler<Double> offset, double amount) {
        super.domainWarp(rotation, offset, amount);
        return this;
    }

    @Override
    public WarpedHeightMap domainWarp(Sampler<Double> rotation, Sampler<Double> offset, double min, double max) {
        super.domainWarp(rotation, offset, min, max);
        return this;
    }

    @Override
    public WarpedHeightMap frequencyModulation(HeightMap modulation, double min, double max) {
        super.frequencyModulation(modulation, min, max);
        return this;
    }

    public FeedbackHeightMap toFeedbackHeightMap(int times, FeedbackHeightMap.Mode mode) {
        return new FeedbackHeightMap(this, times, mode);
    }

    @Override
    public Vector warp(double x, double y) {
        return super.warp(x, y);
    }
}
