package org.ultramine.mods.privreg.gui.inv;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.ultramine.mods.privreg.InitCommon;
import org.ultramine.mods.privreg.item.ItemAntimatter;
import org.ultramine.mods.privreg.item.ItemRegionModification;
import org.ultramine.mods.privreg.packets.PacketIntWindowProperty;
import org.ultramine.mods.privreg.regions.Region;
import org.ultramine.mods.privreg.regions.RegionRights;
import org.ultramine.util.SimpleInventory;

public class ContainerRegionModules extends Container
{
	private final Region region;
	private final SimpleInventory chargeInv = new SimpleInventory(1);
	private final ModificationsInventory modInv;
	private int lastBlockCharge = -1;
	private int lastBlockMaxCharge = -1;

	public ContainerRegionModules(IInventory player, Region region)
	{
		this.region = region;
		this.modInv = new ModificationsInventory(region);

		//battery
		addSlotToContainer(new SlotCustom(chargeInv, 0, 30, 110, ItemAntimatter.class));

		//top
		ModulesInventory modinv = new ModulesInventory(region);
		for(int i = 0; i < 3; i++)
			for(int j = 0; j < 5; j++)
				addSlotToContainer(new SlotModule(modinv, j + i * 5, 80 + j * 18, 16 + i * 18));

		//bottom
		for(int i = 0; i < 3; i++)
			for(int j = 0; j < 5; j++)
				addSlotToContainer(new SlotModification(modInv, j + i * 5, 80 + j * 18, 20 + (i + 3) * 18));


		//player slots
		for(int i = 0; i < 3; i++)
			for(int j = 0; j < 9; j++)
				addSlotToContainer(new Slot(player, j + i * 9 + 9, 8 + j * 18, 140 + i * 18));

		for(int i = 0; i < 9; i++)
			addSlotToContainer(new Slot(player, i, 8 + i * 18, 198));
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void updateProgressBar(int id, int par)
	{
		switch (id)
		{
			case 0:
				region.setCharge(Float.intBitsToFloat(par));
				break;
			case 1:
				region.setMaxCharge(par);
				break;
		}
	}

	@Override
	public void detectAndSendChanges()
	{
		super.detectAndSendChanges();

		if(crafters.size() > 0)
		{
			ICrafting player = (ICrafting) this.crafters.get(0);
			int charge = Float.floatToRawIntBits((float)region.getCharge());
			if (lastBlockCharge != charge)
			{
				new PacketIntWindowProperty(this, 0, charge).sendTo(player);
				lastBlockCharge = charge;
			}
			charge = region.getMaxCharge();
			if (lastBlockMaxCharge != charge)
			{
				new PacketIntWindowProperty(this, 1, charge).sendTo(player);
				lastBlockMaxCharge = charge;
			}
		}

		ItemStack is = chargeInv.getStackInSlot(0);
		if(is != null && is.getItem() == InitCommon.antimatter)
		{
			if (region.getCharge() <= region.getMaxCharge())
			{
				int toCharge = (int)(region.getMaxCharge() - region.getCharge());
				toCharge -= ItemAntimatter.discharge(is, toCharge, ItemAntimatter.getCharge(is) / 100 + 1);
				region.addCharge(toCharge);
			}
		}
	}

	@Override
	public boolean canInteractWith(EntityPlayer player)
	{
		return region.hasRight(player.getGameProfile(), RegionRights.EDIT_MODULES);
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int index)
	{
		ItemStack ret = null;
		Slot slot = (Slot) this.inventorySlots.get(index);

		if(slot != null && slot.getHasStack())
		{
			ItemStack is = slot.getStack();
			ret = is.copy();

			if(index < 31)
			{
				if(!this.mergeItemStack(is, 31, this.inventorySlots.size(), true))
					return null;
			}
			else
			{
				Item it = is.getItem();
				int start = it == InitCommon.antimatter ? 0 : it == InitCommon.module ? 1 : it instanceof ItemRegionModification ? 16 : 31;
				if(start == 31 || !this.mergeItemStack(is, start, 31, false))
					return null;
			}

			if(is.stackSize == 0)
				slot.putStack(null);
			else
				slot.onSlotChanged();
		}

		return ret;
	}

	@Override
	public ItemStack slotClick(int par1, int par2, int par3, EntityPlayer player)
	{
		return super.slotClick(par1, par2, par3, player);
	}

	@Override
	public void onContainerClosed(EntityPlayer player)
	{
		super.onContainerClosed(player);
		ItemStack is = chargeInv.getStackInSlotOnClosing(0);
		if(is != null)
			player.dropPlayerItemWithRandomChoice(is, false);
		modInv.markDirty();
	}
}
