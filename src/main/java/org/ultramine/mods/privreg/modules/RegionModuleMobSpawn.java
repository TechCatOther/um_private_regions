package org.ultramine.mods.privreg.modules;

import cpw.mods.fml.common.eventhandler.Event;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import org.ultramine.mods.privreg.gui.GuiRegionModules;
import org.ultramine.mods.privreg.gui.modules.GuiModuleMobSpawn;
import org.ultramine.mods.privreg.gui.modules.GuiModuleSettings;

public class RegionModuleMobSpawn extends RegionModule
{
	private static final String NBT_PROHIBIT_ANIMALS = "proh_animals";
	private static final String NBT_PROHIBIT_MONSTERS = "proh_monsters";

	private boolean prohibitAnimals = false;
	private boolean prohibitMonsters = true;

	public boolean isProhibitAnimals()
	{
		return prohibitAnimals;
	}

	public void setProhibitAnimals(boolean prohibitAnimals)
	{
		this.prohibitAnimals = prohibitAnimals;
	}

	public boolean isProhibitMonsters()
	{
		return prohibitMonsters;
	}

	public void setProhibitMonsters(boolean prohibitMonsters)
	{
		this.prohibitMonsters = prohibitMonsters;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public GuiModuleSettings createGuiHandler(GuiRegionModules parent)
	{
		return new GuiModuleMobSpawn(parent, this);
	}

	@Override
	protected void writeToNBT(NBTTagCompound nbt)
	{
		nbt.setBoolean(NBT_PROHIBIT_ANIMALS, prohibitAnimals);
		nbt.setBoolean(NBT_PROHIBIT_MONSTERS, prohibitMonsters);
	}

	@Override
	protected void readFromNBT(NBTTagCompound nbt)
	{
		if(nbt.hasKey(NBT_PROHIBIT_ANIMALS))
			prohibitAnimals = nbt.getBoolean(NBT_PROHIBIT_ANIMALS);
		if(nbt.hasKey(NBT_PROHIBIT_MONSTERS))
			prohibitMonsters = nbt.getBoolean(NBT_PROHIBIT_MONSTERS);
	}

	public void onLivingSpawn(LivingSpawnEvent.CheckSpawn e)
	{
		if(e.entity.isEntityAnimal() && prohibitAnimals)
			e.setResult(Event.Result.DENY);
		else if(e.entity.isEntityMonster() && prohibitMonsters)
			e.setResult(Event.Result.DENY);
	}
}
