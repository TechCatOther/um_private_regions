package org.ultramine.mods.privreg.gui.inv;

import net.minecraft.block.Block;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class SlotCustom extends Slot
{
	private Object[] items;

	public SlotCustom(IInventory iinventory, int i, int j, int k, Object... items)
	{
		super(iinventory, i, j, k);
		this.items = items;
	}

	public boolean isItemValid(ItemStack is)
	{
		int i = items.length;
		for(int j = 0; j < i; j++)
		{
			Object obj = items[j];
			if(obj != null)
				if(obj instanceof Class)
				{
                    if(is.getItem() instanceof ItemBlock)
                        return ((Class<?>)obj).isAssignableFrom(Block.getBlockFromItem(is.getItem()).getClass());
					else
                        return ((Class<?>)obj).isAssignableFrom(is.getItem().getClass());
				} else
				if(obj instanceof ItemStack)
				{
					if(is.getItemDamage() == -1 && is.getItem() == ((ItemStack)obj).getItem())
						return true;
					if(is.isItemEqual((ItemStack)obj))
						return true;
				}
				else if((obj instanceof Block) && is.getItem() == Item.getItemFromBlock((Block)obj))
					return true;
                else if((obj instanceof Item) && is.getItem() == obj)
				    return true;
		}

		return false;
	}
}
