package org.ultramine.mods.privreg.gui.inv;

import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import org.ultramine.mods.privreg.InitCommon;
import org.ultramine.mods.privreg.modules.RegionModule;
import org.ultramine.mods.privreg.modules.RegionModulesRegistry;

public class SlotModule extends Slot
{
	private static final RegionModulesRegistry REGISTRY = RegionModulesRegistry.instance();
	private final ModulesInventory inv;
	private int lastModule = -1;

	public SlotModule(ModulesInventory inv, int index, int x, int y)
	{
		super(inv, index, x, y);
		this.inv = inv;
		lastModule = inv.getStackInSlot(index) == null ? -1 : inv.getStackInSlot(index).getItemDamage();
	}

	@Override
	public boolean isItemValid(ItemStack itemstack)
	{
		return itemstack.getItem() == InitCommon.module && !inv.getRegion().hasModuleWithRegistryId(itemstack.getItemDamage());
	}

	@Override
	public void onSlotChanged()
	{
		if(!inv.getRegion().isServer())
			return;

		int currentModule = getStack() == null || getStack().getItem() != InitCommon.module ? -1 : getStack().getItemDamage();

		if(lastModule != currentModule)
		{
			if(lastModule != -1 && lastModule < REGISTRY.getModulesCount() && REGISTRY.getById(lastModule) != null)
				inv.getRegion().removeModule(lastModule);

			if(currentModule != -1 && currentModule < REGISTRY.getModulesCount() && REGISTRY.getById(currentModule) != null)
				inv.getRegion().addModule(RegionModule.wrapItemStack(getStack()));

			lastModule = currentModule;
			inv.getRegion().setChanged(true);
			super.onSlotChanged();
		}
	}
}
