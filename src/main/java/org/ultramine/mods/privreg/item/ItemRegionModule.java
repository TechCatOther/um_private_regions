package org.ultramine.mods.privreg.item;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import org.ultramine.mods.privreg.PrivRegCreativeTab;
import org.ultramine.mods.privreg.modules.RegionModule;
import org.ultramine.mods.privreg.modules.RegionModulesRegistry;

import java.util.List;

public class ItemRegionModule extends Item
{
	private static final RegionModulesRegistry modulesRegistry = RegionModulesRegistry.instance();
	private IIcon[] icons = new IIcon[16];

	public ItemRegionModule()
	{
		setNoRepair();
		setHasSubtypes(true);
		setMaxDamage(0);
		setMaxStackSize(1);

		setUnlocalizedName("um_privreg_module");
		setTextureName("privreg:module");
		setCreativeTab(PrivRegCreativeTab.instance);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIconFromDamage(int data)
	{
		return icons[data % 16];
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister r)
	{
		itemIcon = r.registerIcon("privreg:module");
		for(int i = 0; i < 16; i++)
			icons[i] = r.registerIcon("privreg:module_"+i);
	}

	@Override
	public String getUnlocalizedName(ItemStack par1ItemStack)
	{
		return "item.privreg.module."+modulesRegistry.getById(par1ItemStack.getItemDamage()).getName();
	}

	@Override
	@SideOnly(Side.CLIENT)
	@SuppressWarnings({"rawtypes", "unchecked"})
	public void addInformation(ItemStack is, EntityPlayer par2EntityPlayer, List list, boolean par4)
	{
		list.addAll(RegionModule.wrapItemStack(is).getDisplayDesc());
	}

	@Override
	@SideOnly(Side.CLIENT)
	@SuppressWarnings({"rawtypes", "unchecked"})
	public void getSubItems(Item item, CreativeTabs par2CreativeTabs, List list)
	{
		for (int i = 0; i < modulesRegistry.getModulesCount(); i++)
			if (modulesRegistry.getById(i) != null)
				list.add(new ItemStack(this, 1, i));
	}
}
