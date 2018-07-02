package org.ultramine.util;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerChest extends Container
{
	private IInventory block;

	public ContainerChest(IInventory player, IInventory block)
	{
		this.block = block;

		for (int j = 0, s = block.getSizeInventory()/9; j < s; j++)
		{
			for (int i1 = 0; i1 < 9; i1++)
			{
				addSlotToContainer(new Slot((IInventory)block, i1 + j * 9, 8 + i1 * 18, 18 + j * 18));
			}
		}
		int var3 = (block.getSizeInventory()/9 - 4) * 18;
		for (int j = 0; j < 3; j++)
		{
			for (int i1 = 0; i1 < 9; i1++)
			{
				addSlotToContainer(new Slot(player, i1 + j * 9 + 9, 8 + i1 * 18, 104 + j * 18 + var3));
			}
		}

		for (int k = 0; k < 9; k++)
		{
			addSlotToContainer(new Slot(player, k, 8 + k * 18, 162 + var3));
		}
	}
	public boolean canInteractWith(EntityPlayer entityplayer)
	{
		return block.isUseableByPlayer(entityplayer);
	}
	
	public ItemStack transferStackInSlot(EntityPlayer entity, int i)
	{
		ItemStack var2 = null;
		Slot var3 = (Slot)this.inventorySlots.get(i);

		if (var3 != null && var3.getHasStack())
		{
			ItemStack var4 = var3.getStack();
			var2 = var4.copy();

			if (i < 54)
			{
				if (!this.mergeItemStack(var4, block.getSizeInventory(), this.inventorySlots.size(), true))
				{
					return null;
				}
			}
			else if (!this.mergeItemStack(var4, 0, block.getSizeInventory(), false))
			{
				return null;
			}

			if (var4.stackSize == 0)
			{
				var3.putStack((ItemStack)null);
			}
			else
			{
				var3.onSlotChanged();
			}
		}

		return var2;
	}
}
