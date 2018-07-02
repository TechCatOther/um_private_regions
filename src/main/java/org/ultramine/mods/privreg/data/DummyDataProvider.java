package org.ultramine.mods.privreg.data;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import org.ultramine.mods.privreg.regions.Region;
import org.ultramine.mods.privreg.regions.RegionManager;

import java.util.List;

@SideOnly(Side.SERVER)
public class DummyDataProvider implements IRegionDataProvider
{
	@Override
	public void init(RegionManager regMrg)
	{

	}

	@Override
	public void createRegion(Region region)
	{

	}

	@Override
	public void saveAll(Iterable<Region> regions)
	{

	}

	@Override
	public void saveRegion(Region region)
	{

	}

	@Override
	public void loadAll(List<Region> regions)
	{

	}

	@Override
	public boolean destroyRegion(Region region)
	{
		return false;
	}

	@Override
	public void close()
	{

	}
}
