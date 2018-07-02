package org.ultramine.mods.privreg.item;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.ultramine.mods.privreg.PrivRegCreativeTab;
import org.ultramine.mods.privreg.modifications.RegionModification;
import org.ultramine.mods.privreg.modifications.RegionModificationsRegistry;

import java.util.List;

public class ItemRegionModification extends Item
{
	private final RegionModificationsRegistry.RegistryItem modification;

	public ItemRegionModification(RegionModificationsRegistry.RegistryItem modification)
	{
		this.modification = modification;
		modification.item = this;
		setNoRepair();
		setHasSubtypes(true);
		setMaxDamage(0);

		setUnlocalizedName("um_privreg_modification");
		setTextureName("privreg:module");
		setCreativeTab(PrivRegCreativeTab.instance);
	}

	@Override
	public String getUnlocalizedName(ItemStack is)
	{
		return super.getUnlocalizedName(is) + "." + modification.getName();
	}

	@Override
	public String getItemStackDisplayName(ItemStack is)
	{
		return super.getItemStackDisplayName(is) + " " + is.getItemDamage();
	}

	public RegionModificationsRegistry.RegistryItem getModification()
	{
		return modification;
	}

	@Override
	@SideOnly(Side.CLIENT)
	@SuppressWarnings({"rawtypes", "unchecked"})
	public void addInformation(ItemStack is, EntityPlayer par2EntityPlayer, List list, boolean par4)
	{
		RegionModification.wrapItemStack(is).addInformation(is, list);
	}

	@Override
	@SideOnly(Side.CLIENT)
	@SuppressWarnings({"rawtypes", "unchecked"})
	public void getSubItems(Item item, CreativeTabs par2CreativeTabs, List list)
	{
		list.addAll(RegionModification.wrapItemStack(new ItemStack(item, 1, 1)).getCreativeStackList());
	}
}
