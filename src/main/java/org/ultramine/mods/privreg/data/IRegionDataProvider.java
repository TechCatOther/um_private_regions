package org.ultramine.mods.privreg.data;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import org.ultramine.mods.privreg.regions.Region;
import org.ultramine.mods.privreg.regions.RegionManager;

import java.util.List;

@SideOnly(Side.SERVER)
public interface IRegionDataProvider
{
	void init(RegionManager regMrg);

	void createRegion(Region region);

	void saveAll(Iterable<Region> regions);

	void saveRegion(Region region);

	void loadAll(List<Region> regions);

	boolean destroyRegion(Region region);

	void close();
}
