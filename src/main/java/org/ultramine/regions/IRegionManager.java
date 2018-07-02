package org.ultramine.regions;

import org.ultramine.mods.privreg.regions.Region;

import java.util.Set;

public interface IRegionManager
{
	Region getRegion(BlockPos point);

	Region getRegion(int x, int y, int z);

	Set<IRegion> getRegionsInRange(Rectangle range);

	boolean hasRegionsInRange(Rectangle range);


}
