package org.ultramine.mods.privreg.gui.inv;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import org.ultramine.mods.privreg.InitCommon;
import org.ultramine.mods.privreg.RegionConfig;
import org.ultramine.mods.privreg.item.ItemAntimatter;
import org.ultramine.mods.privreg.tiles.TileCharger;

public class ContainerCharger extends Container
{
	private TileCharger.InventoryCharger block;
	private int lastBlockCharge;
	private int lastBlockMaxCharge;

	public ContainerCharger(IInventory player, TileCharger.InventoryCharger block)
	{
		this.block = block;

		addSlotToContainer(new Slot(block, 0, 137, 57));

		//player slots
		for (int j = 0; j < 3; j++)
			for (int i1 = 0; i1 < 9; i1++)
				addSlotToContainer(new Slot(player, i1 + j * 9 + 9, 8 + i1 * 18, 140 + j * 18));

		for (int k = 0; k < 9; k++)
			addSlotToContainer(new Slot(player, k, 8 + k * 18, 198));
	}

	@Override
	public boolean canInteractWith(EntityPlayer entityplayer)
	{
		return block.isUseableByPlayer(entityplayer);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void updateProgressBar(int par1, int par2)
	{
		switch (par1)
		{
			case 0:
				block.charge = par2;
				break;
			case 1:
				block.maxCharge = par2;
				break;
//			case 2:
//				block.price = (float) par2 / 100;
//				break;
		}
	}

	@Override
	public void addCraftingToCrafters(ICrafting player)
	{
		super.addCraftingToCrafters(player);
		player.sendProgressBarUpdate(this, 0, block.charge);
		player.sendProgressBarUpdate(this, 1, block.maxCharge);
//		player.sendProgressBarUpdate(this, 2, (int) (block.te.price * 100));
	}

	@Override
	public void detectAndSendChanges()
	{
		super.detectAndSendChanges();

		for (Object crafter : this.crafters)
		{
			ICrafting var2 = (ICrafting) crafter;
			if (lastBlockCharge != block.charge)
			{
				var2.sendProgressBarUpdate(this, 0, block.charge);
				lastBlockCharge = block.charge;
			}
			if (lastBlockMaxCharge != block.maxCharge)
			{
				var2.sendProgressBarUpdate(this, 1, block.maxCharge);
				lastBlockMaxCharge = block.maxCharge;
			}
		}

		ItemStack is = block.getStackInSlot(0);
		if(is != null && is.getItem() == InitCommon.antimatter && block.charge > 0)
			block.charge = ItemAntimatter.charge(is, block.charge, block.maxCharge / 1000 + 1);
		if(block.charge == 0)
			block.maxCharge = RegionConfig.ChargerMaxCharge;
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer par1EntityPlayer, int i)
	{
		ItemStack var2 = null;
		Slot var3 = (Slot) this.inventorySlots.get(i);

		if (var3 != null && var3.getHasStack())
		{
			ItemStack var4 = var3.getStack();
			var2 = var4.copy();

			if (i < 1)
			{
				if (!this.mergeItemStack(var4, 1, this.inventorySlots.size(), true))
				{
					return null;
				}
			} else if (!this.mergeItemStack(var4, 0, 1, false))
			{
				return null;
			}

			if (var4.stackSize == 0)
			{
				var3.putStack((ItemStack) null);
			} else
			{
				var3.onSlotChanged();
			}
		}

		return var2;
	}

	@Override
	public void onContainerClosed(EntityPlayer player)
	{
		super.onContainerClosed(player);
		ItemStack is = block.getStackInSlotOnClosing(0);
		if(is != null)
			player.dropPlayerItemWithRandomChoice(is, false);
	}
}
