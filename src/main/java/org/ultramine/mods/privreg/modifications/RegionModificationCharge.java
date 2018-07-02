package org.ultramine.mods.privreg.modifications;

import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

import static org.ultramine.util.I18n.tlt;

public class RegionModificationCharge extends RegionModification
{
	private static final int[] DEFAULT_CHARGES = {1000, 2000, 4000, 8000};

	public RegionModificationCharge(int power, int count)
	{
		super(power, count);
	}

	public int getMaxChargeAddition()
	{
		return power*count;
	}

	@Override
	public void addInformation(ItemStack is, List<String> list)
	{
		list.add(tlt("item.um_privreg_modification.charge.desc1"));
		list.add(tlt("item.um_privreg_modification.charge.desc2", is.getItemDamage()));
	}

	@Override
	public List<ItemStack> getCreativeStackList()
	{
		List<ItemStack> list = new ArrayList<ItemStack>(DEFAULT_CHARGES.length);
		for (int charge : DEFAULT_CHARGES)
			list.add(new ItemStack(registryItem.getItem(), 1, charge));
		return list;
	}
}
