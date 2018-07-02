package org.ultramine.mods.privreg.gui.modules;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import org.ultramine.gui.ElementLabel;
import org.ultramine.gui.ElementTextField;
import org.ultramine.mods.privreg.gui.GuiRegionModules;
import org.ultramine.mods.privreg.modules.RegionModule;
import org.ultramine.mods.privreg.modules.RegionModuleDiscount;

import static org.ultramine.util.I18n.tlt;

@SideOnly(Side.CLIENT)
public class GuiModuleDiscount extends GuiModuleSettings
{
	private RegionModuleDiscount regionModule;

	private ElementTextField strDiscount;
	private ElementTextField strComment;

	public GuiModuleDiscount(GuiRegionModules p, RegionModuleDiscount regionModule)
	{
		super(p);
		this.regionModule = regionModule;
	}

	@Override
	public RegionModule getModule()
	{
		return regionModule;
	}

	@Override
	protected void onClose()
	{
		int discount = 0;
		try
		{
			discount = Integer.parseInt(strDiscount.getText());
		} catch (NumberFormatException e) {}
		regionModule.setDiscount(discount);
		regionModule.setComment(strComment.getText());
	}

	@Override
	public void addElements()
	{
		addElement(new ElementLabel(10, 20, tlt("privreg.gui.module.discount.line1")));
		addElement(new ElementLabel(10, 30, tlt("privreg.gui.module.discount.line2")));
		addElement(strDiscount = new ElementTextField(-1, 50, 30, 120, 10));
		addElement(new ElementLabel(10, 40, tlt("privreg.gui.module.discount.line3")));
		addElement(strComment = new ElementTextField(-1, 10, 50, 160, 10));
		addElement(new ElementLabel(10, 60, tlt("privreg.gui.module.discount.line4")));

		strDiscount.setText(Integer.toString(regionModule.getDiscount()));
		strComment.setText(regionModule.getComment());
	}
}
