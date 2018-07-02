package org.ultramine.mods.privreg.item;

import com.google.common.primitives.Ints;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;
import org.ultramine.mods.privreg.PrivRegCreativeTab;

import java.util.List;

import static org.ultramine.util.I18n.tlt;

public class ItemAntimatter extends Item
{
	private static final String NBT_MAXCHARGE = "maxcharge";
	private static final String NBT_CHARGE = "charge";

	public static final int[] CAPACITIES = {1000, 5000, 10000, 50000};

	@SideOnly(Side.CLIENT)
	private IIcon itemIcon1;

	public ItemAntimatter()
	{
		setNoRepair();
		setMaxDamage(1000);
		setMaxStackSize(1);
		setUnlocalizedName("um_privreg_antimatter");
		setCreativeTab(PrivRegCreativeTab.instance);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIconFromDamage(int data)
	{
		return data == 1000 ? itemIcon : itemIcon1;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister r)
	{
		itemIcon = r.registerIcon("privreg:am_empty");
		itemIcon1 = r.registerIcon("privreg:am_full");
	}

	@Override
	@SuppressWarnings({"rawtypes", "unchecked"})
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack itemstack, EntityPlayer player, List list, boolean b)
	{
		list.add(tlt("item.um_privreg_antimatter.desc1", getCharge(itemstack), getMaxCharge(itemstack)));
	}

	@Override
	@SideOnly(Side.CLIENT)
	@SuppressWarnings({"rawtypes", "unchecked"})
	public void getSubItems(Item item, CreativeTabs par2CreativeTabs, List list)
	{
		for (int capacity : CAPACITIES)
		{
			ItemStack itemStack = new ItemStack(item, 1, 0);
			setMaxCharge(itemStack, capacity);
			setCharge(itemStack, 0);
			updateItemDamage(itemStack, 0, capacity);
			list.add(itemStack);

			itemStack = new ItemStack(item, 1, 0);
			setMaxCharge(itemStack, capacity);
			setCharge(itemStack, capacity);
			updateItemDamage(itemStack, capacity, capacity);
			list.add(itemStack);
		}
	}

	public static int charge(ItemStack itemStack, int amountToAccept, int speed)
	{
		int currentCharge = getCharge(itemStack);
		int maxCharge = getMaxCharge(itemStack);

		int dCharge = Ints.min(speed, amountToAccept, maxCharge - currentCharge);

		currentCharge += dCharge;
		setCharge(itemStack, currentCharge);
		updateItemDamage(itemStack, currentCharge, maxCharge);
		return amountToAccept - dCharge;
	}

	public static int discharge(ItemStack itemstack, int amountToGive, int speed)
	{
		int currentCharge = getCharge(itemstack);

		int dCharge = Ints.min(speed, currentCharge, amountToGive);

		currentCharge -= dCharge;
		setCharge(itemstack, currentCharge);
		updateItemDamage(itemstack, currentCharge, getMaxCharge(itemstack));
		return amountToGive - dCharge;
	}

	private static void setCharge(ItemStack itemStack, int charge)
	{
		if (itemStack.stackTagCompound == null)
			itemStack.stackTagCompound = new NBTTagCompound();
		itemStack.stackTagCompound.setInteger(NBT_CHARGE, charge);
	}

	public static int getCharge(ItemStack itemStack)
	{
		if (itemStack.stackTagCompound == null || !itemStack.stackTagCompound.hasKey(NBT_CHARGE))
			setCharge(itemStack, 0);
		return itemStack.stackTagCompound.getInteger(NBT_CHARGE);
	}

	private static void setMaxCharge(ItemStack itemStack, int maxCharge)
	{
		if (itemStack.stackTagCompound == null)
			itemStack.stackTagCompound = new NBTTagCompound();
		itemStack.stackTagCompound.setInteger(NBT_MAXCHARGE, maxCharge);
	}

	public static int getMaxCharge(ItemStack itemStack)
	{
		if (itemStack.stackTagCompound == null || !itemStack.stackTagCompound.hasKey(NBT_MAXCHARGE))
			setMaxCharge(itemStack, 1000);
		return itemStack.stackTagCompound.getInteger(NBT_MAXCHARGE);
	}

	private static void updateItemDamage(ItemStack itemStack, int currentCharge, int maxCharge)
	{
		itemStack.setItemDamage(itemStack.getMaxDamage() - (int) ((float) currentCharge / maxCharge * itemStack.getMaxDamage()));
	}
}
