package org.ultramine.mods.privreg.gui.inv;

import org.ultramine.mods.privreg.modules.RegionModule;
import org.ultramine.mods.privreg.regions.Region;
import org.ultramine.util.SimpleInventory;

public class ModulesInventory extends SimpleInventory
{
	private final Region region;

	public ModulesInventory(Region region)
	{
		super(15);
		this.region = region;
		int i = 0;
		for(RegionModule mod : region.getModulesStorage())
			this.setInventorySlotContents(i++, mod.toItemStack());
	}

	public Region getRegion()
	{
		return region;
	}
}
