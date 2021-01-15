package sampling.heightMap.modified;

import sampling.heightMap.HeightMap;
import util.space.region.Region;

import java.util.ArrayList;
import java.util.List;

public class RegionCombinedHeightMap implements HeightMap {

    public static class Builder {
        private List<HeightMap> heightMaps;
        private List<Region> regions;

        public Builder(HeightMap base) {
            heightMaps = new ArrayList<>();
            regions = new ArrayList<>();

            heightMaps.add(base);
            regions.add((x, y) -> true);
        }

        public Builder add(HeightMap heightMap, Region region) {
            heightMaps.add(heightMap);
            regions.add(region);
            return this;
        }

        public RegionCombinedHeightMap build() {
            return new RegionCombinedHeightMap(heightMaps, regions);
        }

    }

    private final List<HeightMap> heightMaps;
    private final List<Region> regions;

    private RegionCombinedHeightMap() {
        heightMaps = new ArrayList<>();
        regions = new ArrayList<>();
    }

    private RegionCombinedHeightMap(List<HeightMap> heightMaps, List<Region> regions) {
        this.heightMaps = heightMaps;
        this.regions = regions;
    }

    @Override
    public Double get(double x, double y) {
        for(int i = heightMaps.size() - 1; i >= 0; i--) {
            if(regions.get(i).isInside(x, y)) {
                return heightMaps.get(i).get(x, y);
            }
        }
        return 0.0;
    }
}
