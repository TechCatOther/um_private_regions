package org.ultramine.util;

import org.ultramine.server.util.InventoryUtil;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class SimpleInventory implements IInventory
{
	protected ItemStack[] inventory;

	public SimpleInventory(int size)
	{
		inventory = new ItemStack[size];
	}

	@Override
	public int getSizeInventory()
	{
		return inventory.length;
	}

	@Override
	public ItemStack getStackInSlot(int slot)
	{
		return inventory[slot];
	}

	@Override
	public ItemStack decrStackSize(int slot, int size)
	{
		ItemStack is = inventory[slot];
		if(is != null)
		{
			if(is.stackSize <= size)
			{
				inventory[slot] = null;
				return is;
			}

			ItemStack itemstack1 = is.splitStack(size);

			if(is.stackSize == 0)
				inventory[slot] = null;

			return itemstack1;
		}
		else
		{
			return null;
		}
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int slot)
	{
		ItemStack item = inventory[slot];
		if(item != null)
		{
			inventory[slot] = null;
			return item;
		}
		else
		{
			return null;
		}
	}

	@Override
	public void setInventorySlotContents(int slot, ItemStack is)
	{
		inventory[slot] = is;
	}

	@Override
	public String getInventoryName()
	{
		return "SimpleInventory";
	}

	@Override
	public int getInventoryStackLimit()
	{
		return 64;
	}

	public void readFromNBT(NBTTagCompound nbt)
	{
		InventoryUtil.readInventoryFromNBT(inventory, nbt.getTagList("inv", 10));
	}

	public void writeToNBT(NBTTagCompound nbt)
	{
		nbt.setTag("inv", InventoryUtil.writeInventorytoNBT(inventory));
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer player)
	{
		return true;
	}

	@Override
	public boolean hasCustomInventoryName()
	{
		return false;
	}

	@Override
	public void markDirty()
	{
		
	}

	@Override
	public void openInventory()
	{
		
	}

	@Override
	public void closeInventory()
	{
		
	}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack is)
	{
		return false;
	}
	
	public ItemStack[] getContents()
	{
		return inventory;
	}
}
