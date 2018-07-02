package org.ultramine.mods.privreg;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

public class PrivRegCreativeTab extends CreativeTabs
{
	public static final PrivRegCreativeTab instance = new PrivRegCreativeTab();

	public PrivRegCreativeTab()
	{
		super("um_privreg");
	}

	@Override
	public Item getTabIconItem()
	{
		return Item.getItemFromBlock(InitCommon.region);
	}
}
