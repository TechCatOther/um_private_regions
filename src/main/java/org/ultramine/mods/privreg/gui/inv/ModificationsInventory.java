package org.ultramine.mods.privreg.gui.inv;

import net.minecraft.item.ItemStack;
import org.ultramine.mods.privreg.modifications.RegionModification;
import org.ultramine.mods.privreg.regions.Region;
import org.ultramine.util.SimpleInventory;

public class ModificationsInventory extends SimpleInventory
{
	private final Region region;

	public ModificationsInventory(Region region)
	{
		super(15);
		this.region = region;
		int i = 0;
		for(RegionModification mod : region.getModifications())
			this.setInventorySlotContents(i++, mod.toItemStack());
	}

	public Region getRegion()
	{
		return region;
	}

	@Override
	public void markDirty()
	{
		region.getModifications().clear();
		for(ItemStack is : inventory)
		{
			if(is != null)
			{
				region.getModifications().add(RegionModification.wrapItemStack(is));
			}
		}
		region.recountModifications();
		region.setChanged(true);
	}
}
