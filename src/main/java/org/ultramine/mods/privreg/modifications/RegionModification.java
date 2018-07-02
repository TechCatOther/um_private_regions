package org.ultramine.mods.privreg.modifications;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import org.ultramine.mods.privreg.item.ItemRegionModification;

import java.util.List;

public abstract class RegionModification
{
	protected final RegionModificationsRegistry.RegistryItem registryItem;
	protected int power;
	protected int count;

	public RegionModification(int power, int count)
	{
		registryItem = RegionModificationsRegistry.instance().getByModificationClass(getClass());
		this.power = power;
		this.count = count;
	}

	public int getMaxChargeAddition()
	{
		return 0;
	}

	public int getMaxTacktsAddition()
	{
		return 0;
	}

	@SideOnly(Side.CLIENT)
	public abstract void addInformation(ItemStack is, List<String> list);

	public abstract List<ItemStack> getCreativeStackList();

	public ItemStack toItemStack()
	{
		return new ItemStack(registryItem.getItem(), count, power);
	}

	public NBTTagCompound toNBT()
	{
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setByte("i", (byte)registryItem.getId());
		nbt.setShort("p", (short)power);
		nbt.setShort("c", (short) count);
		return nbt;
	}

	public static RegionModification fromNBT(NBTTagCompound nbt)
	{
		int id = nbt.getByte("i");
		RegionModificationsRegistry.RegistryItem item = RegionModificationsRegistry.instance().getById(id);
		try
		{
			return item.getModificationClass().getDeclaredConstructor(int.class, int.class).newInstance(nbt.getShort("p"), nbt.getShort("c"));
		}
		catch(Exception e)
		{
			throw new RuntimeException(e);
		}
	}

	public static RegionModification wrapItemStack(ItemStack is)
	{
		RegionModificationsRegistry.RegistryItem item = ((ItemRegionModification)is.getItem()).getModification();
		try
		{
			return item.getModificationClass().getDeclaredConstructor(int.class, int.class).newInstance(is.getItemDamage(), is.stackSize);
		}
		catch(Exception e)
		{
			throw new RuntimeException(e);
		}
	}
}
