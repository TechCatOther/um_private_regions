package org.ultramine.mods.privreg.gui.modules;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import org.ultramine.gui.ElementCheckBox;
import org.ultramine.mods.privreg.gui.GuiRegionModules;
import org.ultramine.mods.privreg.modules.RegionModule;
import org.ultramine.mods.privreg.modules.RegionModuleBarrier;

import static org.ultramine.util.I18n.tlt;

@SideOnly(Side.CLIENT)
public class GuiModuleBarrier extends GuiModuleSettings
{
	private final RegionModuleBarrier module;

	private ElementCheckBox cbOpaque;

	public GuiModuleBarrier(GuiRegionModules p, RegionModuleBarrier module)
	{
		super(p);
		this.module = module;
	}

	@Override
	public RegionModule getModule()
	{
		return this.module;
	}

	@Override
	protected void onClose()
	{
		module.setOpaque(cbOpaque.isChecked());
	}

	@Override
	protected void addElements()
	{
		addElement(cbOpaque = new ElementCheckBox(-1, 10, 20, tlt("privreg.gui.module.barrier.opaque")));

		cbOpaque.setChecked(module.isOpaque());
	}
}
