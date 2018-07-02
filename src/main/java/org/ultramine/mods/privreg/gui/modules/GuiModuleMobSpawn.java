package org.ultramine.mods.privreg.gui.modules;

import org.ultramine.gui.ElementCheckBox;
import org.ultramine.mods.privreg.gui.GuiRegionModules;
import org.ultramine.mods.privreg.modules.RegionModule;
import org.ultramine.mods.privreg.modules.RegionModuleMobSpawn;

import static org.ultramine.util.I18n.tlt;

public class GuiModuleMobSpawn extends GuiModuleSettings
{
	private final RegionModuleMobSpawn module;

	private ElementCheckBox cbAnimals;
	private ElementCheckBox cbMonsters;

	public GuiModuleMobSpawn(GuiRegionModules p, RegionModuleMobSpawn module)
	{
		super(p);
		this.module = module;
	}

	@Override
	public RegionModule getModule()
	{
		return module;
	}

	@Override
	protected void onClose()
	{
		module.setProhibitAnimals(cbAnimals.isChecked());
		module.setProhibitMonsters(cbMonsters.isChecked());
	}

	@Override
	protected void addElements()
	{
		addElement(cbAnimals = new ElementCheckBox(-1, 10, 20, tlt("privreg.gui.module.mobspawn.animals")));
		addElement(cbMonsters = new ElementCheckBox(-1, 10, 30, tlt("privreg.gui.module.mobspawn.monsters")));

		cbAnimals.setChecked(module.isProhibitAnimals());
		cbMonsters.setChecked(module.isProhibitMonsters());
	}
}
