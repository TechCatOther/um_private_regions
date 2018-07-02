package org.ultramine.mods.privreg.gui.inv;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import org.ultramine.mods.privreg.item.ItemRegionModification;
import org.ultramine.mods.privreg.regions.Region;

public class SlotModification extends Slot
{
	private final ModificationsInventory inv;

	public SlotModification(ModificationsInventory inv, int index, int x, int y)
	{
		super(inv, index, x, y);
		this.inv = inv;
	}

	@Override
	public boolean isItemValid(ItemStack itemstack)
	{
		return itemstack.getItem() instanceof ItemRegionModification;
	}

	@Override
	public boolean canTakeStack(EntityPlayer player)
	{
		Region region = inv.getRegion();
		ItemStack is = getStack();
		if(is == null)
			return true;
		putStack(null);
		boolean ret = region.getCharge() <= region.getMaxCharge() && region.getTacts() <= region.getMaxTacts();
		putStack(is);
		return ret;
	}
}
