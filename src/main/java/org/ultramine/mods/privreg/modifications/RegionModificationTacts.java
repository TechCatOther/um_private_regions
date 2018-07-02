package org.ultramine.mods.privreg.modifications;

import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

import static org.ultramine.util.I18n.tlt;

public class RegionModificationTacts extends RegionModification
{
	private static final int[] DEFAULT_TACTS = {1, 2, 4, 8, 16, 32, 64, 128};

	public RegionModificationTacts(int power, int count)
	{
		super(power, count);
	}

	public int getMaxTacktsAddition()
	{
		return power*count;
	}

	@Override
	public void addInformation(ItemStack is, List<String> list)
	{
		list.add(tlt("item.um_privreg_modification.tacts.desc1"));
		list.add(tlt("item.um_privreg_modification.tacts.desc2", is.getItemDamage()));
	}

	@Override
	public List<ItemStack> getCreativeStackList()
	{
		List<ItemStack> list = new ArrayList<ItemStack>(DEFAULT_TACTS.length);
		for (int tacts : DEFAULT_TACTS)
			list.add(new ItemStack(registryItem.getItem(), 1, tacts));
		return list;
	}
}
