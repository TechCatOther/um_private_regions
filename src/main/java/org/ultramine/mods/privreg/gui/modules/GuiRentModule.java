package org.ultramine.mods.privreg.gui.modules;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import org.ultramine.gui.ElementButtonFixable;
import org.ultramine.gui.ElementCheckBox;
import org.ultramine.gui.ElementLabel;
import org.ultramine.gui.ElementTextField;
import org.ultramine.gui.IGuiElement;
import org.ultramine.mods.privreg.gui.GuiRegionModules;
import org.ultramine.mods.privreg.modules.RegionModule;
import org.ultramine.mods.privreg.modules.RegionModuleRent;

import static org.ultramine.mods.privreg.modules.RegionModuleRent.RentMode.RENT;
import static org.ultramine.mods.privreg.modules.RegionModuleRent.RentMode.SELL;

@SideOnly(Side.CLIENT)
public class GuiRentModule extends GuiModuleSettings
{
	private final RegionModuleRent module;

	private ElementButtonFixable mode;
	private ElementTextField tfName;
	private ElementTextField tfRentalFee;
	private ElementTextField tfMaxDays;
	private ElementCheckBox cbAllowMultiple;
	private ElementTextField tfSellPrice;

	public GuiRentModule(GuiRegionModules p, RegionModuleRent module)
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
		module.setMode(mode.isPressed() ? RENT : SELL);
		module.setAreaName(tfName.getText());
		if(module.getMode() == RENT)
		{
			module.setRentalFee(tfRentalFee.getAsDoubleOr(0));
			module.setMaxDays(tfMaxDays.getAsIntegerOr(0));
			module.setAllowMultiple(cbAllowMultiple.isChecked());
		}
		else
		{
			module.setSellPrice(tfSellPrice.getAsDoubleOr(0));
		}
	}

	@Override
	public void actionPerformed(int id, IGuiElement element, Object... data)
	{
		super.actionPerformed(id, element, data);
		if(id == 10 || id == 11)
		{
			module.setMode(mode.isPressed() ? RENT : SELL);
			relayout();
		}
	}

	@Override
	protected void addElements()
	{
		mode = new ElementButtonFixable(10, 10, 20, 79, 12, "Аренда");
		ElementButtonFixable mode1 = new ElementButtonFixable(11, 91, 20, 79, 12, "Продажа");
		addElement(mode);
		addElement(mode1);
		mode.linkTo(mode1);
		if(module.getMode() == RENT)
			mode.setPressed();
		else
			mode1.setPressed();

		addElement(new ElementLabel(10, 40, "Название участка:", 0x000000));
		addElement(tfName = new ElementTextField(-1, 80, 40, 90, 9));
		tfName.setText(module.getAreaName());

		if(module.getMode() == RENT)
		{
			addElement(new ElementLabel(10, 50, "Арендная плата $/сутки:", 0x000000));
			addElement(tfRentalFee = new ElementTextField(-1, 100, 50, 70, 9));

			addElement(new ElementLabel(10, 60, "MAX кол-во дней аренды:", 0x000000));
			addElement(tfMaxDays = new ElementTextField(-1, 100, 60, 70, 9));

			addElement(cbAllowMultiple = new ElementCheckBox(-1, 10, 70, "Разрешить аренду нескольким игрокам"));

			tfRentalFee.setFilterString("0123456789.");
			tfMaxDays.setFilterString("0123456789");

			tfRentalFee.setText(Double.toString(module.getRentalFee()));
			tfMaxDays.setText(Integer.toString(module.getMaxDays()));
			cbAllowMultiple.setChecked(module.isAllowMultiple());
		}
		else
		{
			addElement(new ElementLabel(10, 50, "Стоимость участка $:", 0x000000));
			addElement(tfSellPrice = new ElementTextField(-1, 100, 50, 70, 9));

			tfSellPrice.setFilterString("0123456789.");

			tfSellPrice.setText(Double.toString(module.getSellPrice()));
		}
	}
}
